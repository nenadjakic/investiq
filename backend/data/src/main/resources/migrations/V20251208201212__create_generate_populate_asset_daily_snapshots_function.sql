CREATE OR REPLACE FUNCTION populate_asset_daily_snapshots(
    p_snapshot_date date
)
RETURNS void
LANGUAGE plpgsql
AS $$
BEGIN
    -- ============================================================================
    -- STEP 1: Kreiraj TEMP tablicu s FIFO cost basis za sve SELL transakcije
    -- ============================================================================

    CREATE TEMP TABLE temp_fifo_cost_basis AS
    WITH
    -- Sve kupovine s running total
    buys AS (
        SELECT
            t.transaction_id,
            t.asset_id,
            t.transaction_platform AS platform,
            t.transaction_date,
            t.quantity,
            t.transaction_value_eur,
            t.fee_amount_eur,
            -- Running sum koliko smo ukupno kupili do sad
            SUM(t.quantity) OVER (
                PARTITION BY t.asset_id, t.transaction_platform
                ORDER BY t.transaction_date, t.transaction_id
                ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
            ) AS cumulative_buy_qty,
            -- Running sum do prethodne transakcije
            COALESCE(
                SUM(t.quantity) OVER (
                    PARTITION BY t.asset_id, t.transaction_platform
                    ORDER BY t.transaction_date, t.transaction_id
                    ROWS BETWEEN UNBOUNDED PRECEDING AND 1 PRECEDING
                ),
                0
            ) AS prev_cumulative_buy_qty
        FROM vw_transaction_analytics t
        WHERE t.transaction_type = 'BUY'
          AND t.transaction_date::date <= p_snapshot_date
    ),

    -- Sve prodaje s running total
    sells AS (
        SELECT
            t.transaction_id,
            t.asset_id,
            t.transaction_platform AS platform,
            t.transaction_date,
            t.quantity,
            t.transaction_value_eur,
            t.fee_amount_eur,
            -- Running sum koliko smo ukupno prodali do sad
            SUM(t.quantity) OVER (
                PARTITION BY t.asset_id, t.transaction_platform
                ORDER BY t.transaction_date, t.transaction_id
                ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
            ) AS cumulative_sell_qty,
            -- Running sum do prethodne transakcije
            COALESCE(
                SUM(t.quantity) OVER (
                    PARTITION BY t.asset_id, t.transaction_platform
                    ORDER BY t.transaction_date, t.transaction_id
                    ROWS BETWEEN UNBOUNDED PRECEDING AND 1 PRECEDING
                ),
                0
            ) AS prev_cumulative_sell_qty
        FROM vw_transaction_analytics t
        WHERE t.transaction_type = 'SELL'
          AND t.transaction_date::date <= p_snapshot_date
    ),

    -- FIFO matching: za svaku prodaju, pronađi koje kupovine "pokrivaju" tu prodaju
    fifo_matches AS (
        SELECT
            s.transaction_id AS sell_id,
            s.asset_id,
            s.platform,
            s.transaction_date AS sell_date,
            s.quantity AS sell_qty,
            ABS(s.transaction_value_eur) AS sell_value,
            s.fee_amount_eur AS sell_fee,
            b.transaction_id AS buy_id,
            b.transaction_date AS buy_date,
            b.quantity AS buy_qty,
            b.transaction_value_eur AS buy_value,
            b.fee_amount_eur AS buy_fee,
            b.cumulative_buy_qty,
            b.prev_cumulative_buy_qty,
            s.prev_cumulative_sell_qty,

            -- Izračunaj koliko od ove kupovine ide u ovu prodaju (FIFO logika)
            LEAST(
                -- Koliko je još ostalo od ove kupovine
                b.cumulative_buy_qty - GREATEST(b.prev_cumulative_buy_qty, s.prev_cumulative_sell_qty),
                -- Koliko još treba "pokriti" od ove prodaje
                s.prev_cumulative_sell_qty + s.quantity - GREATEST(b.prev_cumulative_buy_qty, s.prev_cumulative_sell_qty)
            ) AS matched_qty

        FROM sells s
        CROSS JOIN buys b
        WHERE b.asset_id = s.asset_id
          AND b.platform = s.platform
          AND b.transaction_date <= s.transaction_date
          -- Ova kupovina još ima "dostupne" dionice u trenutku prodaje
          AND b.cumulative_buy_qty > s.prev_cumulative_sell_qty
          -- Ova prodaja još nije potpuno "pokrivena" s prethodnim kupovinama
          AND s.prev_cumulative_sell_qty < b.cumulative_buy_qty
    ),

    -- Agregiraj cost basis za svaku prodaju
    sell_cost_basis AS (
        SELECT
            fm.sell_id,
            fm.asset_id,
            fm.platform,
            fm.sell_value,
            fm.sell_fee,
            -- Cost basis = suma (matched_qty * cijena po dionici iz kupovine + proporcionalni fees)
            SUM(
                fm.matched_qty * (
                    (fm.buy_value + COALESCE(fm.buy_fee, 0)) / NULLIF(fm.buy_qty, 0)
                )
            ) AS cost_basis_eur
        FROM fifo_matches fm
        WHERE fm.matched_qty > 0
        GROUP BY fm.sell_id, fm.asset_id, fm.platform, fm.sell_value, fm.sell_fee
    )

    -- Konačna temp tablica: realized P&L za svaku prodaju
    SELECT
        scb.sell_id AS transaction_id,
        scb.asset_id,
        scb.platform,
        scb.cost_basis_eur,
        -- Realized P&L = prodajna cijena - cost basis - sell fees
        (scb.sell_value - scb.cost_basis_eur - COALESCE(scb.sell_fee, 0)) AS realized_pl_eur
    FROM sell_cost_basis scb;


    -- ============================================================================
    -- STEP 2: INSERT u asset_daily_snapshots koristeći TEMP tablicu
    -- ============================================================================

INSERT INTO asset_daily_snapshots (
        snapshot_date,
        asset_id,
        platform,
        quantity,
        avg_cost_per_share_eur,
        cost_basis_eur,
        market_price_eur,
        market_value_eur,
        unrealized_pl_eur,
        realized_pl_eur,
        total_buy_qty,
        total_buy_amount_eur,
        total_sell_qty,
        total_sell_amount_eur,
        total_dividends_eur,
        total_fees_eur
    )
    SELECT
        p_snapshot_date AS snapshot_date,
        t.asset_id,
        t.transaction_platform AS platform,
        SUM(
            CASE WHEN t.transaction_type = 'BUY' THEN COALESCE(t.quantity,0)
                 WHEN t.transaction_type = 'SELL' THEN -COALESCE(t.quantity,0)
                 ELSE 0 END
        ) AS quantity,
        CASE
            WHEN SUM(CASE WHEN t.transaction_type = 'BUY' THEN COALESCE(t.quantity,0) ELSE 0 END) = 0 THEN NULL
            ELSE SUM(CASE WHEN t.transaction_type = 'BUY' THEN t.transaction_value_eur + t.fee_amount_eur ELSE 0 END)
                 / SUM(CASE WHEN t.transaction_type = 'BUY' THEN COALESCE(t.quantity,0) ELSE 0 END)
        END AS avg_cost_per_share_eur,
        SUM(CASE WHEN t.transaction_type='BUY' THEN t.transaction_value_eur + t.fee_amount_eur ELSE 0 END) AS cost_basis_eur,
        mp.market_price_eur,
        (mp.market_price_eur * SUM(
            CASE WHEN t.transaction_type = 'BUY' THEN COALESCE(t.quantity,0)
                 WHEN t.transaction_type = 'SELL' THEN -COALESCE(t.quantity,0)
                 ELSE 0 END
        ))::numeric(36,8) AS market_value_eur,
        ((mp.market_price_eur * SUM(
            CASE WHEN t.transaction_type = 'BUY' THEN COALESCE(t.quantity,0)
                 WHEN t.transaction_type = 'SELL' THEN -COALESCE(t.quantity,0)
                 ELSE 0 END
        )) - SUM(CASE WHEN t.transaction_type='BUY' THEN COALESCE(t.transaction_value_eur,0) ELSE 0 END))::numeric(36,8) AS unrealized_pl_eur,

		COALESCE(SUM(tcb.realized_pl_eur), 0)::numeric(36,8) AS realized_pl_eur,
        SUM(CASE WHEN t.transaction_type='BUY' THEN COALESCE(t.quantity,0) ELSE 0 END) AS total_buy_qty,
        SUM(CASE WHEN t.transaction_type='BUY' THEN t.transaction_value_eur + t.fee_amount_eur ELSE 0 END) AS total_buy_amount_eur,
        SUM(CASE WHEN t.transaction_type='SELL' THEN COALESCE(t.quantity,0) ELSE 0 END) AS total_sell_qty,
        SUM(CASE WHEN t.transaction_type='SELL' THEN t.transaction_value_eur + t.fee_amount_eur ELSE 0 END) AS total_sell_amount_eur,
        SUM(CASE WHEN t.transaction_type IN ('DIVIDEND','DIVIDEND_ADJUSTMENT') THEN t.transaction_value_eur ELSE 0 END) AS total_dividends_eur,
        SUM(CASE WHEN t.transaction_type='FEE' THEN t.transaction_value_eur ELSE 0 END) AS total_fees_eur
    FROM vw_transaction_analytics t
    LEFT JOIN temp_fifo_cost_basis tcb
        ON tcb.transaction_id = t.transaction_id
        AND tcb.asset_id = t.asset_id
        AND tcb.platform = t.transaction_platform
    LEFT JOIN assets a ON t.asset_id = a.id
    LEFT JOIN LATERAL (
        SELECT
            CASE
                WHEN a.currency_code = 'GBX' THEN ah.close_price / 100.0
                ELSE ah.close_price
            END *
            CASE
                WHEN CASE
                    WHEN a.currency_code = 'GBX' THEN 'GBP'
                    ELSE a.currency_code
                END = 'EUR' THEN 1.0
                ELSE COALESCE(
                    ch_from.exchange_rate,
                    CASE
                        WHEN ch_to.exchange_rate IS NOT NULL THEN 1.0 / ch_to.exchange_rate
                        ELSE NULL
                    END
                )
            END AS market_price_eur
        FROM asset_histories ah
        LEFT JOIN LATERAL (
            SELECT ch.exchange_rate
            FROM currency_histories ch
            WHERE ch.from_currency_code = CASE
                    WHEN a.currency_code = 'GBX' THEN 'GBP'
                    ELSE a.currency_code
                END
              AND ch.to_currency_code = 'EUR'
              AND ch.valid_date <= p_snapshot_date
            ORDER BY ch.valid_date DESC
            LIMIT 1
        ) ch_from ON CASE
                WHEN a.currency_code = 'GBX' THEN 'GBP'
                ELSE a.currency_code
            END <> 'EUR'
        LEFT JOIN LATERAL (
            SELECT ch.exchange_rate
            FROM currency_histories ch
            WHERE ch.from_currency_code = 'EUR'
              AND ch.to_currency_code = CASE
                    WHEN a.currency_code = 'GBX' THEN 'GBP'
                    ELSE a.currency_code
                END
              AND ch.valid_date <= p_snapshot_date
            ORDER BY ch.valid_date DESC
            LIMIT 1
        ) ch_to ON CASE
                WHEN a.currency_code = 'GBX' THEN 'GBP'
                ELSE a.currency_code
            END <> 'EUR'
        WHERE ah.asset_id = t.asset_id
          AND ah.valid_date <= p_snapshot_date
        ORDER BY ah.valid_date DESC
        LIMIT 1
    ) mp ON TRUE
    WHERE t.transaction_date::date <= p_snapshot_date
        AND t.transaction_type NOT IN ('DEPOSIT', 'WITHDRAW', 'FEE', 'DIVIDEND_ADJUSTMENT')
    GROUP BY t.asset_id, t.transaction_platform, mp.market_price_eur
    ON CONFLICT (snapshot_date, asset_id, platform)
    DO UPDATE SET
        quantity = EXCLUDED.quantity,
        avg_cost_per_share_eur = EXCLUDED.avg_cost_per_share_eur,
        cost_basis_eur = EXCLUDED.cost_basis_eur,
        market_price_eur = EXCLUDED.market_price_eur,
        market_value_eur = EXCLUDED.market_value_eur,
        unrealized_pl_eur = EXCLUDED.unrealized_pl_eur,
        realized_pl_eur = EXCLUDED.realized_pl_eur,
        total_buy_qty = EXCLUDED.total_buy_qty,
        total_buy_amount_eur = EXCLUDED.total_buy_amount_eur,
        total_sell_qty = EXCLUDED.total_sell_qty,
        total_sell_amount_eur = EXCLUDED.total_sell_amount_eur,
        total_dividends_eur = EXCLUDED.total_dividends_eur,
        total_fees_eur = EXCLUDED.total_fees_eur;

    -- ============================================================================
    -- STEP 3: Cleanup temp table
    -- ============================================================================
    DROP TABLE IF EXISTS temp_fifo_cost_basis;

END;
$$;