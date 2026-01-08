INSERT INTO assets (id, asset_type, "name", symbol, fund_manager, currency_code, exchange_id, company_id)
VALUES
    -- MSCI Europe Index
    ('a9349857-c8f4-4d84-8439-2765013dc9ac', 'INDEX', 'MSCI Europe Index', '^990500-EUR-STRD', NULL, 'EUR', NULL, NULL),

    -- FTSE All-World Index
    ('54a3dda8-3b86-4768-bcbf-5149061f5375', 'INDEX', 'FTSE All-World Index', '^AW04.FGI', NULL, 'USD', NULL, NULL),

    -- MSCI Japan Index
    ('006a178f-3973-439b-a2cc-1dce1e24a915', 'INDEX', 'MSCI Japan Index', '^EWJ-EU', NULL, 'USD', NULL, NULL),

    -- MSCI EMU Index
    ('4a965999-ed1f-4b5e-9709-ce8e6a6ffbf8', 'INDEX', 'MSCI EMU Index', 'EUNM.L', NULL, 'GBP', NULL, NULL),

    -- MSCI World Index
    ('327aa8ef-11b2-48b5-962a-d7266bcb549b', 'INDEX', 'MSCI World Index', '^990100-USD-STRD', NULL, 'USD', NULL, NULL)
;

-- MSCI Europe Index
INSERT INTO public.index_sector_allocations (id, index_id, sector_id, weight_percentage)
VALUES
    ('ed2758c8-a6ec-4ffe-9a15-e5eca6a7cf29', 'a9349857-c8f4-4d84-8439-2765013dc9ac', 'ef8c30ca-2538-4913-b020-ead2e82644e6', 4.10),    --'Energy'                     -- 10
    ('74d76844-2a4f-4876-b186-d3f59a1dd3ed', 'a9349857-c8f4-4d84-8439-2765013dc9ac', '0b743204-587a-4ba5-baea-8a9e36805063', 5.07),    --'Materials'                  -- 15
    ('c6965018-d9c1-4349-b00f-fc0f9dc276f0', 'a9349857-c8f4-4d84-8439-2765013dc9ac', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 18.98),    --'Industrials'                -- 20
    ('40d93c26-3563-4764-9722-3c53dc712d20', 'a9349857-c8f4-4d84-8439-2765013dc9ac', '528fa9c4-bb5a-4d58-818f-71e65be26b35', 8.06),     --'Consumer Discretionary'    -- 25
    ('180880d3-7efd-47c9-963d-276dce2b0b44', 'a9349857-c8f4-4d84-8439-2765013dc9ac', '134d7ecb-7480-44f9-a6b5-8e9211212f1c', 9.22),     --'Consumer Staples'          -- 30
    ('ddcda402-a2f7-4f4d-a141-b5f18049ad8e', 'a9349857-c8f4-4d84-8439-2765013dc9ac', '75536f22-aa7f-4bb3-95c9-c7ae66295a13', 13.85),    --'Health Care'               -- 35
    ('7496baeb-873a-4258-94a7-2c03c04dd079', 'a9349857-c8f4-4d84-8439-2765013dc9ac', 'a45f3a5a-0319-4dcb-86e1-9478b16adfa4', 23.97),     --'Financials'               -- 40
    ('f58788ff-c3ed-47a0-a4fe-0092deb4c3b6', 'a9349857-c8f4-4d84-8439-2765013dc9ac', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 7.43),      --'Information Technology'   -- 45
    ('902f8930-b616-4f7c-a728-1c62ca1f0753', 'a9349857-c8f4-4d84-8439-2765013dc9ac', 'ee4c5e5c-def2-4965-8779-00b32b1edf70', 3.57),     --'Communication Services'   -- 50
    ('b2b01c9d-e269-465d-a436-8ddae3a21aff', 'a9349857-c8f4-4d84-8439-2765013dc9ac', '1a09f1f7-628b-4f9a-a05c-0368cbc3a225', 4.52),     --'Utilities'                 -- 55
    ('43355606-bda4-4f79-a86d-c14eec52e5a8', 'a9349857-c8f4-4d84-8439-2765013dc9ac', 'cf1d4f4e-3b6a-4c2e-9f7a-8e9d6c5b4a3f', 0.70)        --'Real Estate'             -- 60
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.index_country_allocations (id, index_id, country_code, weight_percentage)
VALUES
    ('1b338123-fb21-42ec-a0cd-5c08be1ca716', 'a9349857-c8f4-4d84-8439-2765013dc9ac', 'GB', 21.98),
    ('7330b62b-ee1e-46ac-94b8-92e0b072408b', 'a9349857-c8f4-4d84-8439-2765013dc9ac', 'FR', 16.16),
    ('6976961a-ed66-49fe-a7e1-fc2ef153fc0f', 'a9349857-c8f4-4d84-8439-2765013dc9ac', 'DE', 14.67),
    ('96f7a6e2-a480-409a-9b96-9a5c321e0414', 'a9349857-c8f4-4d84-8439-2765013dc9ac', 'CH', 14.23),
    ('4be6a977-93ed-46c5-aec8-95d249d93112', 'a9349857-c8f4-4d84-8439-2765013dc9ac', 'NL', 7.58),
    ('54075385-2363-440d-915c-a2d0ae48d6c8', 'a9349857-c8f4-4d84-8439-2765013dc9ac', 'ES', 5.82),
    ('3f8fef72-9aed-4391-82de-3db29be6ff8f', 'a9349857-c8f4-4d84-8439-2765013dc9ac', 'SE', 5.56),
    ('191a6d0e-0827-4a89-8681-14a85a48cc80', 'a9349857-c8f4-4d84-8439-2765013dc9ac', 'IT', 4.87),
    ('0df48f34-3a54-4d90-b548-809be0dbf4f6', 'a9349857-c8f4-4d84-8439-2765013dc9ac', 'DK', 2.92),
    ('762f9e96-5e72-4cfb-b6db-041091304841', 'a9349857-c8f4-4d84-8439-2765013dc9ac', 'FI', 1.72)
ON CONFLICT (id) DO NOTHING;

-- iShares Core MSCI Europe UCITS ETF EUR (Acc)
UPDATE assets
SET tracked_index_id = 'a9349857-c8f4-4d84-8439-2765013dc9ac'
WHERE id = '8fc5029f-8fa1-4874-adc8-07efbc6c9d5e';
DELETE FROM etf_sector_allocations WHERE etf_id = '8fc5029f-8fa1-4874-adc8-07efbc6c9d5e';
DELETE FROM etf_country_allocations WHERE etf_id = '8fc5029f-8fa1-4874-adc8-07efbc6c9d5e';

-- iShares MSCI Europe ESG Enhanced CTB UCITS ETF EUR Inc (Dist)
UPDATE assets
SET tracked_index_id = 'a9349857-c8f4-4d84-8439-2765013dc9ac'
WHERE id = '3cd133b1-1207-40fe-b91b-ab3e62afbfa6';
DELETE FROM etf_sector_allocations WHERE etf_id = '3cd133b1-1207-40fe-b91b-ab3e62afbfa6';
DELETE FROM etf_country_allocations WHERE etf_id = '3cd133b1-1207-40fe-b91b-ab3e62afbfa6';

-- FTSE All-World Index
INSERT INTO public.index_sector_allocations (id, index_id, sector_id, weight_percentage)
VALUES
    ('978a07a9-d43d-464a-8c92-4e6a59e7806b', '54a3dda8-3b86-4768-bcbf-5149061f5375', 'ef8c30ca-2538-4913-b020-ead2e82644e6', 3.66),    --'Energy'                     -- 10
    ('7514f306-2aaa-4745-bbcd-913d18d3efad', '54a3dda8-3b86-4768-bcbf-5149061f5375', '0b743204-587a-4ba5-baea-8a9e36805063', 3.16),    --'Materials'                  -- 15
    ('432d9cc2-c868-419a-8c23-7fcf25ace988', '54a3dda8-3b86-4768-bcbf-5149061f5375', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 12.61),    --'Industrials'                -- 20
    ('4a1340bc-10dd-4950-b3d7-4061111f19c8', '54a3dda8-3b86-4768-bcbf-5149061f5375', '528fa9c4-bb5a-4d58-818f-71e65be26b35', 12.95),    --'Consumer Discretionary'    -- 25
    ('69e2d4ff-b465-4659-94e8-230f6b5e0fda', '54a3dda8-3b86-4768-bcbf-5149061f5375', '134d7ecb-7480-44f9-a6b5-8e9211212f1c', 4.24),     --'Consumer Staples'          -- 30
    ('73227f01-9b99-4449-b7c7-eccdf1db5822', '54a3dda8-3b86-4768-bcbf-5149061f5375', '75536f22-aa7f-4bb3-95c9-c7ae66295a13', 8.59),     --'Health Care'               -- 35
    ('ab2ad5a2-bdf6-442c-9084-a874c840f30f', '54a3dda8-3b86-4768-bcbf-5149061f5375', 'a45f3a5a-0319-4dcb-86e1-9478b16adfa4', 15.93),     --'Financials'               -- 40
    ('aa568155-88f2-45e7-835e-00701797b23b', '54a3dda8-3b86-4768-bcbf-5149061f5375', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 31.39),     --'Information Technology'   -- 45
    ('b78e4bf0-a6b6-48ed-ad36-962bbe004e9e', '54a3dda8-3b86-4768-bcbf-5149061f5375', 'ee4c5e5c-def2-4965-8779-00b32b1edf70', 2.76),     --'Communication Services'   -- 50
    ('c6d8ef58-483f-4f32-b6e6-3341f9d07878', '54a3dda8-3b86-4768-bcbf-5149061f5375', '1a09f1f7-628b-4f9a-a05c-0368cbc3a225', 2.79),     --'Utilities'                 -- 55
    ('1c97ebf7-3db7-4231-ad67-6f2e99651fc1', '54a3dda8-3b86-4768-bcbf-5149061f5375', 'cf1d4f4e-3b6a-4c2e-9f7a-8e9d6c5b4a3f', 1.93)        --'Real Estate'             -- 60
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.index_country_allocations (id, index_id, country_code, weight_percentage)
VALUES
    ('0ef17ed3-e145-4674-adb1-807c6fef0a1a', '54a3dda8-3b86-4768-bcbf-5149061f5375', 'US', 62.96),
    ('fdda3835-6473-49c6-b08d-8c291bbfe886', '54a3dda8-3b86-4768-bcbf-5149061f5375', 'JP', 5.73),
    ('e5033be7-1036-429c-b628-360595108c82', '54a3dda8-3b86-4768-bcbf-5149061f5375', 'CN', 3.45),
    ('6324cd84-21ae-4bd6-92ec-20d6dbff5b66', '54a3dda8-3b86-4768-bcbf-5149061f5375', 'GB', 3.32),
    ('4ddb0f5b-3665-4ba9-97b1-808f442eb945', '54a3dda8-3b86-4768-bcbf-5149061f5375', 'CA', 2.84),
    ('2a94cdd6-5ee3-4791-8a86-b580bdae25a8', '54a3dda8-3b86-4768-bcbf-5149061f5375', 'TW', 2.29),
    ('33c053c4-555e-4acd-81d3-3314139ab51a', '54a3dda8-3b86-4768-bcbf-5149061f5375', 'FR', 2.12),
    ('37c4b1fb-2299-4383-bad8-e9f7c4113327', '54a3dda8-3b86-4768-bcbf-5149061f5375', 'DE', 2.01),
    ('4f4a246e-c823-4742-b3ce-7ca53b875a54', '54a3dda8-3b86-4768-bcbf-5149061f5375', 'CH', 1.97),
    ('604dc52d-5269-40fa-bb44-30b9074d8156', '54a3dda8-3b86-4768-bcbf-5149061f5375', 'IN', 1.94)
ON CONFLICT (id) DO NOTHING;

-- Invesco FTSE All-World UCITS ETF USD (Acc)
UPDATE assets
SET tracked_index_id = '54a3dda8-3b86-4768-bcbf-5149061f5375'
WHERE id = '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9';
DELETE FROM etf_sector_allocations WHERE etf_id = '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9';
DELETE FROM etf_country_allocations WHERE etf_id = '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9';

-- Vanguard FTSE All-World UCITS ETF (Acc)
UPDATE assets
SET tracked_index_id = '54a3dda8-3b86-4768-bcbf-5149061f5375'
WHERE id = '9947b11a-69cb-4a8a-a4d0-dd822ac24491';
DELETE FROM etf_sector_allocations WHERE etf_id = '9947b11a-69cb-4a8a-a4d0-dd822ac24491';
DELETE FROM etf_country_allocations WHERE etf_id = '9947b11a-69cb-4a8a-a4d0-dd822ac24491';


-- MSCI Japan Index
INSERT INTO public.index_sector_allocations (id, index_id, sector_id, weight_percentage)
VALUES
    ('4bb0df33-4de7-47e6-89cc-1fe88830f935', '006a178f-3973-439b-a2cc-1dce1e24a915', 'ef8c30ca-2538-4913-b020-ead2e82644e6', 0.20),    --'Energy'                     -- 10
    ('13e51421-9654-426c-ab14-c70e2a1a0f82', '006a178f-3973-439b-a2cc-1dce1e24a915', '0b743204-587a-4ba5-baea-8a9e36805063', 1.12),    --'Materials'                  -- 15
    ('dd070671-92b1-4d84-a446-08c97a0055e6', '006a178f-3973-439b-a2cc-1dce1e24a915', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 22.37),    --'Industrials'                -- 20
    ('a3387667-b509-41e6-a749-9d8f9fef84a7', '006a178f-3973-439b-a2cc-1dce1e24a915', '528fa9c4-bb5a-4d58-818f-71e65be26b35', 16.08),    --'Consumer Discretionary'    -- 25
    ('aaf6a7af-ff4b-4f87-99bb-b636d13895e1', '006a178f-3973-439b-a2cc-1dce1e24a915', '134d7ecb-7480-44f9-a6b5-8e9211212f1c', 0.93),     --'Consumer Staples'          -- 30
    ('91049ca4-6a81-449a-8f95-c3ec52b4f8c0', '006a178f-3973-439b-a2cc-1dce1e24a915', '75536f22-aa7f-4bb3-95c9-c7ae66295a13', 10.64),     --'Health Care'               -- 35
    ('3a6f4c3d-f414-4010-9bd8-f4967f094d5a', '006a178f-3973-439b-a2cc-1dce1e24a915', 'a45f3a5a-0319-4dcb-86e1-9478b16adfa4', 21.0),     --'Financials'               -- 40
    ('d8d489b5-43a6-4e4e-b03f-43cfbfab846d', '006a178f-3973-439b-a2cc-1dce1e24a915', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 14.11),     --'Information Technology'   -- 45
    ('9daba169-bfc2-407d-97a1-610f572f9d21', '006a178f-3973-439b-a2cc-1dce1e24a915', 'ee4c5e5c-def2-4965-8779-00b32b1edf70', 6.93),     --'Communication Services'   -- 50
    ('c41c1a81-855a-455e-b6a8-c990baed4913', '006a178f-3973-439b-a2cc-1dce1e24a915', '1a09f1f7-628b-4f9a-a05c-0368cbc3a225', 0.14),     --'Utilities'                 -- 55
    ('8a7d73fc-91a2-493b-bc73-3569917b29d4', '006a178f-3973-439b-a2cc-1dce1e24a915', 'cf1d4f4e-3b6a-4c2e-9f7a-8e9d6c5b4a3f', 5.99)        --'Real Estate'             -- 60
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.index_country_allocations (id, index_id, country_code, weight_percentage)
VALUES
    ('71cbdc2f-10c6-489a-808c-a2af6ef0ec79', '006a178f-3973-439b-a2cc-1dce1e24a915', 'JP', 99.51)
ON CONFLICT (id) DO NOTHING;

-- iShares MSCI Japan ESG Enhanced CTB UCITS ETF USD Inc (Dist)
UPDATE assets
SET tracked_index_id = '006a178f-3973-439b-a2cc-1dce1e24a915'
WHERE id = 'aa491ae0-a832-41f2-9354-6d158c23e399';
DELETE FROM etf_sector_allocations WHERE etf_id = 'aa491ae0-a832-41f2-9354-6d158c23e399';
DELETE FROM etf_country_allocations WHERE etf_id = 'aa491ae0-a832-41f2-9354-6d158c23e399';

-- iShares MSCI Japan GBP Hedged UCITS ETF (Acc)
UPDATE assets
SET tracked_index_id = '006a178f-3973-439b-a2cc-1dce1e24a915'
WHERE id = '42e35a5d-9958-47dc-8951-be1c98f0c81f';
DELETE FROM etf_sector_allocations WHERE etf_id = '42e35a5d-9958-47dc-8951-be1c98f0c81f';
DELETE FROM etf_country_allocations WHERE etf_id = '42e35a5d-9958-47dc-8951-be1c98f0c81f';

-- MSCI EMU Index
INSERT INTO public.index_sector_allocations (id, index_id, sector_id, weight_percentage)
VALUES
    ('57f1663d-32a6-4dea-a9a1-0e648dd69661', '4a965999-ed1f-4b5e-9709-ce8e6a6ffbf8', 'ef8c30ca-2538-4913-b020-ead2e82644e6', 3.05),    --'Energy'                     -- 10
    ('62f55402-6cc0-4c5c-b027-10babf0ae2bc', '4a965999-ed1f-4b5e-9709-ce8e6a6ffbf8', '0b743204-587a-4ba5-baea-8a9e36805063', 3.90),    --'Materials'                  -- 15
    ('8b5bfc68-cbf1-4096-850e-3351121cdac1', '4a965999-ed1f-4b5e-9709-ce8e6a6ffbf8', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 20.54),    --'Industrials'                -- 20
    ('ce837c0c-1d66-4ab1-8eca-0065eba06f0f', '4a965999-ed1f-4b5e-9709-ce8e6a6ffbf8', '528fa9c4-bb5a-4d58-818f-71e65be26b35', 10.97),    --'Consumer Discretionary'    -- 25
    ('e584c5a0-a8b6-4ee6-88b2-af5fa811ad3a', '4a965999-ed1f-4b5e-9709-ce8e6a6ffbf8', '134d7ecb-7480-44f9-a6b5-8e9211212f1c', 5.80),     --'Consumer Staples'          -- 30
    ('fbe13279-b37d-43b4-bf1a-4bad6ed20eaa', '4a965999-ed1f-4b5e-9709-ce8e6a6ffbf8', '75536f22-aa7f-4bb3-95c9-c7ae66295a13', 6.82),    --'Health Care'               -- 35
    ('79a58901-6288-4658-b06f-41f01b7afad5', '4a965999-ed1f-4b5e-9709-ce8e6a6ffbf8', 'a45f3a5a-0319-4dcb-86e1-9478b16adfa4', 25.23),     --'Financials'               -- 40
    ('54f77acb-5a3f-45ab-88c6-e95c9a7ac98b', '4a965999-ed1f-4b5e-9709-ce8e6a6ffbf8', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 12.32),      --'Information Technology'   -- 45
    ('849a6f37-dfd0-49d1-82f3-e2fec95365eb', '4a965999-ed1f-4b5e-9709-ce8e6a6ffbf8', 'ee4c5e5c-def2-4965-8779-00b32b1edf70', 3.85),     --'Communication Services'   -- 50
    ('f57a3959-7db6-4c03-93ed-126bb4c8ba79', '4a965999-ed1f-4b5e-9709-ce8e6a6ffbf8', '1a09f1f7-628b-4f9a-a05c-0368cbc3a225', 6.22),     --'Utilities'                 -- 55
    ('5c620011-d3c0-4ad3-b8e4-451346dd882c', '4a965999-ed1f-4b5e-9709-ce8e6a6ffbf8', 'cf1d4f4e-3b6a-4c2e-9f7a-8e9d6c5b4a3f', 0.78)        --'Real Estate'             -- 60
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.index_country_allocations (id, index_id, country_code, weight_percentage)
VALUES
    ('b5ba931d-0037-4a1c-bad4-5bc3f308eae6', '4a965999-ed1f-4b5e-9709-ce8e6a6ffbf8', 'FR', 29.81),
    ('aa9a9e9a-92f6-4fcb-a9ca-9fba0c79064b', '4a965999-ed1f-4b5e-9709-ce8e6a6ffbf8', 'DE', 27.09),
    ('ce0b7745-b9a8-407a-8403-7e84b5a60798', '4a965999-ed1f-4b5e-9709-ce8e6a6ffbf8', 'NL', 13.93),
    ('4121d917-3c4e-412b-9105-56e77c62e7d6', '4a965999-ed1f-4b5e-9709-ce8e6a6ffbf8', 'ES', 10.74),
    ('94431e97-8ec2-48c7-88e1-56ba5367772e', '4a965999-ed1f-4b5e-9709-ce8e6a6ffbf8', 'IT', 9.00),
    ('1e445c9a-37d6-4fae-a608-12d387bfdb72', '4a965999-ed1f-4b5e-9709-ce8e6a6ffbf8', 'FI', 3.17),
    ('82c5d01a-53cc-4e55-931d-d5d35168e7c9', '4a965999-ed1f-4b5e-9709-ce8e6a6ffbf8', 'BE', 3.13),
    ('cdc3f9c9-a4d6-4b8f-9630-1d64e36cdb5b', '4a965999-ed1f-4b5e-9709-ce8e6a6ffbf8', 'IE', 1.33),
    ('11a6f04b-827d-44db-a5f3-c1044d402513', '4a965999-ed1f-4b5e-9709-ce8e6a6ffbf8', 'AT', 0.74),
    ('17b0dd04-13e5-452f-895d-4808c771d456', '4a965999-ed1f-4b5e-9709-ce8e6a6ffbf8', 'PT', 0.54)
ON CONFLICT (id) DO NOTHING;

-- iShares VII PLC -iShares Core MSCI EMU UCITS ETF EUR (Acc)
UPDATE assets
SET tracked_index_id = '4a965999-ed1f-4b5e-9709-ce8e6a6ffbf8'
WHERE id = '40b35093-06c8-40e6-8ab9-05a6dbe30d68';
DELETE FROM etf_sector_allocations WHERE etf_id = '40b35093-06c8-40e6-8ab9-05a6dbe30d68';
DELETE FROM etf_country_allocations WHERE etf_id = '40b35093-06c8-40e6-8ab9-05a6dbe30d68';

-- MSCI World Index
INSERT INTO public.index_sector_allocations (id, index_id, sector_id, weight_percentage)
VALUES
    ('66be105a-976d-4abc-9a90-920df8de1302', '327aa8ef-11b2-48b5-962a-d7266bcb549b', 'ef8c30ca-2538-4913-b020-ead2e82644e6', 3.35),    --'Energy'                     -- 10
    ('40f9bcc0-a869-4e61-8602-d352eda8ee09', '327aa8ef-11b2-48b5-962a-d7266bcb549b', '0b743204-587a-4ba5-baea-8a9e36805063', 2.25),    --'Materials'                  -- 15
    ('bd94cdbc-98d9-4a5a-ae75-17f6bc8beba1', '327aa8ef-11b2-48b5-962a-d7266bcb549b', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 10.18),    --'Industrials'                -- 20
    ('bc475b84-de3e-4a0b-afd0-1530ddab99f8', '327aa8ef-11b2-48b5-962a-d7266bcb549b', '528fa9c4-bb5a-4d58-818f-71e65be26b35', 10.35),    --'Consumer Discretionary'    -- 25
    ('fd1a8548-76b4-4327-95bc-c0c1c3c615aa', '327aa8ef-11b2-48b5-962a-d7266bcb549b', '134d7ecb-7480-44f9-a6b5-8e9211212f1c', 5.07),     --'Consumer Staples'          -- 30
    ('82a472b7-39c2-4222-a8a1-292f2705674e', '327aa8ef-11b2-48b5-962a-d7266bcb549b', '75536f22-aa7f-4bb3-95c9-c7ae66295a13', 8.63),    --'Health Care'               -- 35
    ('34554a10-bb63-4f24-98f6-5e3eb11266a3', '327aa8ef-11b2-48b5-962a-d7266bcb549b', 'a45f3a5a-0319-4dcb-86e1-9478b16adfa4', 14.03),     --'Financials'               -- 40
    ('8e73ba95-3ff0-4e87-91e3-87801ee509ed', '327aa8ef-11b2-48b5-962a-d7266bcb549b', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 29.92),      --'Information Technology'   -- 45
    ('daf2af3d-1e02-4d0e-a408-27492613ebc2', '327aa8ef-11b2-48b5-962a-d7266bcb549b', 'ee4c5e5c-def2-4965-8779-00b32b1edf70', 8.57),     --'Communication Services'   -- 50
    ('4ad0fc3e-023e-477d-bd7a-a8d6d5b20f6e', '327aa8ef-11b2-48b5-962a-d7266bcb549b', '1a09f1f7-628b-4f9a-a05c-0368cbc3a225', 2.58),     --'Utilities'                 -- 55
    ('56fd0c0c-ea34-4815-aae5-daddfe09c69f', '327aa8ef-11b2-48b5-962a-d7266bcb549b', 'cf1d4f4e-3b6a-4c2e-9f7a-8e9d6c5b4a3f', 1.78)        --'Real Estate'             -- 60
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.index_country_allocations (id, index_id, country_code, weight_percentage)
VALUES
    ('8c561421-cafd-4cdf-b9e9-d7b0058b3059', '327aa8ef-11b2-48b5-962a-d7266bcb549b', 'US', 69.33),
    ('95444db5-7e5d-4820-ad24-fe79779333f7', '327aa8ef-11b2-48b5-962a-d7266bcb549b', 'JP', 5.52),
    ('c17ca415-7c09-4549-839f-278965540687', '327aa8ef-11b2-48b5-962a-d7266bcb549b', 'GB', 3.41),
    ('46c4cc4e-feb0-4fa5-8e27-0cafb8cce111', '327aa8ef-11b2-48b5-962a-d7266bcb549b', 'CA', 2.81),
    ('090f768f-6fac-4ea8-957b-c220537a4d3a', '327aa8ef-11b2-48b5-962a-d7266bcb549b', 'FR', 2.42),
    ('0932b24f-e57e-4164-a2f0-d1c2091ee6fd', '327aa8ef-11b2-48b5-962a-d7266bcb549b', 'CH', 2.39),
    ('e81a1c1c-98b0-45dc-a536-7a9ff48b8bf5', '327aa8ef-11b2-48b5-962a-d7266bcb549b', 'DE', 2.28),
    ('6d8ec763-7a63-4a9a-821b-bb6e6ab52c2d', '327aa8ef-11b2-48b5-962a-d7266bcb549b', 'AU', 1.55),
    ('65ba8e08-b762-4f1e-98fa-93fd3ef752fa', '327aa8ef-11b2-48b5-962a-d7266bcb549b', 'NL', 1.49),
    ('b73db30a-6642-425c-8f30-7822863e7025', '327aa8ef-11b2-48b5-962a-d7266bcb549b', 'IR', 1.29)
ON CONFLICT (id) DO NOTHING;

-- HSBC MSCI World UCITS ETF (Dist)
UPDATE assets
SET tracked_index_id = '327aa8ef-11b2-48b5-962a-d7266bcb549b'
WHERE id = '5c526cd2-44f6-4de7-b5ea-fd83e081bf35';
DELETE FROM etf_sector_allocations WHERE etf_id = '5c526cd2-44f6-4de7-b5ea-fd83e081bf35';
DELETE FROM etf_country_allocations WHERE etf_id = '5c526cd2-44f6-4de7-b5ea-fd83e081bf35';