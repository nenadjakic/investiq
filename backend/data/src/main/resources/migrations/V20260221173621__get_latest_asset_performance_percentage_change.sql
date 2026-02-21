CREATE OR REPLACE FUNCTION public.get_latest_asset_performance_percentage(p_platform text DEFAULT NULL::text)
    RETURNS TABLE(asset_id uuid, ticker text, name text, type text, currency_code text, percentage numeric)
LANGUAGE sql
STABLE
AS $function$
    WITH latest AS (
        SELECT MAX(snapshot_date) AS d FROM asset_daily_snapshots
    ),
    grouped AS (
        SELECT
            s.asset_id,
            SUM(s.quantity)                                                      AS total_quantity,
            SUM(s.avg_cost_per_share_eur * s.quantity) / NULLIF(SUM(s.quantity), 0) AS wavg_cost_per_share_eur,
            SUM(s.market_price_eur * s.quantity) / NULLIF(SUM(s.quantity), 0)    AS wavg_market_price_eur,
            SUM(s.cost_basis_eur)                                                AS total_cost_basis_eur,
            SUM(s.unrealized_pl_eur)                                             AS total_unrealized_pl_eur
        FROM asset_daily_snapshots s
        JOIN latest l ON s.snapshot_date = l.d
        WHERE s.quantity <> 0
          AND (p_platform IS NULL OR s.platform = p_platform)
        GROUP BY s.asset_id
    )
    SELECT
        g.asset_id,
        a.symbol                            AS ticker,
        a.name                              AS name,
        a.asset_type                        AS type,
        COALESCE(a.currency_code, 'EUR')    AS currency_code,
        CASE
            WHEN g.wavg_cost_per_share_eur IS NOT NULL
                AND g.wavg_cost_per_share_eur > 0
                AND g.wavg_market_price_eur IS NOT NULL
                THEN ((g.wavg_market_price_eur - g.wavg_cost_per_share_eur) * 100)
                / g.wavg_cost_per_share_eur
            WHEN g.total_unrealized_pl_eur IS NOT NULL
                AND g.total_cost_basis_eur IS NOT NULL
                AND g.total_cost_basis_eur > 0
                THEN (g.total_unrealized_pl_eur * 100) / g.total_cost_basis_eur
            ELSE 0
            END::numeric(36,8)                  AS percentage
    FROM grouped g
             JOIN assets a ON g.asset_id = a.id
$function$;