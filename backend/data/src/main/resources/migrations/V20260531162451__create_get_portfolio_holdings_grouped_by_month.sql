CREATE OR REPLACE FUNCTION public.get_portfolio_holdings_grouped_by_month(p_platform text DEFAULT NULL::text)
RETURNS TABLE(
    year int,
    month int,
    holding_id uuid,
    holding_name text,
    cost_basis_eur numeric,
    market_value_eur numeric,
    unrealized_pl_eur numeric,
    tickers text[]
)
LANGUAGE sql
STABLE
AS $function$
    WITH end_of_months AS (
        SELECT
            EXTRACT(YEAR FROM snapshot_date)::int AS year,
            EXTRACT(MONTH FROM snapshot_date)::int AS month,
            MAX(snapshot_date) AS snapshot_date
        FROM asset_daily_snapshots
        GROUP BY EXTRACT(YEAR FROM snapshot_date), EXTRACT(MONTH FROM snapshot_date)
    )
SELECT
    eom.year,
    eom.month,
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
    SUM(s.cost_basis_eur)      AS cost_basis_eur,
    SUM(s.market_value_eur)    AS market_value_eur,
    SUM(s.unrealized_pl_eur)   AS unrealized_pl_eur,
    ARRAY_AGG(DISTINCT a.symbol ORDER BY a.symbol) AS tickers
FROM asset_daily_snapshots s
         JOIN end_of_months eom ON s.snapshot_date = eom.snapshot_date
         JOIN assets a ON s.asset_id = a.id
         LEFT JOIN companies c ON a.company_id = c.id
         LEFT JOIN assets idx ON a.tracked_index_id = idx.id
WHERE s.quantity <> 0
  AND (p_platform IS NULL OR s.platform = p_platform)
GROUP BY eom.year, eom.month, holding_id, holding_name
ORDER BY eom.year, eom.month, SUM(COALESCE(s.market_value_eur, 0)) DESC
    $function$;