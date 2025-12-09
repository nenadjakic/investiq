CREATE OR REPLACE FUNCTION populate_asset_daily_snapshots(
    p_snapshot_date date
)
RETURNS void
LANGUAGE plpgsql
AS $$
BEGIN
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
        (SUM(CASE WHEN t.transaction_type='SELL' THEN COALESCE(t.transaction_value_eur,0) ELSE 0 END)
         - (SUM(CASE WHEN t.transaction_type='SELL' THEN COALESCE(t.quantity,0) ELSE 0 END)
            * (SUM(CASE WHEN t.transaction_type='BUY' THEN COALESCE(t.transaction_value_eur,0) ELSE 0 END)
               / NULLIF(SUM(CASE WHEN t.transaction_type='BUY' THEN COALESCE(t.quantity,0) ELSE 0 END),0))
           )
        )::numeric(36,8) AS realized_pl_eur,
        SUM(CASE WHEN t.transaction_type='BUY' THEN COALESCE(t.quantity,0) ELSE 0 END) AS total_buy_qty,
        SUM(CASE WHEN t.transaction_type='BUY' THEN t.transaction_value_eur + t.fee_amount_eur ELSE 0 END) AS total_buy_amount_eur,
        SUM(CASE WHEN t.transaction_type='SELL' THEN COALESCE(t.quantity,0) ELSE 0 END) AS total_sell_qty,
        SUM(CASE WHEN t.transaction_type='SELL' THEN t.transaction_value_eur + t.fee_amount_eur ELSE 0 END) AS total_sell_amount_eur,
        SUM(CASE WHEN t.transaction_type IN ('DIVIDEND','DIVIDEND_ADJUSTMENT') THEN t.transaction_value_eur ELSE 0 END) AS total_dividends_eur,
        SUM(CASE WHEN t.transaction_type='FEE' THEN t.transaction_value_eur ELSE 0 END) AS total_fees_eur
    FROM vw_transaction_analytics t
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
END;
$$;