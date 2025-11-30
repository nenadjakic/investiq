CREATE OR REPLACE VIEW vw_transaction_analytics AS
SELECT 
    t.id AS transaction_id,
   	t.platform as transaction_platform,
    t.transaction_type,
    t.transaction_date,
    DATE(t.transaction_date) AS transaction_date_only,
    EXTRACT(YEAR FROM t.transaction_date) AS year,
    EXTRACT(QUARTER FROM t.transaction_date) AS quarter,
    EXTRACT(MONTH FROM t.transaction_date) AS month,
    EXTRACT(WEEK FROM t.transaction_date) AS week,
    
    t.quantity,
    t.price,
    t.amount,
    t.gross_amount,
    t.tax_amount,
    t.tax_percentage,
    
    t.currency_code AS currency,
    
    a.id AS asset_id,
    a.name AS asset_name,
    a.symbol AS asset_symbol,
    a.asset_type,
    a.fund_manager,
    
    c.id AS company_id,
    c.name AS company_name,
    
    co.iso2_code AS company_country_code,
    co.name AS company_country_name,
    
    i.id AS industry_id,
    i.name AS industry_name,
    
    s.id AS sector_id,
    s.name AS sector_name,
    
    e.id AS exchange_id,
    e.name AS exchange_name,
    e.acronym AS exchange_acronym,
    e.mic AS exchange_mic,
    
    co_ex.iso2_code AS exchange_country_code,
    co_ex.name AS exchange_country_name,    
	(
	    -- base value in *effective currency* (GBX normalized to GBP)
	    (
	      CASE
	        WHEN t.transaction_type = 'BUY' THEN
	          (CASE WHEN t.currency_code = 'GBX' THEN (t.price / 100.0) ELSE t.price END) * COALESCE(t.quantity, 0)
	        WHEN t.transaction_type = 'SELL' THEN
	          -1 * (CASE WHEN t.currency_code = 'GBX' THEN (t.price / 100.0) ELSE t.price END) * COALESCE(t.quantity, 0)
	        WHEN t.transaction_type = 'DIVIDEND' THEN
	          (CASE WHEN t.currency_code = 'GBX' THEN (t.amount / 100.0) ELSE t.amount END)
	        WHEN t.transaction_type = 'DIVIDEND_ADJUSTMENT' THEN
	          -1 * (CASE WHEN t.currency_code = 'GBX' THEN (t.amount / 100.0) ELSE t.amount END)
	        WHEN t.transaction_type = 'DEPOSIT' THEN
	          (CASE WHEN t.currency_code = 'GBX' THEN (t.amount / 100.0) ELSE t.amount END)
	        WHEN t.transaction_type = 'WITHDRAWAL' THEN
	          -1 * (CASE WHEN t.currency_code = 'GBX' THEN (t.amount / 100.0) ELSE t.amount END)
	        ELSE
	          (CASE WHEN t.currency_code = 'GBX' THEN (t.amount / 100.0) ELSE t.amount END)
	      END
	    )
	    /* convert base_value -> EUR */
	    *
	    CASE
	      WHEN (CASE WHEN t.currency_code = 'GBX' THEN 'GBP' ELSE t.currency_code END) = 'EUR' THEN 1.0
	      ELSE COALESCE(ch1.exchange_rate, CASE WHEN ch2.exchange_rate IS NOT NULL THEN (1.0 / ch2.exchange_rate) ELSE NULL END)
	    END
	) AS transaction_value_eur
	    
FROM transactions t
	LEFT JOIN assets a ON t.asset_id = a.id
	LEFT JOIN currencies curr ON t.currency_code = curr.code
	LEFT JOIN currencies curr_asset ON a.currency_code = curr_asset.code
	LEFT JOIN companies c ON a.company_id = c.id
	LEFT JOIN countries co ON c.country_code = co.iso2_code
	LEFT JOIN industries i ON c.industry_id = i.id
	LEFT JOIN sectors s ON i.sector_id = s.id
	LEFT JOIN exchanges e ON a.exchange_id = e.id
	LEFT JOIN countries co_ex ON e.country_iso2_code = co_ex.iso2_code
	
	LEFT JOIN LATERAL (
	    SELECT ch.exchange_rate
	    FROM currency_histories ch
	    WHERE ch.from_currency_code = (CASE WHEN t.currency_code = 'GBX' THEN 'GBP' ELSE t.currency_code END)
	      AND ch.to_currency_code = 'EUR'
	      AND ch.valid_date <= t.transaction_date::date
	    ORDER BY ch.valid_date DESC
	    LIMIT 1
	) ch1 ON (CASE WHEN t.currency_code = 'GBX' THEN 'GBP' ELSE t.currency_code END) <> 'EUR'
	
	LEFT JOIN LATERAL (
	    SELECT ch.exchange_rate
	    FROM currency_histories ch
	    WHERE ch.from_currency_code = 'EUR'
	      AND ch.to_currency_code = (CASE WHEN t.currency_code = 'GBX' THEN 'GBP' ELSE t.currency_code END)
	      AND ch.valid_date <= t.transaction_date::date
	    ORDER BY ch.valid_date DESC
	    LIMIT 1
	) ch2 ON (CASE WHEN t.currency_code = 'GBX' THEN 'GBP' ELSE t.currency_code END) <> 'EUR';