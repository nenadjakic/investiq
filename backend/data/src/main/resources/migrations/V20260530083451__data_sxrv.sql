INSERT INTO assets (id, asset_type, "name", symbol
                   , fund_manager, currency_code, exchange_id
                   , company_id, tracked_index_id, asset_class)
VALUES
    ('1b51a756-253b-49c9-bfb4-5aa452da9e29', 'ETF', 'iShares NASDAQ 100 UCITS ETF USD (Acc)', 'SXRV.DE'
    , 'iShares', 'EUR', 'b3c1b9fe-5a55-4e9e-9f6c-2f6f2cd13c30'
    , NULL, '99696a98-2667-48e1-9a38-851cfb8ffb45', 'EQUITY');


INSERT INTO asset_aliases (id, asset_id, platform, external_symbol)
VALUES
    ('78efee4a-ba30-4fd3-9ba2-644348b90cf5', '1b51a756-253b-49c9-bfb4-5aa452da9e29', 'TRADING212', 'SXRV');