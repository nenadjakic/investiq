CREATE OR REPLACE FUNCTION public.get_latest_portfolio_holdings(
    p_platform text DEFAULT NULL
)
RETURNS TABLE (
    snapshot_date DATE,
    asset_id UUID,
    quantity NUMERIC,
    avg_cost_per_share_eur NUMERIC(36,8),
    cost_basis_eur NUMERIC,
    market_price_eur NUMERIC(36,8),
    market_value_eur NUMERIC,
    unrealized_pl_eur NUMERIC,
    realized_pl_eur NUMERIC,
    total_dividends_eur NUMERIC,
    total_fees_eur NUMERIC,
    ticker TEXT,
    name TEXT,
    asset_type TEXT
)
LANGUAGE sql
STABLE
AS $$
    WITH latest AS (SELECT MAX(snapshot_date) AS d FROM asset_daily_snapshots)
SELECT
    l.d AS snapshot_date,
    s.asset_id,
    SUM(s.quantity) AS quantity,
    -- weighted avg cost per share = SUM(cost_basis_eur) / SUM(quantity)
    CASE WHEN SUM(s.quantity) > 0 AND SUM(s.cost_basis_eur) IS NOT NULL
             THEN (SUM(s.cost_basis_eur) / NULLIF(SUM(s.quantity), 0))
         ELSE NULL
        END::numeric(36,8) AS avg_cost_per_share_eur,
    SUM(s.cost_basis_eur) AS cost_basis_eur,
    -- weighted market price = SUM(market_value_eur) / SUM(quantity) when available
    CASE WHEN SUM(s.quantity) > 0 AND SUM(s.market_value_eur) IS NOT NULL
             THEN (SUM(s.market_value_eur) / NULLIF(SUM(s.quantity), 0))
         ELSE NULL
        END::numeric(36,8) AS market_price_eur,
    SUM(s.market_value_eur) AS market_value_eur,
    SUM(s.unrealized_pl_eur) AS unrealized_pl_eur,
    SUM(s.realized_pl_eur) AS realized_pl_eur,
    SUM(s.total_dividends_eur) AS total_dividends_eur,
    SUM(s.total_fees_eur) AS total_fees_eur,
    a.symbol AS ticker,
    a.name AS name,
    a.asset_type AS asset_type
FROM asset_daily_snapshots s
         JOIN latest l ON s.snapshot_date = l.d
         JOIN assets a ON s.asset_id = a.id
WHERE s.quantity <> 0
  AND (p_platform IS NULL OR s.platform = p_platform)
GROUP BY l.d, s.asset_id, a.symbol, a.name, a.asset_type
ORDER BY SUM(COALESCE(s.market_value_eur,0)) DESC
    $$;
