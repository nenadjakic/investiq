INSERT INTO assets (id, asset_type, "name", symbol, fund_manager, currency_code, exchange_id, company_id)
VALUES
    -- S&P 500 Index
    ('cf4a597d-bc40-4440-9a54-e719956d1e05', 'INDEX', 'S&P 500 Index', '^GSPC', NULL, 'USD', NULL, NULL),
    
    -- NASDAQ Composite Index
    ('99696a98-2667-48e1-9a38-851cfb8ffb45', 'INDEX', 'NASDAQ Composite Index', '^IXIC', NULL, 'USD', NULL, NULL),
    
    -- EURO STOXX 50 Index
    ('b779ffcb-1fa1-49c5-825c-6ff0bb259d1c', 'INDEX', 'EURO STOXX 50 Index', '^STOXX50E', NULL, 'EUR', NULL, NULL),
    
    -- STOXX Europe 600 Index
    ('b98c0954-19bf-4968-9e5e-08ecd358cd44', 'INDEX', 'STOXX Europe 600 Index', '^STOXX', NULL, 'EUR', NULL, NULL);
