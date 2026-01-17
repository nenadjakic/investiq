INSERT INTO assets (id, asset_type, "name", symbol, fund_manager, currency_code, exchange_id, company_id)
VALUES
    ('9719cd3e-d95b-4fa1-a007-ca909ddc228e', 'STOCK', 'Pfizer Inc.', 'PFE', NULL, 'USD', 'e698eb12-504d-4247-b65c-ef711e4a0003', '960f212e-4e3f-4017-afa1-d0d4b97a4698')
;
DELETE FROM asset_aliases
WHERE platform = 'platform'
    AND asset_id IN ('c5c0bcf3-5ac1-48f2-ab10-e1880a907232', 'd4f5e6a7-b8c9-0d1e-2f3a-4b5c6d7e8f90')
;