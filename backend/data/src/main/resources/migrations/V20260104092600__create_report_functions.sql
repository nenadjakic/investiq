CREATE OR REPLACE FUNCTION public.get_portfolio_snapshot_with_previous(
    p_date DATE,
    p_period CHAR(1)  -- 'W' = weekly, 'M' = monthly, 'Y' = yearly
)
RETURNS TABLE (
    snapshot_date DATE,
    prev_snapshot_date DATE,

    total_cost_basis_eur NUMERIC,
    total_market_value_eur NUMERIC,
    total_unrealized_pl_eur NUMERIC,
    total_realized_pl_eur NUMERIC,
    total_dividends_eur NUMERIC,
    total_fees_eur NUMERIC,
    total_holdings BIGINT,
    total_buy_amount_eur numeric,

    prev_total_cost_basis_eur NUMERIC,
    prev_total_market_value_eur NUMERIC,
    prev_total_unrealized_pl_eur NUMERIC,
    prev_total_realized_pl_eur NUMERIC,
    prev_total_dividends_eur NUMERIC,
    prev_total_fees_eur NUMERIC,
    prev_total_holdings BIGINT,
	prev_total_buy_amount_eur NUMERIC
)
LANGUAGE sql
STABLE
AS $$
WITH params AS (
    SELECT
        p_date AS curr_date,
        CASE p_period
            WHEN 'W' THEN p_date - INTERVAL '1 week'
            WHEN 'M' THEN p_date - INTERVAL '1 month'
            WHEN 'Y' THEN p_date - INTERVAL '1 year'
        END::DATE AS prev_date
),

curr AS (
    SELECT
        SUM(s.cost_basis_eur) AS total_cost_basis_eur,
        SUM(s.market_value_eur) AS total_market_value_eur,
        SUM(s.unrealized_pl_eur) AS total_unrealized_pl_eur,
        SUM(s.realized_pl_eur) AS total_realized_pl_eur,
        SUM(s.total_dividends_eur) AS total_dividends_eur,
        SUM(s.total_fees_eur) AS total_fees_eur,
        COUNT(DISTINCT CASE WHEN a.asset_type = 'ETF' THEN a.id ELSE a.company_id END) AS total_holdings,
		SUM(s.total_buy_amount_eur) as total_buy_amount_eur
    FROM asset_daily_snapshots s
    JOIN assets a ON s.asset_id = a.id
    JOIN params p ON s.snapshot_date = p.curr_date
    WHERE s.quantity <> 0
),

prev AS (
    SELECT
        SUM(s.cost_basis_eur) AS prev_total_cost_basis_eur,
        SUM(s.market_value_eur) AS prev_total_market_value_eur,
        SUM(s.unrealized_pl_eur) AS prev_total_unrealized_pl_eur,
        SUM(s.realized_pl_eur) AS prev_total_realized_pl_eur,
        SUM(s.total_dividends_eur) AS prev_total_dividends_eur,
        SUM(s.total_fees_eur) AS prev_total_fees_eur,
        COUNT(DISTINCT CASE WHEN a.asset_type = 'ETF' THEN a.id ELSE a.company_id END) AS prev_total_holdings,
		SUM(s.total_buy_amount_eur) as prev_total_buy_amount_eur
    FROM asset_daily_snapshots s
    JOIN assets a ON s.asset_id = a.id
    JOIN params p ON s.snapshot_date = p.prev_date
    WHERE s.quantity <> 0
)

SELECT
    (SELECT curr_date FROM params) AS snapshot_date,
    (SELECT prev_date FROM params) AS prev_snapshot_date,

    c.total_cost_basis_eur,
    c.total_market_value_eur,
    c.total_unrealized_pl_eur,
    c.total_realized_pl_eur,
    c.total_dividends_eur,
    c.total_fees_eur,
    c.total_holdings,
    c.total_buy_amount_eur,

    p.prev_total_cost_basis_eur,
    p.prev_total_market_value_eur,
    p.prev_total_unrealized_pl_eur,
    p.prev_total_realized_pl_eur,
    p.prev_total_dividends_eur,
    p.prev_total_fees_eur,
    p.prev_total_holdings,
    p.prev_total_buy_amount_eur
FROM curr c
         CROSS JOIN prev p;
$$;

CREATE OR REPLACE FUNCTION public.get_portfolio_holdings_with_previous(
    p_date DATE,
    p_period CHAR(1)
)
RETURNS TABLE (
    snapshot_date DATE,
    prev_snapshot_date DATE,

    holding_id UUID,
    holding_name TEXT,
    tickers TEXT[],

    cost_basis_eur NUMERIC,
    market_value_eur NUMERIC,
    unrealized_pl_eur NUMERIC,
    realized_pl_eur NUMERIC,
    dividends_eur NUMERIC,
    fees_eur NUMERIC,

    weight_pct NUMERIC,
    prev_weight_pct NUMERIC,

    prev_cost_basis_eur NUMERIC,
    prev_market_value_eur NUMERIC,
    prev_unrealized_pl_eur NUMERIC,
    prev_realized_pl_eur NUMERIC,
    prev_dividends_eur NUMERIC,
    prev_fees_eur NUMERIC
)
LANGUAGE sql
STABLE
AS $$
WITH params AS (
    SELECT
        p_date AS curr_date,
        CASE p_period
            WHEN 'W' THEN p_date - INTERVAL '1 week'
            WHEN 'M' THEN p_date - INTERVAL '1 month'
            WHEN 'Y' THEN p_date - INTERVAL '1 year'
        END::DATE AS prev_date
),

curr AS (
    SELECT
        p.curr_date AS snapshot_date,
        CASE WHEN a.asset_type = 'ETF' THEN a.id ELSE a.company_id END AS holding_id,
        CASE WHEN a.asset_type = 'ETF' THEN a.name ELSE c.name END AS holding_name,

        SUM(s.cost_basis_eur) AS cost_basis_eur,
        SUM(s.market_value_eur) AS market_value_eur,
        SUM(s.unrealized_pl_eur) AS unrealized_pl_eur,
        SUM(s.realized_pl_eur) AS realized_pl_eur,
        SUM(s.total_dividends_eur) AS dividends_eur,
        SUM(s.total_fees_eur) AS fees_eur,

        ARRAY_AGG(DISTINCT a.symbol ORDER BY a.symbol) AS tickers
    FROM params p
    JOIN asset_daily_snapshots s
        ON s.snapshot_date = p.curr_date
    JOIN assets a ON s.asset_id = a.id
    LEFT JOIN companies c ON a.company_id = c.id
    WHERE s.quantity <> 0
    GROUP BY p.curr_date, holding_id, holding_name
),

curr_totals AS (
    SELECT SUM(market_value_eur) AS total_market_value_eur
    FROM curr
),

prev AS (
    SELECT
        p.prev_date AS snapshot_date,
        CASE WHEN a.asset_type = 'ETF' THEN a.id ELSE a.company_id END AS holding_id,

        SUM(s.cost_basis_eur) AS cost_basis_eur,
        SUM(s.market_value_eur) AS market_value_eur,
        SUM(s.unrealized_pl_eur) AS unrealized_pl_eur,
        SUM(s.realized_pl_eur) AS realized_pl_eur,
        SUM(s.total_dividends_eur) AS dividends_eur,
        SUM(s.total_fees_eur) AS fees_eur
    FROM params p
    JOIN asset_daily_snapshots s
        ON s.snapshot_date = p.prev_date
    JOIN assets a ON s.asset_id = a.id
    WHERE s.quantity <> 0
    GROUP BY p.prev_date, holding_id
),

prev_totals AS (
    SELECT SUM(market_value_eur) AS total_market_value_eur
    FROM prev
)

SELECT
    c.snapshot_date,
    (SELECT prev_date FROM params) AS prev_snapshot_date,

    c.holding_id,
    c.holding_name,
    c.tickers,

    COALESCE(c.cost_basis_eur, 0) AS cost_basis_eur,
    COALESCE(c.market_value_eur, 0) AS market_value_eur,
    COALESCE(c.unrealized_pl_eur, 0) AS unrealized_pl_eur,
    COALESCE(c.realized_pl_eur, 0) AS realized_pl_eur,
    COALESCE(c.dividends_eur, 0) AS dividends_eur,
    COALESCE(c.fees_eur, 0) AS fees_eur,

    COALESCE(CASE
                 WHEN ct.total_market_value_eur = 0 THEN 0
                 ELSE ROUND(
                         COALESCE(c.market_value_eur, 0)
                             / ct.total_market_value_eur * 100,
                         4
                      )
                 END, 0) AS weight_pct,

    COALESCE(CASE
                 WHEN pt.total_market_value_eur = 0 THEN 0
                 ELSE ROUND(
                         COALESCE(p.market_value_eur, 0)
                             / pt.total_market_value_eur * 100,
                         4
                      )
                 END, 0) AS prev_weight_pct,

    COALESCE(p.cost_basis_eur, 0) AS prev_cost_basis_eur,
    COALESCE(p.market_value_eur, 0) AS prev_market_value_eur,
    COALESCE(p.unrealized_pl_eur, 0) AS prev_unrealized_pl_eur,
    COALESCE(p.realized_pl_eur, 0) AS prev_realized_pl_eur,
    COALESCE(p.dividends_eur, 0) AS prev_dividends_eur,
    COALESCE(p.fees_eur, 0) AS prev_fees_eur
FROM curr c
         CROSS JOIN curr_totals ct
         LEFT JOIN prev p USING (holding_id)
         CROSS JOIN prev_totals pt
ORDER BY c.market_value_eur DESC;
$$;
