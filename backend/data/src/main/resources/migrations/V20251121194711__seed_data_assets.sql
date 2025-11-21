INSERT INTO companies (name, country_code, industry_id) VALUES
('Johnson & Johnson', 'US', (SELECT id FROM industries WHERE name = 'Drug Manufacturers - General')),
('IBM', 'US', (SELECT id FROM industries WHERE name = 'Information Technology Services'));

INSERT INTO assets (id, asset_type, "name", symbol, fund_manager, currency_code, exchange_id, company_id)
VALUES (
    gen_random_uuid(),
    'STOCK',
    'Johnson & Johnson',
    'JNJ',
    NULL,
    'USD',
    (SELECT exchange_id FROM exchanges WHERE acronym = 'NYSE'),
    (SELECT company_id FROM companies WHERE name = 'Johnson & Johnson')
);

INSERT INTO assets (id, asset_type, "name", symbol, fund_manager, currency_code, exchange_id, company_id)
VALUES (
    gen_random_uuid(),
    'STOCK',
    'International Business Machines Corporation',
    'IBM',
    NULL,
    'USD',
    (SELECT exchange_id FROM exchanges WHERE acronym = 'NYSE'),
    (SELECT company_id FROM companies WHERE name = 'Johnson & Johnson')
);

INSERT INTO asset_aliases (asset_alias_id, asset_id, platform, external_symbol)
VALUES (
	gen_random_uuid(),
	(SELECT id FROM assets WHERE symbol = 'JNJ' AND exchange_id = (SELECT exchange_id FROM exchanges WHERE acronym = 'NYSE')),
    'TRADING212',
    'JNJ'
    ),
    (
    gen_random_uuid(),
    (SELECT id FROM assets WHERE symbol = 'IBM' AND exchange_id = (SELECT exchange_id FROM exchanges WHERE acronym = 'NYSE')),
    'TRADING212',
    'IBM'
    ),
    (gen_random_uuid(),
    (SELECT id FROM assets WHERE symbol = 'IBM' AND exchange_id = (SELECT exchange_id FROM exchanges WHERE acronym = 'NYSE')),
    'ETORO',
    'IBM'
    );