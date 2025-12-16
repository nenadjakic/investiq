-- Add INDEX type to asset_type constraint
ALTER TABLE assets DROP CONSTRAINT IF EXISTS ch_assets_asset_type;
ALTER TABLE assets ADD CONSTRAINT ch_assets_asset_type 
    CHECK (((asset_type)::text = ANY ((ARRAY['ETF'::character varying, 'STOCK'::character varying, 'INDEX'::character varying])::text[])));

-- Insert stock market indices data
-- Using major exchanges - we'll use NASDAQ for US indices and XETR for European indices
INSERT INTO assets (id, asset_type, "name", symbol, fund_manager, currency_code, exchange_id, company_id)
VALUES
    -- S&P 500 Index
    ('00000000-0000-0000-0000-000000000001', 'INDEX', 'S&P 500 Index', '^GSPC', NULL, 'USD', 'e698eb12-504d-4247-b65c-ef711e4a0003', NULL),
    
    -- NASDAQ Composite Index
    ('00000000-0000-0000-0000-000000000002', 'INDEX', 'NASDAQ Composite Index', '^IXIC', NULL, 'USD', 'e698eb12-504d-4247-b65c-ef711e4a0003', NULL),
    
    -- EURO STOXX 50 Index
    ('00000000-0000-0000-0000-000000000003', 'INDEX', 'EURO STOXX 50 Index', '^STOXX50E', NULL, 'EUR', 'b3c1b9fe-5a55-4e9e-9f6c-2f6f2cd13c30', NULL),
    
    -- STOXX Europe 600 Index
    ('00000000-0000-0000-0000-000000000004', 'INDEX', 'STOXX Europe 600 Index', '^STOXX', NULL, 'EUR', 'b3c1b9fe-5a55-4e9e-9f6c-2f6f2cd13c30', NULL);
