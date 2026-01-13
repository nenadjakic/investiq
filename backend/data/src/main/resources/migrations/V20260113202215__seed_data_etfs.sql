UPDATE assets
SET exchange_id = 'b3c1b9fe-5a55-4e9e-9f6c-2f6f2cd13c30',
    symbol = 'JEDI.DE'
WHERE id = '24070ba3-6675-46e7-a2a4-47737dfa2152';

UPDATE index_country_allocations
SET country_code = 'IE'
WHERE id = 'b73db30a-6642-425c-8f30-7822863e7025';

INSERT INTO assets (id, asset_type, "name", symbol, fund_manager, currency_code, exchange_id, company_id)
VALUES
    ('7582b2fb-b552-4766-8f04-a45d1a9efc67', 'ETF', 'iShares MSCI China UCITS ETF USD Acc', 'ICGA.DE', 'iShares', 'EUR', 'b3c1b9fe-5a55-4e9e-9f6c-2f6f2cd13c30', null),
    ('b15f779e-f07d-4eb4-aacf-ff4dec99c18e', 'ETF', 'iShares MSCI EM ex China UCITS ETF USD Acc', 'MTPI.PA', 'iShared', 'EUR', '258de74f-06ce-480b-85c5-2834e05cd7ac', null),
    ('3739f7ad-a484-478b-83e2-07827a65b323', 'ETF', 'iShares MSCI EM Latin America UCITS ETF USD (Dist)', 'LTAM.MI', 'iShares', 'EUR', '08db89ad-c6c3-41d2-864a-650b410e9849', null),
    ('a794ee7b-c1cd-400d-b04d-54e6561f22f1', 'ETF', 'iShares VII PLC - iShares MSCI Japan ETF USD Acc', 'SXR5.DE', 'iShares', 'EUR', 'b3c1b9fe-5a55-4e9e-9f6c-2f6f2cd13c30', null),
    ('dfdbd24b-cc6a-4f3a-8eeb-920da92e663c', 'ETF', 'WisdomTree Uranium and Nuclear Energy UCITS ETF - USD Acc', 'WNUC.DE', 'WisdomTree', 'EUR', 'b3c1b9fe-5a55-4e9e-9f6c-2f6f2cd13c30', null),
    ('d82ef284-f253-4048-beca-820e69634c77', 'ETF', 'Amundi MSCI Korea UCITS ETF Acc', 'LKOR.DE', 'Amundi', 'EUR', 'b3c1b9fe-5a55-4e9e-9f6c-2f6f2cd13c30', null),
    ('b8e7ed55-7ba0-4ae7-a3f0-83329dd61fa9', 'ETF', 'iShares V PLC - iShares MSCI Poland UCITS ETF Acc', 'IBCJ.DE', 'iShares', 'EUR', 'b3c1b9fe-5a55-4e9e-9f6c-2f6f2cd13c30', null),
    ('ea3a0f1a-461f-471d-9038-db0f977b5b16', 'ETF', 'Amundi MSCI Greece UCITS ETF Dist', 'GRE.PA', 'Amundi', 'EUR', '258de74f-06ce-480b-85c5-2834e05cd7ac', null),
    ('1c0f34b9-f0a0-4bec-9a6a-b33cb03a3265', 'ETF', 'VanEck Rare Earth and Strategic Metals UCITS ETF A USD Acc', 'VVMX.DE', 'VanEck', 'EUR', 'b3c1b9fe-5a55-4e9e-9f6c-2f6f2cd13c30', null),
    ('d81be2a1-6161-4fcb-b536-4f06c2189ae2', 'ETF', 'VanEck Uranium and Nuclear Technologies UCITS ETF Acc', 'NUKL.DE', 'VanEck', 'EUR', 'b3c1b9fe-5a55-4e9e-9f6c-2f6f2cd13c30', null),
    ('23796ac2-cc10-4d9a-b513-4399e0d9859a', 'ETF', 'WisdomTree Quantum Computing UCITS ETF - USD Acc', 'WQTM.DE', 'WisdomTree', 'EUR', 'b3c1b9fe-5a55-4e9e-9f6c-2f6f2cd13c30', null)
;

INSERT INTO asset_aliases (id, asset_id, platform, external_symbol)
VALUES
    ('6fa35fa0-5c0c-4ddc-a757-461c565df881', '7582b2fb-b552-4766-8f04-a45d1a9efc67', 'TRADING212', 'ICGA'),
    ('3917af14-b9af-4f87-bf75-905e8a1e7cde', 'b15f779e-f07d-4eb4-aacf-ff4dec99c18e', 'TRADING212', 'MTPI'),
    ('a17c158d-efa1-438d-89f8-52ba7c8f3534', '3739f7ad-a484-478b-83e2-07827a65b323', 'TRADING212', 'LTAM'),
    ('d78c388e-2c0e-4d76-bb44-56731ef8e498', 'a794ee7b-c1cd-400d-b04d-54e6561f22f1', 'TRADING212', 'SXR5'),
    ('649b0c32-bc59-4394-bd5e-40b40a7f61f8', 'dfdbd24b-cc6a-4f3a-8eeb-920da92e663c', 'TRADING212', 'WNUC'),
    ('c92a20c6-0e28-43f1-9417-ff6f7dd52671', 'd82ef284-f253-4048-beca-820e69634c77', 'TRADING212', 'LKOR'),
    ('0b0fac9a-6b82-4a66-9fd8-43694a2ab99d', 'b8e7ed55-7ba0-4ae7-a3f0-83329dd61fa9', 'TRADING212', 'IBCJ'),
    ('29eb4a17-f70e-4fc6-8d01-eda275688688', 'ea3a0f1a-461f-471d-9038-db0f977b5b16', 'TRADING212', 'GRE'),
    ('09a48f0b-1887-470f-9a7e-fbae37dd4262', '1c0f34b9-f0a0-4bec-9a6a-b33cb03a3265', 'TRADING212', 'VVMX'),
    ('6921e8c8-cca9-435d-9bcf-2dba10449393', 'd81be2a1-6161-4fcb-b536-4f06c2189ae2', 'TRADING212', 'NUKL'),
    ('04958b24-7757-426c-b34e-be7dc58920d1', '23796ac2-cc10-4d9a-b513-4399e0d9859a', 'TRADING212', 'WQTM')
;

-- iShares MSCI China UCITS ETF USD Acc
INSERT INTO etf_sector_allocations (id, etf_id, sector_id, weight_percentage)
VALUES
    ('627e8255-8949-4154-b04b-ee963e3e246a', '7582b2fb-b552-4766-8f04-a45d1a9efc67', 'ef8c30ca-2538-4913-b020-ead2e82644e6', 2.60),    --'Energy'                     -- 10
    ('8aac0886-ad4d-4c53-a58b-317cc06dae66', '7582b2fb-b552-4766-8f04-a45d1a9efc67', '0b743204-587a-4ba5-baea-8a9e36805063', 5.23),    --'Materials'                  -- 15
    ('9b2568b2-e568-40c8-a30c-ae1f537fb38d', '7582b2fb-b552-4766-8f04-a45d1a9efc67', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 4.80),    --'Industrials'                -- 20
    ('2a62adab-327b-4e44-b731-dd4f5c1f77fc', '7582b2fb-b552-4766-8f04-a45d1a9efc67', '528fa9c4-bb5a-4d58-818f-71e65be26b35', 27.45),    --'Consumer Discretionary'    -- 25
    ('015856c4-0393-47cb-af22-a5578adec049', '7582b2fb-b552-4766-8f04-a45d1a9efc67', '134d7ecb-7480-44f9-a6b5-8e9211212f1c', 3.13),     --'Consumer Staples'          -- 30
    ('5347792e-6cf2-4296-a975-b0a369655cd0', '7582b2fb-b552-4766-8f04-a45d1a9efc67', '75536f22-aa7f-4bb3-95c9-c7ae66295a13', 4.99),     --'Health Care'               -- 35
    ('276174d7-08ee-48fb-ab15-aae3aaa07745', '7582b2fb-b552-4766-8f04-a45d1a9efc67', 'a45f3a5a-0319-4dcb-86e1-9478b16adfa4', 17.59),     --'Financials'               -- 40
    ('c3679fc9-3d80-49d7-b9ff-435a615d7fca', '7582b2fb-b552-4766-8f04-a45d1a9efc67', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 8.03),     --'Information Technology'   -- 45
    ('244891b1-092d-41e4-943b-1da25a04c86d', '7582b2fb-b552-4766-8f04-a45d1a9efc67', 'ee4c5e5c-def2-4965-8779-00b32b1edf70', 27.45),     --'Communication Services'   -- 50
    ('62d3ab6a-03fa-4184-a6c7-aabdb1378c72', '7582b2fb-b552-4766-8f04-a45d1a9efc67', '1a09f1f7-628b-4f9a-a05c-0368cbc3a225', 1.76),     --'Utilities'                 -- 55
    ('2eddb0d0-652b-44bd-9651-3970ffa9dfb3', '7582b2fb-b552-4766-8f04-a45d1a9efc67', 'cf1d4f4e-3b6a-4c2e-9f7a-8e9d6c5b4a3f', 1.47)        --'Real Estate'             -- 60
ON CONFLICT (id) DO NOTHING;

INSERT INTO etf_country_allocations (id, etf_id, country_code, weight_percentage)
VALUES
    ('609f42bd-ae0b-40df-bdd0-e1ace497e18e', '7582b2fb-b552-4766-8f04-a45d1a9efc67', 'CN', 99.68)
    ON CONFLICT (id) DO NOTHING;

-- iShares MSCI EM ex China UCITS ETF USD Acc
INSERT INTO etf_sector_allocations (id, etf_id, sector_id, weight_percentage)
VALUES
    ('8c3098ab-3e02-4bcb-8761-a8abb0f16f46', 'b15f779e-f07d-4eb4-aacf-ff4dec99c18e', 'ef8c30ca-2538-4913-b020-ead2e82644e6', 3.36),    --'Energy'                     -- 10
    ('42771996-7ab8-4eec-9e15-e15876e6ac0e', 'b15f779e-f07d-4eb4-aacf-ff4dec99c18e', '0b743204-587a-4ba5-baea-8a9e36805063', 6.96),    --'Materials'                  -- 15
    ('051ceb22-b394-425a-acf1-6181904e4f36', 'b15f779e-f07d-4eb4-aacf-ff4dec99c18e', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 7.44),    --'Industrials'                -- 20
    ('16bac97e-fff4-4163-93d5-a0d92fab736f', 'b15f779e-f07d-4eb4-aacf-ff4dec99c18e', '528fa9c4-bb5a-4d58-818f-71e65be26b35', 5.32),    --'Consumer Discretionary'    -- 25
    ('f618ca18-2d2d-4f42-9685-ba21c2937df9', 'b15f779e-f07d-4eb4-aacf-ff4dec99c18e', '134d7ecb-7480-44f9-a6b5-8e9211212f1c', 3.45),     --'Consumer Staples'          -- 30
    ('51c7bc1a-2d3b-429e-9559-42069fc87a97', 'b15f779e-f07d-4eb4-aacf-ff4dec99c18e', '75536f22-aa7f-4bb3-95c9-c7ae66295a13', 2.44),     --'Health Care'               -- 35
    ('eab62e6c-2afd-44f9-b168-5f295af18026', 'b15f779e-f07d-4eb4-aacf-ff4dec99c18e', 'a45f3a5a-0319-4dcb-86e1-9478b16adfa4', 26.76),     --'Financials'               -- 40
    ('0dfa4575-47e0-4978-81aa-be61bd2563d5', 'b15f779e-f07d-4eb4-aacf-ff4dec99c18e', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 37.10),     --'Information Technology'   -- 45
    ('7ebabff5-3a55-47a4-ace3-09bb66f9a6e6', 'b15f779e-f07d-4eb4-aacf-ff4dec99c18e', 'ee4c5e5c-def2-4965-8779-00b32b1edf70', 3.93),     --'Communication Services'   -- 50
    ('8cfc5e2d-3e23-4b13-9341-6d13a17308c7', 'b15f779e-f07d-4eb4-aacf-ff4dec99c18e', '1a09f1f7-628b-4f9a-a05c-0368cbc3a225', 1.70),     --'Utilities'                 -- 55
    ('3f411603-9332-47ac-9e99-ce0e882f3065', 'b15f779e-f07d-4eb4-aacf-ff4dec99c18e', 'cf1d4f4e-3b6a-4c2e-9f7a-8e9d6c5b4a3f', 1.28)       --'Real Estate'             -- 60
    ON CONFLICT (id) DO NOTHING;

INSERT INTO etf_country_allocations (id, etf_id, country_code, weight_percentage)
VALUES
    ('c01f170b-2973-4e9a-8c96-bb2c34aa52e8', 'b15f779e-f07d-4eb4-aacf-ff4dec99c18e', 'TW', 28.66),
    ('18aafbc7-f826-47aa-aabb-c645448ccbe8', 'b15f779e-f07d-4eb4-aacf-ff4dec99c18e', 'IN', 20.01),
    ('9c7c88b4-a890-408f-be11-6ecb9c348502', 'b15f779e-f07d-4eb4-aacf-ff4dec99c18e', 'KR', 19.46),
    ('7fc7b916-2a7f-46b3-8c8a-0fa7ca0e5547', 'b15f779e-f07d-4eb4-aacf-ff4dec99c18e', 'DE', 5.99),
    ('00bb4cc1-5fd6-4a50-b926-8ea961e19096', 'b15f779e-f07d-4eb4-aacf-ff4dec99c18e', 'ZA', 5.20),
    ('60624083-eb94-4b47-820d-21bcb1949d4c', 'b15f779e-f07d-4eb4-aacf-ff4dec99c18e', 'SA', 3.83),
    ('f07ce6a4-dab6-455e-b2b7-379b2ee3cc0a', 'b15f779e-f07d-4eb4-aacf-ff4dec99c18e', 'MX', 2.65),
    ('c05e88c8-31d9-4705-88ee-671de461dc94', 'b15f779e-f07d-4eb4-aacf-ff4dec99c18e', 'AE', 1.95),
    ('5b32a872-69be-4b56-812c-6d2ff02d1aa7', 'b15f779e-f07d-4eb4-aacf-ff4dec99c18e', 'MY', 1.61),
    ('b7c9dadf-25c9-46e3-b620-ab180a063e65', 'b15f779e-f07d-4eb4-aacf-ff4dec99c18e', 'ID', 1.55)
    ON CONFLICT (id) DO NOTHING;

-- iShares MSCI EM Latin America UCITS ETF USD (Dist)
INSERT INTO etf_sector_allocations (id, etf_id, sector_id, weight_percentage)
VALUES
    ('2fcf8e60-3661-46b0-932b-54ee06b29035', '3739f7ad-a484-478b-83e2-07827a65b323', 'ef8c30ca-2538-4913-b020-ead2e82644e6', 6.11),    --'Energy'                     -- 10
    ('7bc68cf9-c5ad-49d6-b25d-41d89a44c077', '3739f7ad-a484-478b-83e2-07827a65b323', '0b743204-587a-4ba5-baea-8a9e36805063', 19.24),    --'Materials'                  -- 15
    ('2bde6f62-f2bd-48c9-92a7-33c552e84d75', '3739f7ad-a484-478b-83e2-07827a65b323', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 9.08),    --'Industrials'                -- 20
    ('d7764338-5ebe-4b56-8034-a1ed3717dd84', '3739f7ad-a484-478b-83e2-07827a65b323', '528fa9c4-bb5a-4d58-818f-71e65be26b35', 1.86),    --'Consumer Discretionary'    -- 25
    ('c3eeef47-c44d-4a57-a58f-c1065812f019', '3739f7ad-a484-478b-83e2-07827a65b323', '134d7ecb-7480-44f9-a6b5-8e9211212f1c', 11.20),     --'Consumer Staples'          -- 30
    ('cab5bb4e-a204-464e-ba48-6955cb88d60e', '3739f7ad-a484-478b-83e2-07827a65b323', '75536f22-aa7f-4bb3-95c9-c7ae66295a13', 0.68),     --'Health Care'               -- 35
    ('487bd6cf-7520-40a2-980f-e9bc08f9f93f', '3739f7ad-a484-478b-83e2-07827a65b323', 'a45f3a5a-0319-4dcb-86e1-9478b16adfa4', 38.62),     --'Financials'               -- 40
    ('6c230194-08ab-4624-b4a6-17c5ac6f9433', '3739f7ad-a484-478b-83e2-07827a65b323', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 0.49),     --'Information Technology'   -- 45
    ('d29989a4-0978-4238-b65e-24b59052d03d', '3739f7ad-a484-478b-83e2-07827a65b323', 'ee4c5e5c-def2-4965-8779-00b32b1edf70', 3.29),     --'Communication Services'   -- 50
    ('931bac91-b2f1-4d55-924c-578e242c06c9', '3739f7ad-a484-478b-83e2-07827a65b323', '1a09f1f7-628b-4f9a-a05c-0368cbc3a225', 7.08),     --'Utilities'                 -- 55
    ('8472447f-adc6-4315-b863-de8678453cbd', '3739f7ad-a484-478b-83e2-07827a65b323', 'cf1d4f4e-3b6a-4c2e-9f7a-8e9d6c5b4a3f', 1.52)        --'Real Estate'             -- 60
    ON CONFLICT (id) DO NOTHING;

INSERT INTO etf_country_allocations (id, etf_id, country_code, weight_percentage)
VALUES
    ('9771126a-3404-452c-9941-a6a2beaf0661', '3739f7ad-a484-478b-83e2-07827a65b323', 'BR', 50.71),
    ('4cdd645e-ce98-430e-b312-f61001b7fdf3', '3739f7ad-a484-478b-83e2-07827a65b323', 'MX', 25.95),
    ('ad769799-3c80-4235-b92f-43571a72a5a5', '3739f7ad-a484-478b-83e2-07827a65b323', 'CL', 7.79),
    ('6d787ef7-fc69-4a03-ad55-eef8fe1f3f7b', '3739f7ad-a484-478b-83e2-07827a65b323', 'DE', 7.43),
    ('0ce09883-f0bb-4f8b-b31d-d39c5dc32292', '3739f7ad-a484-478b-83e2-07827a65b323', 'PE', 5.22),
    ('caab33d9-a9d5-4418-8cdf-9b6f7b0d57a2', '3739f7ad-a484-478b-83e2-07827a65b323', 'CO', 2.08)
    ON CONFLICT (id) DO NOTHING;


-- iShares VII PLC - iShares MSCI Japan ETF USD Acc

UPDATE assets
SET tracked_index_id = '006a178f-3973-439b-a2cc-1dce1e24a915'
WHERE id = 'a794ee7b-c1cd-400d-b04d-54e6561f22f1';

-- WisdomTree Uranium and Nuclear Energy UCITS ETF - USD Acc
INSERT INTO etf_sector_allocations (id, etf_id, sector_id, weight_percentage)
VALUES
    ('8e51918a-70a3-4dd0-ae98-383cff6f7e8b', 'dfdbd24b-cc6a-4f3a-8eeb-920da92e663c', 'ef8c30ca-2538-4913-b020-ead2e82644e6', 61.93),    --'Energy'                     -- 10
    ('6eb21e7f-bc54-40b6-8cae-c2f6f1c1d8ea', 'dfdbd24b-cc6a-4f3a-8eeb-920da92e663c', '0b743204-587a-4ba5-baea-8a9e36805063', 4.19),    --'Materials'                  -- 15
    ('df633e51-1df9-4a14-8cd2-041f65b92fe5', 'dfdbd24b-cc6a-4f3a-8eeb-920da92e663c', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 27.71),    --'Industrials'                -- 20
    ('70f6c67a-c87c-4aa9-8c76-2dbb93f10ef8', 'dfdbd24b-cc6a-4f3a-8eeb-920da92e663c', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 1.56),     --'Information Technology'   -- 45
    ('9bb19701-0301-43a0-8d58-d467fba750b0', 'dfdbd24b-cc6a-4f3a-8eeb-920da92e663c', '1a09f1f7-628b-4f9a-a05c-0368cbc3a225', 4.61)     --'Utilities'                 -- 55
    ON CONFLICT (id) DO NOTHING;

INSERT INTO etf_country_allocations (id, etf_id, country_code, weight_percentage)
VALUES
    ('61259070-bfaf-44d4-99a8-3b23083ba118', 'dfdbd24b-cc6a-4f3a-8eeb-920da92e663c', 'US', 44.32),
    ('0435cc2f-2739-4e3b-9035-8b8feb0889ab', 'dfdbd24b-cc6a-4f3a-8eeb-920da92e663c', 'CA', 19),
    ('946cc6a8-7a07-411b-a563-a10ecc3627b6', 'dfdbd24b-cc6a-4f3a-8eeb-920da92e663c', 'AU', 11.49),
    ('677a8acf-631c-46f9-a2f6-582d022a00ce', 'dfdbd24b-cc6a-4f3a-8eeb-920da92e663c', 'HK', 7.84),
    ('dcc8714f-cfc8-4325-865b-95f8091b0ae7', 'dfdbd24b-cc6a-4f3a-8eeb-920da92e663c', 'KR', 7.68),
    ('2dfefe84-60e6-4877-a39a-496bf105e0a4', 'dfdbd24b-cc6a-4f3a-8eeb-920da92e663c', 'GB', 3.81),
    ('15b2fcde-26d6-4c48-aa0c-c4dcb57b5317', 'dfdbd24b-cc6a-4f3a-8eeb-920da92e663c', 'FR', 1.79),
    ('aa06eae0-2a3a-4efd-8aad-b968502e37c5', 'dfdbd24b-cc6a-4f3a-8eeb-920da92e663c', 'CN', 1.53),
    ('3a696e15-a670-46cb-aae1-06a80cb63628', 'dfdbd24b-cc6a-4f3a-8eeb-920da92e663c', 'KZ', 0.87),
    ('2eb936ca-a624-4e25-ab1e-6ca8c62d3ac8', 'dfdbd24b-cc6a-4f3a-8eeb-920da92e663c', 'JP', 0.47)
    ON CONFLICT (id) DO NOTHING;


-- Amundi MSCI Korea UCITS ETF Acc
INSERT INTO etf_sector_allocations (id, etf_id, sector_id, weight_percentage)
VALUES
    ('721fe68a-03db-4ad9-82b2-a603d674bddc', 'd82ef284-f253-4048-beca-820e69634c77', 'ef8c30ca-2538-4913-b020-ead2e82644e6', 1.11),    --'Energy'                     -- 10
    ('7ed9c399-663e-4fef-a64e-2a4eff7efd5d', 'd82ef284-f253-4048-beca-820e69634c77', '0b743204-587a-4ba5-baea-8a9e36805063', 2.60),    --'Materials'                  -- 15
    ('6437ac59-489d-463a-9ccc-ce07e3ff2be9', 'd82ef284-f253-4048-beca-820e69634c77', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 18.74),    --'Industrials'                -- 20
    ('2df18b0f-2f57-477b-921f-14f52d22717a', 'd82ef284-f253-4048-beca-820e69634c77', '528fa9c4-bb5a-4d58-818f-71e65be26b35', 6.29),    --'Consumer Discretionary'    -- 25
    ('3b2ac1df-234e-4799-8a0e-12bb0353a378', 'd82ef284-f253-4048-beca-820e69634c77', '134d7ecb-7480-44f9-a6b5-8e9211212f1c', 1.83),     --'Consumer Staples'          -- 30
    ('89899559-2375-4eb1-b740-20b4bcfea728', 'd82ef284-f253-4048-beca-820e69634c77', '75536f22-aa7f-4bb3-95c9-c7ae66295a13', 4.75),     --'Health Care'               -- 35
    ('11d997d6-939c-42d3-a48d-1d51e8fe1c99', 'd82ef284-f253-4048-beca-820e69634c77', 'a45f3a5a-0319-4dcb-86e1-9478b16adfa4', 10.51),     --'Financials'               -- 40
    ('e5b5b7b5-ecf2-496f-9323-6cee678b88f9', 'd82ef284-f253-4048-beca-820e69634c77', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 49.16),     --'Information Technology'   -- 45
    ('a9a7c800-777d-4352-ab2f-463ebe1326c4', 'd82ef284-f253-4048-beca-820e69634c77', 'ee4c5e5c-def2-4965-8779-00b32b1edf70', 4.25),     --'Communication Services'   -- 50
    ('149b864c-3208-4b8b-a2f2-a1f53de978ee', 'd82ef284-f253-4048-beca-820e69634c77', '1a09f1f7-628b-4f9a-a05c-0368cbc3a225', 0.76)     --'Utilities'                 -- 55
    ON CONFLICT (id) DO NOTHING;

INSERT INTO etf_country_allocations (id, etf_id, country_code, weight_percentage)
VALUES
    ('5d8bc7d4-9dd1-44f6-98d4-eda45601a263', 'd82ef284-f253-4048-beca-820e69634c77', 'KR', 100)
    ON CONFLICT (id) DO NOTHING;

-- iShares V PLC - iShares MSCI Poland UCITS ETF Acc
INSERT INTO etf_sector_allocations (id, etf_id, sector_id, weight_percentage)
VALUES
    ('23059fc6-c576-4eb8-b82b-fdd34294ed9c', 'b8e7ed55-7ba0-4ae7-a3f0-83329dd61fa9', 'ef8c30ca-2538-4913-b020-ead2e82644e6', 13.15),    --'Energy'                     -- 10
    ('b322af37-11e0-4522-a036-ea3e9486b2c1', 'b8e7ed55-7ba0-4ae7-a3f0-83329dd61fa9', '0b743204-587a-4ba5-baea-8a9e36805063', 9.82),    --'Materials'                  -- 15
    ('4b15cb56-4347-4b4a-a451-e603f7f4e174', 'b8e7ed55-7ba0-4ae7-a3f0-83329dd61fa9', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 2.02),    --'Industrials'                -- 20
    ('f5111b63-f9c5-477e-b1c3-68947d6fd595', 'b8e7ed55-7ba0-4ae7-a3f0-83329dd61fa9', '528fa9c4-bb5a-4d58-818f-71e65be26b35', 13.31),    --'Consumer Discretionary'    -- 25
    ('ac9a1164-6475-43fd-951e-14ea9034ff47', 'b8e7ed55-7ba0-4ae7-a3f0-83329dd61fa9', '134d7ecb-7480-44f9-a6b5-8e9211212f1c', 7.08),     --'Consumer Staples'          -- 30
    ('baba2527-9962-4268-b858-afee40429b1f', 'b8e7ed55-7ba0-4ae7-a3f0-83329dd61fa9', 'a45f3a5a-0319-4dcb-86e1-9478b16adfa4', 48.59),     --'Financials'               -- 40
    ('d77195d6-1351-443c-a436-5fd68568da0c', 'b8e7ed55-7ba0-4ae7-a3f0-83329dd61fa9', 'ee4c5e5c-def2-4965-8779-00b32b1edf70', 3.68),     --'Communication Services'   -- 50
    ('1e0d04b0-8143-4904-a1ca-3c7844418dd9', 'b8e7ed55-7ba0-4ae7-a3f0-83329dd61fa9', '1a09f1f7-628b-4f9a-a05c-0368cbc3a225', 1.97)     --'Utilities'                 -- 55
    ON CONFLICT (id) DO NOTHING;

INSERT INTO etf_country_allocations (id, etf_id, country_code, weight_percentage)
VALUES
    ('df637f60-156f-42ce-830c-cf19c3a3b13f', 'b8e7ed55-7ba0-4ae7-a3f0-83329dd61fa9', 'PL', 99.62)
    ON CONFLICT (id) DO NOTHING;

-- Amundi MSCI Greece UCITS ETF Dist
INSERT INTO etf_sector_allocations (id, etf_id, sector_id, weight_percentage)
VALUES
    ('08744c46-66c7-44cf-be74-8879033b4d4f', 'ea3a0f1a-461f-471d-9038-db0f977b5b16', 'ef8c30ca-2538-4913-b020-ead2e82644e6', 3.71),    --'Energy'                     -- 10
    ('0cd4e462-9f55-49be-8c0c-9cb2a03ab2de', 'ea3a0f1a-461f-471d-9038-db0f977b5b16', '0b743204-587a-4ba5-baea-8a9e36805063', 3.22),    --'Materials'                  -- 15
    ('9a7ab63b-9aa7-4024-94d8-9f3111a82320', 'ea3a0f1a-461f-471d-9038-db0f977b5b16', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 6.41),    --'Industrials'                -- 20
    ('869d1c90-a02c-4d1f-b61f-acef2b68da8b', 'ea3a0f1a-461f-471d-9038-db0f977b5b16', '528fa9c4-bb5a-4d58-818f-71e65be26b35', 10.20),    --'Consumer Discretionary'    -- 25
    ('eb851121-f46d-4e50-a6cc-74155f0cf5d2', 'ea3a0f1a-461f-471d-9038-db0f977b5b16', '134d7ecb-7480-44f9-a6b5-8e9211212f1c', 13.69),     --'Consumer Staples'          -- 30
    ('dedc314a-ce2c-4ee0-97ee-47ba515177ff', 'ea3a0f1a-461f-471d-9038-db0f977b5b16', 'a45f3a5a-0319-4dcb-86e1-9478b16adfa4', 52.87),     --'Financials'               -- 40
    ('f83e56a8-cbd7-4fb3-b25c-997c83d9cccf', 'ea3a0f1a-461f-471d-9038-db0f977b5b16', 'ee4c5e5c-def2-4965-8779-00b32b1edf70', 3.88),     --'Communication Services'   -- 50
    ('52d011d3-30aa-4156-b2dd-599969a97858', 'ea3a0f1a-461f-471d-9038-db0f977b5b16', '1a09f1f7-628b-4f9a-a05c-0368cbc3a225', 5.41),     --'Utilities'                 -- 55
    ('b660a13e-aa68-4330-92e0-2ec360498b52', 'ea3a0f1a-461f-471d-9038-db0f977b5b16', 'cf1d4f4e-3b6a-4c2e-9f7a-8e9d6c5b4a3f', 0.61)        --'Real Estate'             -- 60
    ON CONFLICT (id) DO NOTHING;

INSERT INTO etf_country_allocations (id, etf_id, country_code, weight_percentage)
VALUES
    ('b664138d-e402-4123-a22e-e26b2328fbeb', 'ea3a0f1a-461f-471d-9038-db0f977b5b16', 'GR', 86.75),
    ('459b26fa-12df-4d21-afde-092666511c4e', 'ea3a0f1a-461f-471d-9038-db0f977b5b16', 'GB', 13.25)
    ON CONFLICT (id) DO NOTHING;

-- VanEck Rare Earth and Strategic Metals UCITS ETF A USD Acc
INSERT INTO etf_sector_allocations (id, etf_id, sector_id, weight_percentage)
VALUES
    ('11feaf75-1bc3-4b23-9815-bb87bea0ef6b', '1c0f34b9-f0a0-4bec-9a6a-b33cb03a3265', '0b743204-587a-4ba5-baea-8a9e36805063', 100)    --'Materials'                  -- 15
    ON CONFLICT (id) DO NOTHING;

INSERT INTO etf_country_allocations (id, etf_id, country_code, weight_percentage)
VALUES
    ('72d5f6a2-6347-4f51-bc9f-3efd3479587c', '1c0f34b9-f0a0-4bec-9a6a-b33cb03a3265', 'CN', 32.13),
    ('330ebd66-9c7e-4a9d-a7c5-bdd100486910', '1c0f34b9-f0a0-4bec-9a6a-b33cb03a3265', 'AU', 20.03),
    ('428e7b50-c095-4b4d-b8df-d04a3ae0db50', '1c0f34b9-f0a0-4bec-9a6a-b33cb03a3265', 'US', 18.25),
    ('cb86f56d-4282-44c9-94f8-0a7ad2c90e5e', '1c0f34b9-f0a0-4bec-9a6a-b33cb03a3265', 'CA', 13.36),
    ('c3df08c8-c3da-4309-9708-cb675b02f841', '1c0f34b9-f0a0-4bec-9a6a-b33cb03a3265', 'CL', 5.71),
    ('33025e0c-2bf0-4a25-b64c-b300b5248e12', '1c0f34b9-f0a0-4bec-9a6a-b33cb03a3265', 'NL', 3.34),
    ('44d37eca-dda8-4112-ba2b-7ea4a04ca501', '1c0f34b9-f0a0-4bec-9a6a-b33cb03a3265', 'BR', 2.58),
    ('317f67b7-0c13-4b3a-a0e5-a52c2785b639', '1c0f34b9-f0a0-4bec-9a6a-b33cb03a3265', 'DE', 2.44),
    ('be1893a3-a271-47b8-b94f-39e0bab687fa', '1c0f34b9-f0a0-4bec-9a6a-b33cb03a3265', 'FR', 2.16)
    ON CONFLICT (id) DO NOTHING;

-- VanEck Uranium and Nuclear Technologies UCITS ETF Acc
INSERT INTO etf_sector_allocations (id, etf_id, sector_id, weight_percentage)
VALUES
    ('cd44ea69-13f3-4e34-b7c5-c38d995304af', 'd81be2a1-6161-4fcb-b536-4f06c2189ae2', 'ef8c30ca-2538-4913-b020-ead2e82644e6', 48.4),    --'Energy'                     -- 10        -- 15
    ('6329073d-1671-45bb-b3c3-71c22a92060e', 'd81be2a1-6161-4fcb-b536-4f06c2189ae2', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 44.0),    --'Industrials'                -- 20
    ('3d7a83c4-fff1-45b3-827e-6dcbe647c473', 'd81be2a1-6161-4fcb-b536-4f06c2189ae2', '1a09f1f7-628b-4f9a-a05c-0368cbc3a225', 7.5)     --'Utilities'                 -- 55
    ON CONFLICT (id) DO NOTHING;

INSERT INTO etf_country_allocations (id, etf_id, country_code, weight_percentage)
VALUES
    ('54bab5e6-2967-4626-8458-393711ea575f', 'd81be2a1-6161-4fcb-b536-4f06c2189ae2', 'CA', 35.38),
    ('cf449b29-fc4a-42dd-b3b3-b698e7e66bad', 'd81be2a1-6161-4fcb-b536-4f06c2189ae2', 'US', 26.91),
    ('7272ee4b-f8ef-43df-8d0e-559bbdf26a19', 'd81be2a1-6161-4fcb-b536-4f06c2189ae2', 'JP', 23.00),
    ('98194a72-7517-4cb3-81bc-a5a85234bc16', 'd81be2a1-6161-4fcb-b536-4f06c2189ae2', 'KR', 6.31),
    ('dfe56bb4-36ee-4f39-bef8-0bcb61e39206', 'd81be2a1-6161-4fcb-b536-4f06c2189ae2', 'AU', 4.28),
    ('1f584752-3d07-4088-a3cc-e4b985a95a8e', 'd81be2a1-6161-4fcb-b536-4f06c2189ae2', 'GB', 1.67),
    ('7cf91584-20ce-4f6d-bcd6-a1b3d3ae1221', 'd81be2a1-6161-4fcb-b536-4f06c2189ae2', 'CN', 1.45),
    ('c9ebb987-d5c0-4bbb-8dc4-1c0c26c815b6', 'd81be2a1-6161-4fcb-b536-4f06c2189ae2', 'KZ', 0.94)
    ON CONFLICT (id) DO NOTHING;

-- WisdomTree Quantum Computing UCITS ETF - USD Acc
INSERT INTO etf_sector_allocations (id, etf_id, sector_id, weight_percentage)
VALUES
    ('ebd4c5df-4525-428a-964f-eb9ebff2813b', '23796ac2-cc10-4d9a-b513-4399e0d9859a', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 4.53),    --'Industrials'                -- 20
    ('2c2de598-276e-4708-95f7-fbf17c54201f', '23796ac2-cc10-4d9a-b513-4399e0d9859a', '528fa9c4-bb5a-4d58-818f-71e65be26b35', 3.06),    --'Consumer Discretionary'    -- 25
    ('6573843a-a6a9-46c9-b929-36e10c5a4342', '23796ac2-cc10-4d9a-b513-4399e0d9859a', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 81.94),     --'Information Technology'   -- 45
    ('893948f8-b1ea-4584-afaa-a6bb014899af', '23796ac2-cc10-4d9a-b513-4399e0d9859a', 'ee4c5e5c-def2-4965-8779-00b32b1edf70', 10.47)     --'Communication Services'   -- 50
    ON CONFLICT (id) DO NOTHING;

INSERT INTO etf_country_allocations (id, etf_id, country_code, weight_percentage)
VALUES
    ('d0d97b13-f9c3-42c8-9055-7009bbfbadb9', '23796ac2-cc10-4d9a-b513-4399e0d9859a', 'US', 63.72),
    ('5e673ae4-7432-4a49-be4f-f18d02853d47', '23796ac2-cc10-4d9a-b513-4399e0d9859a', 'JP', 9.85),
    ('76643b7e-c566-4ae1-9e2d-ad3c36b40c9e', '23796ac2-cc10-4d9a-b513-4399e0d9859a', 'CA', 7.18),
    ('7c4be28e-e274-4a74-9f18-c5ae973d4bc5', '23796ac2-cc10-4d9a-b513-4399e0d9859a', 'NL', 5.78),
    ('7ebe69b9-a359-48f1-a9aa-90c036769c7b', '23796ac2-cc10-4d9a-b513-4399e0d9859a', 'DE', 4.22),
    ('19d6ce04-6cdd-4275-a20b-175de53ef287', '23796ac2-cc10-4d9a-b513-4399e0d9859a', 'GB', 2.94),
    ('d32379dc-19f7-4ed9-aa76-e79070fa9455', '23796ac2-cc10-4d9a-b513-4399e0d9859a', 'KR', 2.30),
    ('b7cbaa97-74d4-4d2d-83ce-b7f84eee3c6d', '23796ac2-cc10-4d9a-b513-4399e0d9859a', 'FI', 2.18),
    ('ed1cc47f-533d-42bb-81d0-86fa59222a77', '23796ac2-cc10-4d9a-b513-4399e0d9859a', 'TW', 1.83)
    ON CONFLICT (id) DO NOTHING;