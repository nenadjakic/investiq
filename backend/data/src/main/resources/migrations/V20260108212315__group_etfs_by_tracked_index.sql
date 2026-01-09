CREATE OR REPLACE FUNCTION public.get_latest_portfolio_holdings_grouped(
    p_platform text DEFAULT NULL
)
RETURNS TABLE (
    snapshot_date DATE,
    holding_id UUID,
    holding_name TEXT,
    cost_basis_eur NUMERIC,
    market_value_eur NUMERIC,
    unrealized_pl_eur NUMERIC,
    tickers TEXT[]
)
LANGUAGE sql
STABLE
AS $$
   WITH latest AS (SELECT MAX(snapshot_date) AS d FROM asset_daily_snapshots)
    SELECT
        l.d AS snapshot_date,
        CASE
            WHEN a.asset_type = 'ETF' AND a.tracked_index_id IS NOT NULL THEN a.tracked_index_id
            WHEN a.asset_type = 'ETF' THEN a.id
            ELSE a.company_id
        END AS holding_id,
        CASE
            WHEN a.asset_type = 'ETF' AND a.tracked_index_id IS NOT NULL THEN idx.name
            WHEN a.asset_type = 'ETF' THEN a.name
            ELSE c.name
        END AS holding_name,

        SUM(s.cost_basis_eur) AS cost_basis_eur,
        SUM(s.market_value_eur) AS market_value_eur,
        SUM(s.unrealized_pl_eur) AS unrealized_pl_eur,
        ARRAY_AGG(DISTINCT a.symbol ORDER BY a.symbol) AS tickers
    FROM asset_daily_snapshots s
             JOIN latest l ON s.snapshot_date = l.d
             JOIN assets a ON s.asset_id = a.id
             LEFT JOIN companies c ON a.company_id = c.id
             LEFT JOIN assets idx ON a.tracked_index_id = idx.id
    WHERE s.quantity <> 0
        AND (p_platform IS NULL OR s.platform = p_platform)
    GROUP BY l.d,  holding_id, holding_name
    ORDER BY SUM(COALESCE(s.market_value_eur,0)) desc
$$;

CREATE OR REPLACE FUNCTION public.get_latest_holding_dividend_performance(
    p_platform text DEFAULT NULL
)
RETURNS TABLE (
    holding_id UUID,
    holding_name TEXT,
    total_dividend_eur NUMERIC(36,8),
    total_cost_basis_eur NUMERIC(36,8),
    total_annualized_dividend NUMERIC(36,8),
    dividend_cost_yield NUMERIC(36,8),
    days_held INTEGER
)
LANGUAGE sql
STABLE
AS $$
    WITH latest AS (
        SELECT MAX(snapshot_date) AS d
        FROM asset_daily_snapshots
    ),
    first_buy AS (
        SELECT
            asset_id,
            MIN(transaction_date_only) AS first_buy_date
        FROM vw_transaction_analytics
        WHERE transaction_type = 'BUY'
            AND (p_platform IS NULL OR transaction_platform = p_platform)
        GROUP BY asset_id
    ),
    total_dividends AS (
        SELECT
            t.asset_id,
            SUM(t.transaction_value_eur) AS total_dividend_eur
        FROM vw_transaction_analytics t
        JOIN first_buy fb ON t.asset_id = fb.asset_id
        WHERE t.transaction_type = 'DIVIDEND'
            AND t.transaction_date_only >= fb.first_buy_date
            AND (p_platform IS NULL OR t.transaction_platform = p_platform)
        GROUP BY t.asset_id
    ),
    annualized_dividend_per_asset AS (
        SELECT
            s.asset_id,
            CASE
                WHEN a.asset_type = 'ETF' AND a.tracked_index_id IS NOT NULL THEN a.tracked_index_id
                WHEN a.asset_type = 'ETF' THEN a.id
                ELSE a.company_id
            END AS holding_id,
            SUM(s.cost_basis_eur) AS cost_basis_eur,
            COALESCE(d.total_dividend_eur,0) AS total_dividend_eur,
            fb.first_buy_date,
            CASE
                WHEN (CURRENT_DATE - fb.first_buy_date) > 0
                THEN COALESCE(d.total_dividend_eur,0) * 365.0 / (CURRENT_DATE - fb.first_buy_date)
                ELSE COALESCE(d.total_dividend_eur,0)
            END AS annualized_dividend_eur,
            (CURRENT_DATE - fb.first_buy_date) AS days_held
        FROM asset_daily_snapshots s
            JOIN latest l ON s.snapshot_date = l.d
            JOIN assets a ON s.asset_id = a.id
            LEFT JOIN first_buy fb ON s.asset_id = fb.asset_id
            LEFT JOIN total_dividends d ON s.asset_id = d.asset_id
        WHERE s.quantity <> 0
            AND (p_platform IS NULL OR s.platform = p_platform)
        GROUP BY s.asset_id, a.asset_type, a.id, a.company_id, a.tracked_index_id, d.total_dividend_eur, fb.first_buy_date
    )
    SELECT
        holding_id,
        MAX(
            CASE
                WHEN a.asset_type = 'ETF' AND a.tracked_index_id IS NOT NULL THEN idx.name
                WHEN a.asset_type = 'ETF' THEN a.name
                ELSE c.name
            END
        ) AS holding_name,
        SUM(COALESCE(ad.total_dividend_eur, 0)::numeric(36,8)) AS total_dividend_eur,
        SUM(cost_basis_eur) AS total_cost_basis_eur,
        SUM(annualized_dividend_eur) AS total_annualized_dividend,
        (SUM(annualized_dividend_eur) / NULLIF(SUM(cost_basis_eur),0)) * 100 AS dividend_cost_yield,
        MAX(days_held) AS days_held
    FROM annualized_dividend_per_asset ad
       JOIN assets a ON ad.asset_id = a.id
       LEFT JOIN companies c ON a.company_id = c.id
       LEFT JOIN assets idx ON a.tracked_index_id = idx.id
    GROUP BY holding_id
    ORDER BY dividend_cost_yield DESC;
$$;

