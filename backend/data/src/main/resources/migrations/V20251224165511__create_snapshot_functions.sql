CREATE OR REPLACE FUNCTION public.get_latest_portfolio_snapshot()
RETURNS TABLE (
    snapshot_date DATE,
    total_value NUMERIC,
    total_invested NUMERIC,
    total_unrealized_pl NUMERIC,
    total_realized_pl NUMERIC,
    total_holdings BIGINT,
    total_dividends_eur NUMERIC
)
LANGUAGE sql
STABLE
AS $$
    SELECT
        snapshot_date,
        SUM(market_value_eur) as total_value,
        SUM(cost_basis_eur) as total_invested,
        SUM(unrealized_pl_eur) as total_unrealized_pl,
        SUM(realized_pl_eur) as total_realized_pl,
        COUNT(DISTINCT asset_id) as total_holdings,
        SUM(total_dividends_eur) as total_dividends_eur
    FROM asset_daily_snapshots
    WHERE snapshot_date = (SELECT MAX(snapshot_date) FROM asset_daily_snapshots)
      AND quantity > 0
    GROUP BY snapshot_date
$$;

CREATE OR REPLACE FUNCTION public.get_portfolio_snapshot_at_date_or_before(
    p_snapshot_date DATE
)
RETURNS TABLE (
    snapshot_date DATE,
    total_value NUMERIC,
    total_invested NUMERIC,
    total_unrealized_pl NUMERIC,
    total_realized_pl NUMERIC,
    total_holdings BIGINT,
    total_dividends_eur NUMERIC
)
LANGUAGE sql
STABLE
AS $$
    SELECT
        snapshot_date,
        SUM(market_value_eur) as total_value,
        SUM(cost_basis_eur) as total_invested,
        SUM(unrealized_pl_eur) as total_unrealized_pl,
        SUM(realized_pl_eur) as total_realized_pl,
        COUNT(DISTINCT asset_id) as total_holdings,
        SUM(total_dividends_eur) as total_dividends_eur
    FROM asset_daily_snapshots
    WHERE snapshot_date <= p_snapshot_date
      AND quantity > 0
    GROUP BY snapshot_date
    ORDER BY snapshot_date DESC
    LIMIT 1;
$$;

CREATE OR REPLACE FUNCTION public.get_portfolio_sector_industry_allocation()
RETURNS TABLE (
    industry TEXT,
    sector TEXT,
    value_eur NUMERIC(36,8)
)
LANGUAGE sql
STABLE
AS $$
    WITH latest AS (SELECT MAX(snapshot_date) AS d FROM asset_daily_snapshots),
    stock_values AS (
            SELECT
                COALESCE(i.name, 'Unknown') AS industry,
                COALESCE(sc.name, 'Unknown') AS sector,
                SUM(COALESCE(s.market_value_eur, (s.market_price_eur * s.quantity)::numeric(36,8), 0))::numeric(36,8) AS value_eur
            FROM asset_daily_snapshots s
            JOIN latest l ON s.snapshot_date = l.d
            JOIN assets a ON s.asset_id = a.id
            LEFT JOIN companies c ON a.company_id = c.id
            LEFT JOIN industries i ON c.industry_id = i.id
            LEFT JOIN sectors sc ON i.sector_id = sc.id
            WHERE a.asset_type = 'STOCK'
                AND s.quantity <> 0
            GROUP BY COALESCE(i.name, 'Unknown'), COALESCE(sc.name, 'Unknown')
    ),
    etf_values AS (
            -- ETFs with direct sector allocations
            SELECT
                'Unknown' AS industry,
                COALESCE(sc.name, 'Unknown') AS sector,
                SUM(
                    COALESCE(s.market_value_eur, (s.market_price_eur * s.quantity)::numeric(36,8), 0)
                    * (esa.weight_percentage / 100.0)
                )::numeric(36,8) AS value_eur
            FROM asset_daily_snapshots s
            JOIN latest l ON s.snapshot_date = l.d
            JOIN assets a ON s.asset_id = a.id
            JOIN etf_sector_allocations esa ON esa.etf_id = a.id
            LEFT JOIN sectors sc ON esa.sector_id = sc.id
            WHERE a.asset_type = 'ETF'
                AND a.tracked_index_id IS NULL
                AND s.quantity <> 0
            GROUP BY COALESCE(sc.name, 'Unknown')
            UNION ALL
            -- ETFs that track an index use the index sector allocation
            SELECT
                'Unknown' AS industry,
                COALESCE(sc.name, 'Unknown') AS sector,
                SUM(
                    COALESCE(s.market_value_eur, (s.market_price_eur * s.quantity)::numeric(36,8), 0)
                    * (isa.weight_percentage / 100.0)
                )::numeric(36,8) AS value_eur
            FROM asset_daily_snapshots s
            JOIN latest l ON s.snapshot_date = l.d
            JOIN assets a ON s.asset_id = a.id
            JOIN index_sector_allocations isa ON isa.index_id = a.tracked_index_id
            LEFT JOIN sectors sc ON isa.sector_id = sc.id
            WHERE a.asset_type = 'ETF'
                AND a.tracked_index_id IS NOT NULL
                AND s.quantity <> 0
            GROUP BY COALESCE(sc.name, 'Unknown')
    )
    SELECT industry, sector, SUM(value_eur)::numeric(36,8) AS value_eur
    FROM (
             SELECT industry, sector, value_eur FROM stock_values
             UNION ALL
             SELECT industry, sector, value_eur FROM etf_values
         ) t
    GROUP BY industry, sector
    ORDER BY value_eur DESC;
$$;

CREATE OR REPLACE FUNCTION public.get_portfolio_country_allocation()
RETURNS TABLE (
    country TEXT,
    value_eur NUMERIC(36,8)
)
LANGUAGE sql
STABLE
AS $$
    WITH latest AS (SELECT MAX(snapshot_date) AS d FROM asset_daily_snapshots),
    stock_values AS (
            SELECT
                COALESCE(co.name, 'Unknown') AS country,
                SUM(COALESCE(s.market_value_eur, (s.market_price_eur * s.quantity)::numeric(36,8), 0))::numeric(36,8) AS value_eur
            FROM asset_daily_snapshots s
            JOIN latest l ON s.snapshot_date = l.d
            JOIN assets a ON s.asset_id = a.id
            LEFT JOIN companies c ON a.company_id = c.id
            LEFT JOIN countries co ON c.country_code = co.iso2_code
            WHERE a.asset_type = 'STOCK'
                AND s.quantity <> 0
            GROUP BY COALESCE(co.name, 'Unknown')
    ),
    etf_values AS (
            -- ETFs with direct country allocations
            SELECT
                COALESCE(co.name, 'Unknown') AS country,
                SUM(
                    COALESCE(s.market_value_eur, (s.market_price_eur * s.quantity)::numeric(36,8), 0)
                    * (eca.weight_percentage / 100.0)
                )::numeric(36,8) AS value_eur
            FROM asset_daily_snapshots s
            JOIN latest l ON s.snapshot_date = l.d
            JOIN assets a ON s.asset_id = a.id
            JOIN etf_country_allocations eca ON eca.etf_id = a.id
            LEFT JOIN countries co ON eca.country_code = co.iso2_code
            WHERE a.asset_type = 'ETF'
                AND a.tracked_index_id IS NULL
                AND s.quantity <> 0
            GROUP BY COALESCE(co.name, 'Unknown')
            UNION ALL
            -- ETFs that track an index use the index country allocation
            SELECT
                COALESCE(co.name, 'Unknown') AS country,
                SUM(
                    COALESCE(s.market_value_eur, (s.market_price_eur * s.quantity)::numeric(36,8), 0)
                    * (ica.weight_percentage / 100.0)
                )::numeric(36,8) AS value_eur
            FROM asset_daily_snapshots s
            JOIN latest l ON s.snapshot_date = l.d
            JOIN assets a ON s.asset_id = a.id
            JOIN index_country_allocations ica ON ica.index_id = a.tracked_index_id
            LEFT JOIN countries co ON ica.country_code = co.iso2_code
            WHERE a.asset_type = 'ETF'
                AND a.tracked_index_id IS NOT NULL
                AND s.quantity <> 0
            GROUP BY COALESCE(co.name, 'Unknown')
    )
    SELECT country, SUM(value_eur)::numeric(36,8) AS value_eur
    FROM (
             SELECT country, value_eur FROM stock_values
             UNION ALL
             SELECT country, value_eur FROM etf_values
         ) t
    GROUP BY country
    ORDER BY value_eur DESC;
$$;

CREATE OR REPLACE FUNCTION public.get_portfolio_currency_allocation()
RETURNS TABLE (
    currency TEXT,
    value_eur NUMERIC(36,8)
)
LANGUAGE sql
STABLE
AS $$
    WITH latest AS (SELECT MAX(snapshot_date) AS d FROM asset_daily_snapshots)
    SELECT
        COALESCE(UPPER(a.currency_code), 'UNKNOWN') AS currency,
        SUM(COALESCE(s.market_value_eur, (s.market_price_eur * s.quantity)::numeric(36,8), 0))::numeric(36,8) AS value_eur
    FROM asset_daily_snapshots s
             JOIN latest l ON s.snapshot_date = l.d
             JOIN assets a ON s.asset_id = a.id
    WHERE s.snapshot_date = l.d
      AND s.quantity <> 0
    GROUP BY COALESCE(UPPER(a.currency_code), 'UNKNOWN')
    ORDER BY value_eur DESC;
$$;

CREATE OR REPLACE FUNCTION public.get_portfolio_asset_type_allocation()
RETURNS TABLE (
    asset_type TEXT,
    value_eur NUMERIC
)
LANGUAGE sql
STABLE
AS $$
    WITH latest AS (SELECT MAX(snapshot_date) AS d FROM asset_daily_snapshots)
    SELECT
        COALESCE(UPPER(a.asset_type), 'UNKNOWN') AS asset_type,
        SUM(COALESCE(s.market_value_eur, 0)) AS value_eur
    FROM asset_daily_snapshots s
             JOIN latest l ON s.snapshot_date = l.d
             JOIN assets a ON s.asset_id = a.id
    WHERE s.snapshot_date = l.d
      AND s.quantity <> 0
    GROUP BY COALESCE(UPPER(a.asset_type), 'UNKNOWN')
    ORDER BY value_eur DESC;
$$;

CREATE OR REPLACE FUNCTION public.get_latest_portfolio_holdings()
RETURNS TABLE (
    snapshot_date DATE,
    asset_id UUID,
    quantity NUMERIC,
    avg_cost_per_share_eur NUMERIC(36,8),
    cost_basis_eur NUMERIC,
    market_price_eur NUMERIC(36,8),
    market_value_eur NUMERIC,
    unrealized_pl_eur NUMERIC,
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
        a.symbol AS ticker,
        a.name AS name,
        a.asset_type AS asset_type
    FROM asset_daily_snapshots s
             JOIN latest l ON s.snapshot_date = l.d
             JOIN assets a ON s.asset_id = a.id
    WHERE s.quantity <> 0
    GROUP BY l.d, s.asset_id, a.symbol, a.name, a.asset_type
    ORDER BY SUM(COALESCE(s.market_value_eur,0)) DESC
$$;

CREATE OR REPLACE FUNCTION public.get_latest_portfolio_holdings_grouped()
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
        CASE WHEN a.asset_type = 'ETF' THEN a.id ELSE a.company_id END AS holding_id,
        CASE WHEN a.asset_type = 'ETF' THEN a.name ELSE c.name END AS holding_name,

        SUM(s.cost_basis_eur) AS cost_basis_eur,
        SUM(s.market_value_eur) AS market_value_eur,
        SUM(s.unrealized_pl_eur) AS unrealized_pl_eur,
        ARRAY_AGG(DISTINCT a.symbol ORDER BY a.symbol) AS tickers
    FROM asset_daily_snapshots s
             JOIN latest l ON s.snapshot_date = l.d
             JOIN assets a ON s.asset_id = a.id
             LEFT JOIN companies c ON a.company_id = c.id
    WHERE s.quantity <> 0
    GROUP BY l.d,  holding_id, holding_name
    ORDER BY SUM(COALESCE(s.market_value_eur,0)) desc
$$;


CREATE OR REPLACE FUNCTION public.get_latest_asset_performance_percentage()
RETURNS TABLE (
    asset_id UUID,
    ticker TEXT,
    name TEXT,
    type TEXT,
    currency_code TEXT,
    percentage NUMERIC(36,8)
)
LANGUAGE sql
STABLE
AS $$
    WITH latest AS (SELECT MAX(snapshot_date) AS d FROM asset_daily_snapshots)
    SELECT
        s.asset_id,
        a.symbol AS ticker,
        a.name AS name,
        a.asset_type AS type,
        COALESCE(a.currency_code, 'EUR') AS currency_code,
        CASE
            WHEN s.avg_cost_per_share_eur IS NOT NULL AND s.avg_cost_per_share_eur > 0 AND s.market_price_eur IS NOT NULL
                THEN ((s.market_price_eur - s.avg_cost_per_share_eur) * 100) / s.avg_cost_per_share_eur
            WHEN s.unrealized_pl_eur IS NOT NULL AND s.cost_basis_eur IS NOT NULL AND s.cost_basis_eur > 0
                THEN (s.unrealized_pl_eur * 100) / s.cost_basis_eur
            ELSE 0
            END::numeric(36,8) AS percentage
    FROM asset_daily_snapshots s
             JOIN latest l ON s.snapshot_date = l.d
             JOIN assets a ON s.asset_id = a.id
    WHERE s.quantity <> 0
$$;


CREATE OR REPLACE FUNCTION public.get_latest_asset_dividend_performance()
RETURNS TABLE (
    asset_id UUID,
    ticker TEXT,
    name TEXT,
    total_dividend_eur NUMERIC(36,8),
    cost_basis_eur NUMERIC(36,8),
    first_buy_date DATE,
    days_held INTEGER,
    annualized_dividend_eur NUMERIC(36,8),
    dividend_cost_yield NUMERIC(36,8)
)
LANGUAGE sql
STABLE
AS $$
    WITH latest AS (SELECT MAX(snapshot_date) AS d FROM asset_daily_snapshots),
                first_buy AS (
                    SELECT
                        asset_id,
                        MIN(transaction_date_only) AS first_buy_date
                    FROM vw_transaction_analytics
                    WHERE transaction_type = 'BUY'
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
                    GROUP BY t.asset_id
                )
    SELECT
        s.asset_id,
        a.symbol AS ticker,
        a.name AS name,
        COALESCE(d.total_dividend_eur, 0)::numeric(36,8) AS total_dividend_eur,
        SUM(s.cost_basis_eur)::numeric(36,8) AS cost_basis_eur,
        fb.first_buy_date,
        -- Calculate days held
        (CURRENT_DATE - fb.first_buy_date) AS days_held,
        -- Annualized dividend = total_dividend * 365 / days_held
        CASE
            WHEN (CURRENT_DATE - fb.first_buy_date) > 0
                THEN (COALESCE(d.total_dividend_eur, 0) * 365.0 / (CURRENT_DATE - fb.first_buy_date))
            ELSE COALESCE(d.total_dividend_eur, 0)
            END::numeric(36,8) AS annualized_dividend_eur,
                  -- Dividend cost yield = annualized_dividend / cost_basis * 100
        CASE
            WHEN SUM(s.cost_basis_eur) > 0 AND (CURRENT_DATE - fb.first_buy_date) > 0
                THEN ((COALESCE(d.total_dividend_eur, 0) * 365.0 / (CURRENT_DATE - fb.first_buy_date)) * 100 / SUM(s.cost_basis_eur))
            WHEN SUM(s.cost_basis_eur) > 0
                THEN (COALESCE(d.total_dividend_eur, 0) * 100 / SUM(s.cost_basis_eur))
            ELSE 0
            END::numeric(36,8) AS dividend_cost_yield
    FROM asset_daily_snapshots s
             JOIN latest l ON s.snapshot_date = l.d
             JOIN assets a ON s.asset_id = a.id
             LEFT JOIN first_buy fb ON s.asset_id = fb.asset_id
             LEFT JOIN total_dividends d ON s.asset_id = d.asset_id
    WHERE s.quantity <> 0
    GROUP BY s.asset_id, a.symbol, a.name, d.total_dividend_eur, fb.first_buy_date
    ORDER BY dividend_cost_yield DESC
$$;


CREATE OR REPLACE FUNCTION public.get_latest_holding_dividend_performance()
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
        GROUP BY t.asset_id
    ),
    annualized_dividend_per_asset AS (
        SELECT
            s.asset_id,
            CASE WHEN a.asset_type = 'ETF' THEN a.id ELSE a.company_id END AS holding_id,
            s.cost_basis_eur,
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
    )
    SELECT
        holding_id,
        MAX(CASE WHEN a.asset_type = 'ETF' THEN a.name ELSE c.name END) AS holding_name,
        SUM(COALESCE(ad.total_dividend_eur, 0)::numeric(36,8)) AS total_dividend_eur,
        SUM(cost_basis_eur) AS total_cost_basis_eur,
        SUM(annualized_dividend_eur) AS total_annualized_dividend,
        (SUM(annualized_dividend_eur) / NULLIF(SUM(cost_basis_eur),0)) * 100 AS dividend_cost_yield,
        MAX(days_held) AS days_held
    FROM annualized_dividend_per_asset ad
             JOIN assets a ON ad.asset_id = a.id
             LEFT JOIN companies c ON a.company_id = c.id
    GROUP BY holding_id
    ORDER BY dividend_cost_yield DESC;
$$;

CREATE OR REPLACE FUNCTION public.get_latest_portfolio_dividend_performance()
RETURNS TABLE (
    total_dividend_eur NUMERIC(36,8),
    total_cost_basis_eur NUMERIC(36,8),
    first_investment_date DATE,
    days_held INTEGER,
    annualized_dividend_eur NUMERIC(36,8),
    dividend_cost_yield NUMERIC(36,8)
)
LANGUAGE sql
STABLE
AS $$
    WITH latest AS (SELECT MAX(snapshot_date) AS d FROM asset_daily_snapshots),
                first_investment AS (
                    SELECT MIN(transaction_date_only) AS first_date
                    FROM vw_transaction_analytics
                    WHERE transaction_type = 'BUY'
                ),
                total_dividends AS (
                    SELECT COALESCE(SUM(transaction_value_eur), 0) AS total_dividend_eur
                    FROM vw_transaction_analytics
                    WHERE transaction_type = 'DIVIDEND'
                ),
                portfolio_cost AS (
                    SELECT COALESCE(SUM(s.cost_basis_eur), 0) AS total_cost_basis_eur
                    FROM asset_daily_snapshots s
                    JOIN latest l ON s.snapshot_date = l.d
                    WHERE s.quantity <> 0
                )
    SELECT
        d.total_dividend_eur::numeric(36,8) AS total_dividend_eur,
        p.total_cost_basis_eur::numeric(36,8) AS total_cost_basis_eur,
        fi.first_date AS first_investment_date,
        COALESCE(CURRENT_DATE - fi.first_date, 0) AS days_held,
        -- Annualized dividend = total_dividend * 365 / days_held
        CASE
            WHEN fi.first_date IS NOT NULL AND (CURRENT_DATE - fi.first_date) > 0
                THEN (d.total_dividend_eur * 365.0 / (CURRENT_DATE - fi.first_date))
            ELSE d.total_dividend_eur
            END::numeric(36,8) AS annualized_dividend_eur,
                  -- Dividend cost yield = annualized_dividend / cost_basis * 100
        CASE
            WHEN p.total_cost_basis_eur > 0 AND fi.first_date IS NOT NULL AND (CURRENT_DATE - fi.first_date) > 0
                THEN ((d.total_dividend_eur * 365.0 / (CURRENT_DATE - fi.first_date)) * 100 / p.total_cost_basis_eur)
            WHEN p.total_cost_basis_eur > 0
                THEN (d.total_dividend_eur * 100 / p.total_cost_basis_eur)
            ELSE 0
            END::numeric(36,8) AS dividend_cost_yield
    FROM total_dividends d
             CROSS JOIN portfolio_cost p
             CROSS JOIN first_investment fi
$$;