INSERT INTO companies (id, name, country_code, industry_id)
VALUES
    ('2c6b6bbc-1fe3-4ba7-8f75-8a830a72b584', 'CSG', 'CZ', 'a1b2c3d4-e5f6-7890-abcd-ef0123456789');

INSERT INTO assets (id, asset_type, "name", symbol, fund_manager, currency_code, exchange_id, company_id)
VALUES
    ('257673ca-4d67-4aac-a971-5ca80160ee41', 'STOCK', 'CSG N.V.', 'CSG.AS', NULL, 'EUR', '64777de8-a81a-4288-a11d-b8976d56f85a', '2c6b6bbc-1fe3-4ba7-8f75-8a830a72b584');


INSERT INTO asset_aliases (id, asset_id, platform, external_symbol)
VALUES
    ('27f2eeb2-a5f1-4569-95f9-b59368f1279d', '257673ca-4d67-4aac-a971-5ca80160ee41', 'TRADING212', 'CSG');