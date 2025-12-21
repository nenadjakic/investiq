-- Invesco NASDAQ-100 Swap UCITS ETF Acc
UPDATE assets
SET asset_class = 'EQUITY',
    tracked_index_id = '99696a98-2667-48e1-9a38-851cfb8ffb45'
WHERE id = '3156dcad-1744-4991-9698-409b0e2bb4ee';

-- iShares MSCI Japan ESG Enhanced CTB UCITS ETF USD Inc (Dist)
UPDATE assets
SET asset_class = 'EQUITY'
WHERE id = 'aa491ae0-a832-41f2-9354-6d158c23e399';

INSERT INTO etf_sector_allocations (id, etf_id, sector_id, weight_percentage)
VALUES
    ('79c31918-fab8-4c75-b65d-d5893369e37f', 'aa491ae0-a832-41f2-9354-6d158c23e399', 'ef8c30ca-2538-4913-b020-ead2e82644e6', 0.19),    --'Energy'                     -- 10
    ('63c292c2-93e1-4937-aa59-38f7f6b6b81d', 'aa491ae0-a832-41f2-9354-6d158c23e399', '0b743204-587a-4ba5-baea-8a9e36805063', 1.10),    --'Materials'                  -- 15
    ('422a088d-00bc-46cb-87e8-d230b19a75fb', 'aa491ae0-a832-41f2-9354-6d158c23e399', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 22.34),    --'Industrials'                -- 20
    ('25bde96a-a9cf-4fd7-8ded-216f6c14723e', 'aa491ae0-a832-41f2-9354-6d158c23e399', '528fa9c4-bb5a-4d58-818f-71e65be26b35', 0.62),    --'Consumer Discretionary'    -- 25
    ('00baea83-967c-414f-82bf-501e538a7aa9', 'aa491ae0-a832-41f2-9354-6d158c23e399', '134d7ecb-7480-44f9-a6b5-8e9211212f1c', 0.93),     --'Consumer Staples'          -- 30
    ('738fdb10-ec4c-4ba8-9eec-ba68bd554994', 'aa491ae0-a832-41f2-9354-6d158c23e399', '75536f22-aa7f-4bb3-95c9-c7ae66295a13', 10.34),     --'Health Care'               -- 35
    ('2a6a6c05-8e34-4a89-b0a4-5b0041229672', 'aa491ae0-a832-41f2-9354-6d158c23e399', 'a45f3a5a-0319-4dcb-86e1-9478b16adfa4', 20.75),     --'Financials'               -- 40
    ('5974f558-fab6-46fa-8970-244dacfe4427', 'aa491ae0-a832-41f2-9354-6d158c23e399', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 14.37),     --'Information Technology'   -- 45
    ('df8bf91b-a76c-4f91-9f52-a6d5a079383a', 'aa491ae0-a832-41f2-9354-6d158c23e399', 'ee4c5e5c-def2-4965-8779-00b32b1edf70', 7.10),     --'Communication Services'   -- 50
    ('3abf37c3-bc48-426e-b7f5-eb42375d41d9', 'aa491ae0-a832-41f2-9354-6d158c23e399', '1a09f1f7-628b-4f9a-a05c-0368cbc3a225', 0.00),     --'Utilities'                 -- 55
    ('4720c75f-3c8e-42d5-baa9-39b86e74aca8', 'aa491ae0-a832-41f2-9354-6d158c23e399', 'cf1d4f4e-3b6a-4c2e-9f7a-8e9d6c5b4a3f', 6.01)        --'Real Estate'             -- 60
;

INSERT INTO etf_country_allocations (id, etf_id, country_code, weight_percentage)
VALUES
    ('4f74ddf2-244b-4a27-a7dd-e56eee86a9c1', 'aa491ae0-a832-41f2-9354-6d158c23e399', 'JP', 99.38)
;

-- iShares S&P 500 Information Technology Sector UCITS ETF USD (Acc)
UPDATE assets
SET asset_class = 'EQUITY'
WHERE id = '8571c932-dd11-4d38-9545-41fa6c0ee2ab';

INSERT INTO etf_sector_allocations (id, etf_id, sector_id, weight_percentage)
VALUES
    ('761005f2-6001-41e7-a7e5-2301207b57bb', '8571c932-dd11-4d38-9545-41fa6c0ee2ab', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 99.88)     --'Information Technology'   -- 45
;

INSERT INTO etf_country_allocations (id, etf_id, country_code, weight_percentage)
VALUES
    ('8be00496-0d37-4f67-ad79-11e9e8faccc9', '8571c932-dd11-4d38-9545-41fa6c0ee2ab', 'US', 99.88)
;

-- iShares MSCI Europe ex-UK GBP Hedged UCITS ETF (Dist)
UPDATE assets
SET asset_class = 'EQUITY'
WHERE id = 'b10c4bfa-81e1-428f-bc7f-ab9613a5bb9e';

INSERT INTO etf_sector_allocations (id, etf_id, sector_id, weight_percentage)
VALUES
    ('e51a7604-1b19-4327-a2cd-7fa532fa9a99', 'b10c4bfa-81e1-428f-bc7f-ab9613a5bb9e', 'ef8c30ca-2538-4913-b020-ead2e82644e6', 2.36),    --'Energy'                     -- 10
    ('be6e3157-7694-44de-9277-6c579f9cb3fb', 'b10c4bfa-81e1-428f-bc7f-ab9613a5bb9e', '0b743204-587a-4ba5-baea-8a9e36805063', 4.47),    --'Materials'                  -- 15
    ('5a5b783e-a631-4e16-bcf6-f7cd15eac87d', 'b10c4bfa-81e1-428f-bc7f-ab9613a5bb9e', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 20.55),    --'Industrials'                -- 20
    ('aa841e59-6a99-443e-9bf3-6b96f235eb1a', 'b10c4bfa-81e1-428f-bc7f-ab9613a5bb9e', '528fa9c4-bb5a-4d58-818f-71e65be26b35', 9.09),    --'Consumer Discretionary'    -- 25
    ('2ced242c-eefb-491e-b559-5161d9d19741', 'b10c4bfa-81e1-428f-bc7f-ab9613a5bb9e', '134d7ecb-7480-44f9-a6b5-8e9211212f1c', 7.29),     --'Consumer Staples'          -- 30
    ('1da2cc6b-42ac-439f-8250-c356f88fb2a1', 'b10c4bfa-81e1-428f-bc7f-ab9613a5bb9e', '75536f22-aa7f-4bb3-95c9-c7ae66295a13', 13.59),     --'Health Care'               -- 35
    ('101708b0-7dfa-45b8-bef4-894a39b00880', 'b10c4bfa-81e1-428f-bc7f-ab9613a5bb9e', 'a45f3a5a-0319-4dcb-86e1-9478b16adfa4', 23.67),     --'Financials'               -- 40
    ('d35103c7-4ccd-495a-bf91-e0ebd75673bd', 'b10c4bfa-81e1-428f-bc7f-ab9613a5bb9e', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 9.23),     --'Information Technology'   -- 45
    ('01acd039-0cbf-4ce4-ba68-e715bcf90bb4', 'b10c4bfa-81e1-428f-bc7f-ab9613a5bb9e', 'ee4c5e5c-def2-4965-8779-00b32b1edf70', 4.01),     --'Communication Services'   -- 50
    ('5604af17-71ca-401f-bf46-1b8cbdcdca3d', 'b10c4bfa-81e1-428f-bc7f-ab9613a5bb9e', '1a09f1f7-628b-4f9a-a05c-0368cbc3a225', 4.48),     --'Utilities'                 -- 55
    ('e703f9b3-8c34-43cf-b7c9-de9f5ec327c5', 'b10c4bfa-81e1-428f-bc7f-ab9613a5bb9e', 'cf1d4f4e-3b6a-4c2e-9f7a-8e9d6c5b4a3f', 0.74)        --'Real Estate'             -- 60
;

INSERT INTO etf_country_allocations (id, etf_id, country_code, weight_percentage)
VALUES
    ('783e10a4-497e-4a21-9d3c-602c5cfc2500', 'b10c4bfa-81e1-428f-bc7f-ab9613a5bb9e', 'FR', 20.75),
    ('ede7263a-40f9-4f4b-a061-79a09c484a23', 'b10c4bfa-81e1-428f-bc7f-ab9613a5bb9e', 'DE', 18.86),
    ('358f7921-a58d-4b3d-af95-5281eff42116', 'b10c4bfa-81e1-428f-bc7f-ab9613a5bb9e', 'CH', 18.23),
    ('906eebf5-7492-42e5-8721-40d788ddec25', 'b10c4bfa-81e1-428f-bc7f-ab9613a5bb9e', 'NL', 9.71),
    ('0128fea1-806f-4654-b40b-a11e33fea556', 'b10c4bfa-81e1-428f-bc7f-ab9613a5bb9e', 'ES', 7.48),
    ('8232b802-f432-4f84-93d7-093595774565', 'b10c4bfa-81e1-428f-bc7f-ab9613a5bb9e', 'SE', 7.14),
    ('25c0e523-3a8e-42b5-8713-7a876d1edec3', 'b10c4bfa-81e1-428f-bc7f-ab9613a5bb9e', 'IT', 6.26),
    ('aef99811-5b47-427c-95fa-e688927633f8', 'b10c4bfa-81e1-428f-bc7f-ab9613a5bb9e', 'DK', 3.74),
    ('3051fdf5-0aeb-42f6-901f-bfe92115490f', 'b10c4bfa-81e1-428f-bc7f-ab9613a5bb9e', 'FI', 2.21),
    ('fd9d962d-1b87-4dce-abab-a3a912712872', 'b10c4bfa-81e1-428f-bc7f-ab9613a5bb9e', 'BE', 2.17)
;

-- iShares Core S&P 500 UCITS ETF USD (Acc)
UPDATE assets
SET asset_class = 'EQUITY',
    tracked_index_id = 'cf4a597d-bc40-4440-9a54-e719956d1e05'
WHERE id = '1dfe41ba-3464-456d-8b51-7e21e4bf4a68';

-- Invesco EQQQ NASDAQ-100 UCITS ETF (Dist)
UPDATE assets
SET asset_class = 'EQUITY',
    tracked_index_id = '99696a98-2667-48e1-9a38-851cfb8ffb45'
WHERE id = '940d090e-30d5-4133-86c7-9afed791abb5';

-- Vanguard S&P 500 UCITS ETF USD (Acc)
UPDATE assets
SET asset_class = 'EQUITY',
    tracked_index_id = 'cf4a597d-bc40-4440-9a54-e719956d1e05'
WHERE id = '30ce052d-2dba-4271-b6e3-1a034681853b';

-- Invesco FTSE All-World UCITS ETF USD (Acc)
UPDATE assets
SET asset_class = 'EQUITY'
WHERE id = '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9';

INSERT INTO etf_sector_allocations(id, etf_id, sector_id, weight_percentage)
VALUES
    ('2ff34717-5f1c-4631-ac5d-e6ed1c4283c8', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', 'ef8c30ca-2538-4913-b020-ead2e82644e6', 3.4),    --'Energy'                     -- 10
    ('244c3b0c-bcc0-4e26-9d9e-c83f8e4778f7', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', '0b743204-587a-4ba5-baea-8a9e36805063', 0.0),    --'Materials'                  -- 15
    ('7ef20e75-9222-48cc-8b9c-64e717dbe81c', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 10.2),    --'Industrials'                -- 20
    ('1ecd3649-9703-45f8-9066-683f71bf379b', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', '528fa9c4-bb5a-4d58-818f-71e65be26b35', 10.4),    --'Consumer Discretionary'    -- 25
    ('6774bcef-05ba-4116-95a4-16cd8e2643f5', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', '134d7ecb-7480-44f9-a6b5-8e9211212f1c', 5.2),     --'Consumer Staples'          -- 30
    ('338c48bc-fa9a-4f10-9839-494cdac2695d', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', '75536f22-aa7f-4bb3-95c9-c7ae66295a13', 8.7),     --'Health Care'               -- 35
    ('8bcd400f-eacb-4a8b-83ba-0452a317f9a3', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', 'a45f3a5a-0319-4dcb-86e1-9478b16adfa4', 17.5),     --'Financials'               -- 40
    ('51733791-6f15-4840-a46a-16201511e465', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 27.2),     --'Information Technology'   -- 45
    ('f20280af-8bb3-4d45-8320-ff92969eba28', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', 'ee4c5e5c-def2-4965-8779-00b32b1edf70', 9.1),     --'Communication Services'   -- 50
    ('b70826ae-c046-4edd-8526-d9702332b24b', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', '1a09f1f7-628b-4f9a-a05c-0368cbc3a225', 2.4),     --'Utilities'                 -- 55
    ('872e463f-4c00-4586-aecb-689a041a07e2', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', 'cf1d4f4e-3b6a-4c2e-9f7a-8e9d6c5b4a3f', 1.9)        --'Real Estate'             -- 60
;

INSERT INTO etf_country_allocations (id, etf_id, country_code, weight_percentage)
VALUES
    ('c80b1e85-da99-484d-830e-c19ba76999a3', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', 'US', 64.1),
    ('a74b4cc8-2523-4a35-a348-1705aa1dc4bb', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', 'JP', 5.7),
    ('e5fdf037-f6fe-4d38-a2b7-b1cf62a93bb1', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', 'GB', 4.5),
    ('5b4da85a-ba42-421e-871f-5eea7a3654b6', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', 'HK', 3.2),
    ('269d0ccd-1be8-4376-bce4-bbc5f235e0bb', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', 'CA', 2.8),
    ('6b7bd182-a511-4e47-827e-f0b0969ab723', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', 'FR', 2.3),
    ('4eef5462-8f30-4bf4-add6-215ca954b767', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', 'TW', 2.1),
    ('dfff18c8-574d-479b-bcfb-86cc69573aea', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', 'CH', 2.0),
    ('b68de676-91fb-40b0-8caf-8649b7594d83', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', 'DE', 2.0),
    ('ca68100a-aa45-4f6e-a4f2-61b5acb41616', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', 'AU', 1.5),
    ('6948561e-bbd7-44e1-8f08-c0c29cc5bf2b', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', 'KR', 1.3),
    ('5af929d7-05e3-4fc7-a770-7826350c6e44', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', 'NL', 0.9),
    ('5caf6b4c-c96a-4637-b9fe-ccafd78e7086', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', 'ES', 0.8),
    ('8c2d2b87-02f0-4f70-be69-c1e5a3bd31d6', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', 'SE', 0.7),
    ('fc798fe2-8df3-4fb0-a2dc-32614657dba4', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', 'IT', 0.6),
    ('316a8a90-a0cc-4296-91c4-2a429608a587', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', 'CN', 0.5),
    ('4feaefcd-bb64-4907-b94c-f1202fbb5696', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', 'DK', 0.4),
    ('a7796248-54c9-4be4-93e2-c99882212e80', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', 'SG', 0.4),
    ('f3313079-7114-4e5e-848f-4357b9a3f5d5', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', 'BR', 0.3),
    ('a3fcc98f-0e85-4b7b-a26b-7ee02ab1be8b', '4936d8ac-27e8-46b1-b42d-b610c7fd1ea9', 'SA', 0.3)
;

-- iShares Core MSCI Europe UCITS ETF EUR (Acc)
UPDATE assets
SET asset_class = 'EQUITY'
WHERE id = '8fc5029f-8fa1-4874-adc8-07efbc6c9d5e';

INSERT INTO etf_sector_allocations (id, etf_id, sector_id, weight_percentage)
VALUES
    ('dcbc9739-d82a-4187-b0b1-57b331799b10', '8fc5029f-8fa1-4874-adc8-07efbc6c9d5e', 'ef8c30ca-2538-4913-b020-ead2e82644e6', 4.10),    --'Energy'                     -- 10
    ('daaa2390-ae39-4aaa-8497-9ffe0f7356e3', '8fc5029f-8fa1-4874-adc8-07efbc6c9d5e', '0b743204-587a-4ba5-baea-8a9e36805063', 5.07),    --'Materials'                  -- 15
    ('a3a0a919-d5ef-4d88-aedd-00f0a64eac0f', '8fc5029f-8fa1-4874-adc8-07efbc6c9d5e', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 18.98),    --'Industrials'                -- 20
    ('86c18586-3d69-4c35-b2b0-60cbfddc3f6d', '8fc5029f-8fa1-4874-adc8-07efbc6c9d5e', '528fa9c4-bb5a-4d58-818f-71e65be26b35', 8.06),    --'Consumer Discretionary'    -- 25
    ('b8cb6803-fdfd-4733-bc02-921e6165e6e4', '8fc5029f-8fa1-4874-adc8-07efbc6c9d5e', '134d7ecb-7480-44f9-a6b5-8e9211212f1c', 9.22),     --'Consumer Staples'          -- 30
    ('3103abb1-2e51-4922-8ca2-21904117aac0', '8fc5029f-8fa1-4874-adc8-07efbc6c9d5e', '75536f22-aa7f-4bb3-95c9-c7ae66295a13', 13.85),     --'Health Care'               -- 35
    ('ca908a72-4bf8-4ec6-8d1d-7752ec9179a2', '8fc5029f-8fa1-4874-adc8-07efbc6c9d5e', 'a45f3a5a-0319-4dcb-86e1-9478b16adfa4', 23.97),     --'Financials'               -- 40
    ('8832c573-f294-43c0-85d0-b3f64af24543', '8fc5029f-8fa1-4874-adc8-07efbc6c9d5e', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 7.43),     --'Information Technology'   -- 45
    ('74ac36ed-0b58-4651-947d-eea2b5547916', '8fc5029f-8fa1-4874-adc8-07efbc6c9d5e', 'ee4c5e5c-def2-4965-8779-00b32b1edf70', 3.57),     --'Communication Services'   -- 50
    ('5a0b9643-3bf5-4069-9607-ba05c2129118', '8fc5029f-8fa1-4874-adc8-07efbc6c9d5e', '1a09f1f7-628b-4f9a-a05c-0368cbc3a225', 4.52),     --'Utilities'                 -- 55
    ('f7886de1-2963-4590-aab1-365788644692', '8fc5029f-8fa1-4874-adc8-07efbc6c9d5e', 'cf1d4f4e-3b6a-4c2e-9f7a-8e9d6c5b4a3f', 0.70)        --'Real Estate'             -- 60
;

INSERT INTO etf_country_allocations (id, etf_id, country_code, weight_percentage)
VALUES
    ('34460899-fd84-4d25-a50c-508c7bcf3603', '8fc5029f-8fa1-4874-adc8-07efbc6c9d5e', 'GB', 21.98),
    ('e4c98a7b-6557-4cec-9565-776e45a67464', '8fc5029f-8fa1-4874-adc8-07efbc6c9d5e', 'FR', 16.16),
    ('b274c3b7-dee0-4a21-888c-890432daf4c0', '8fc5029f-8fa1-4874-adc8-07efbc6c9d5e', 'DE', 14.67),
    ('17ecb829-18b3-495b-8347-f9c89ef384a0', '8fc5029f-8fa1-4874-adc8-07efbc6c9d5e', 'CH', 14.23),
    ('8450bd8c-be95-4120-ad24-6adb9ebf948a', '8fc5029f-8fa1-4874-adc8-07efbc6c9d5e', 'NL', 7.58),
    ('f27b2763-af8f-4bde-b542-b6d28ac04081', '8fc5029f-8fa1-4874-adc8-07efbc6c9d5e', 'ES', 5.82),
    ('1fdeb5c0-8f1a-4885-876a-1eb3f3b4b0ba', '8fc5029f-8fa1-4874-adc8-07efbc6c9d5e', 'SE', 5.56),
    ('e69451e0-6983-4553-a829-f891ad26fa93', '8fc5029f-8fa1-4874-adc8-07efbc6c9d5e', 'IT', 4.87),
    ('af8e0bcb-641f-4d77-ae44-59d65fa0f239', '8fc5029f-8fa1-4874-adc8-07efbc6c9d5e', 'DK', 2.92),
    ('0e38e5ed-825a-4db6-b387-69a195969191', '8fc5029f-8fa1-4874-adc8-07efbc6c9d5e', 'FI', 1.72)
;

-- iShares MSCI Japan GBP Hedged UCITS ETF (Acc)
UPDATE assets
SET asset_class = 'EQUITY'
WHERE id = '42e35a5d-9958-47dc-8951-be1c98f0c81f';

INSERT INTO etf_sector_allocations (id, etf_id, sector_id, weight_percentage)
VALUES
    ('541eff7c-162a-423c-8090-c9ff344cac68', '42e35a5d-9958-47dc-8951-be1c98f0c81f', 'ef8c30ca-2538-4913-b020-ead2e82644e6', 0.91),    --'Energy'                     -- 10
    ('69a48300-4741-4335-a9bd-a335cf61752d', '42e35a5d-9958-47dc-8951-be1c98f0c81f', '0b743204-587a-4ba5-baea-8a9e36805063', 3.20),    --'Materials'                  -- 15
    ('ffdef423-5668-40a1-8bc5-124cee69bf4f', '42e35a5d-9958-47dc-8951-be1c98f0c81f', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 24.78),    --'Industrials'                -- 20
    ('a04ccec7-e844-4139-ade6-1d8326cf0ecd', '42e35a5d-9958-47dc-8951-be1c98f0c81f', '528fa9c4-bb5a-4d58-818f-71e65be26b35', 16.45),    --'Consumer Discretionary'    -- 25
    ('7fb60822-98c9-4f53-a0bb-b0fdfef23bbc', '42e35a5d-9958-47dc-8951-be1c98f0c81f', '134d7ecb-7480-44f9-a6b5-8e9211212f1c', 4.45),     --'Consumer Staples'          -- 30
    ('c3a19737-e01e-4efc-9d6b-3e884b3ccfcb', '42e35a5d-9958-47dc-8951-be1c98f0c81f', '75536f22-aa7f-4bb3-95c9-c7ae66295a13', 6.18),     --'Health Care'               -- 35
    ('442c01c1-2742-4f9d-a3e6-09e2c913358d', '42e35a5d-9958-47dc-8951-be1c98f0c81f', 'a45f3a5a-0319-4dcb-86e1-9478b16adfa4', 16.23),     --'Financials'               -- 40
    ('e5457939-10d5-4db1-80d9-75e835401620', '42e35a5d-9958-47dc-8951-be1c98f0c81f', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 13.62),     --'Information Technology'   -- 45
    ('bf14e311-0e16-44eb-b33f-0b50d08176af', '42e35a5d-9958-47dc-8951-be1c98f0c81f', 'ee4c5e5c-def2-4965-8779-00b32b1edf70', 7.54),     --'Communication Services'   -- 50
    ('deb27060-1e50-4fb5-9e08-6817007ec8c6', '42e35a5d-9958-47dc-8951-be1c98f0c81f', '1a09f1f7-628b-4f9a-a05c-0368cbc3a225', 1.06),     --'Utilities'                 -- 55
    ('9b83b043-743c-4f6e-8cd6-a0c9bd865248', '42e35a5d-9958-47dc-8951-be1c98f0c81f', 'cf1d4f4e-3b6a-4c2e-9f7a-8e9d6c5b4a3f', 2.30)        --'Real Estate'             -- 60
;

INSERT INTO etf_country_allocations (id, etf_id, country_code, weight_percentage)
VALUES
    ('df5157f3-c657-4906-a9de-a2b26d9bbda5', '42e35a5d-9958-47dc-8951-be1c98f0c81f', 'JP', 96.70)
;

-- HAN-GINS Tech Megatrend Equal Weight UCITS ETF (Acc)
UPDATE assets
SET asset_class = 'EQUITY',
    fund_manager = 'HAN-GINS'
WHERE id = '9e3576fe-1e9e-41c3-8089-fefec9f420d0';

INSERT INTO etf_sector_allocations (id, etf_id, sector_id, weight_percentage)
VALUES
    ('4c37c4f2-7966-4d0f-a2b0-ef3c87f429fc', '9e3576fe-1e9e-41c3-8089-fefec9f420d0', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 10),    --'Industrials'                -- 20
    ('b2a67ac7-28df-4237-8f92-f18fc2d892c7', '9e3576fe-1e9e-41c3-8089-fefec9f420d0', '528fa9c4-bb5a-4d58-818f-71e65be26b35', 7),    --'Consumer Discretionary'    -- 25
    ('f3ef89d1-de75-4b69-8e97-e804f51ebd38', '9e3576fe-1e9e-41c3-8089-fefec9f420d0', '75536f22-aa7f-4bb3-95c9-c7ae66295a13', 11),     --'Health Care'               -- 35
    ('57f2088c-0675-447b-8f42-860348da7e78', '9e3576fe-1e9e-41c3-8089-fefec9f420d0', 'a45f3a5a-0319-4dcb-86e1-9478b16adfa4', 4.5),     --'Financials'               -- 40
    ('7454dee6-9974-4f24-9ef2-ade277ed9645', '9e3576fe-1e9e-41c3-8089-fefec9f420d0', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 46),     --'Information Technology'   -- 45
    ('301f7978-72b3-4bda-a2d0-4f92014fe0c5', '9e3576fe-1e9e-41c3-8089-fefec9f420d0', 'ee4c5e5c-def2-4965-8779-00b32b1edf70', 17)     --'Communication Services'   -- 50
;

INSERT INTO etf_country_allocations (id, etf_id, country_code, weight_percentage)
VALUES
    ('dca89700-4e01-4fad-831a-315f4098ad4e', '9e3576fe-1e9e-41c3-8089-fefec9f420d0', 'US', 59.05),
    ('613a51bc-dae1-40ee-b1ee-c9e5108890d5', '9e3576fe-1e9e-41c3-8089-fefec9f420d0', 'CN', 33.1),
    ('ca939399-6528-4e26-a8ea-a761405f075a', '9e3576fe-1e9e-41c3-8089-fefec9f420d0', 'JP', 8.67),
    ('ec233e11-ca2d-43a8-ac0f-9c32697eff35', '9e3576fe-1e9e-41c3-8089-fefec9f420d0', 'IL', 3.61),
    ('14263a8f-d0b2-4f53-8a24-b4d0bac2737b', '9e3576fe-1e9e-41c3-8089-fefec9f420d0', 'DE', 2.78),
    ('881ad575-ffab-48db-b949-a7f746359491', '9e3576fe-1e9e-41c3-8089-fefec9f420d0', 'CA', 2.48)
;

-- iShares NASDAQ 100 UCITS ETF USD (Acc)
UPDATE assets
SET asset_class = 'EQUITY',
    tracked_index_id = '99696a98-2667-48e1-9a38-851cfb8ffb45'
WHERE id = '5db84264-80bf-4ac2-b2ec-4c00ebcb1a21';

-- VanECK Space UCITS ETF R (Acc)
UPDATE assets
SET asset_class = 'EQUITY'
WHERE id = '24070ba3-6675-46e7-a2a4-47737dfa2152';

INSERT INTO etf_sector_allocations (id, etf_id, sector_id, weight_percentage)
VALUES
    ('03a5a9c3-70c0-4902-9bd2-4f481a400915', '24070ba3-6675-46e7-a2a4-47737dfa2152', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 45.2),    --'Industrials'                -- 20
    ('2f011b0d-3022-4df0-b42e-0746e808ef00', '24070ba3-6675-46e7-a2a4-47737dfa2152', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 14.9),     --'Information Technology'   -- 45
    ('fe944b10-c04c-4a81-9d8c-ab0686b9b13e', '24070ba3-6675-46e7-a2a4-47737dfa2152', 'ee4c5e5c-def2-4965-8779-00b32b1edf70', 39.8)     --'Communication Services'   -- 50
;

INSERT INTO etf_country_allocations (id, etf_id, country_code, weight_percentage)
VALUES
    ('c0e41a66-e046-41be-9dfb-4361c26a0c6c', '24070ba3-6675-46e7-a2a4-47737dfa2152', 'US', 65.45),
    ('5103ae95-bc0b-4c9f-b674-27b2b152f30e', '24070ba3-6675-46e7-a2a4-47737dfa2152', 'JP', 9.46),
    ('d1f49217-dc09-42c5-ae44-2b2658dadf18', '24070ba3-6675-46e7-a2a4-47737dfa2152', 'LU', 4.73),
    ('3a6a96f4-12d0-472f-81b6-c780aaf554b5', '24070ba3-6675-46e7-a2a4-47737dfa2152', 'CA', 4.54),
    ('a2f234c5-c710-4d0e-b0f8-958cf806cf4a', '24070ba3-6675-46e7-a2a4-47737dfa2152', 'GB', 4.03),
    ('c55c99f7-b47c-41f8-a640-728a74573c54', '24070ba3-6675-46e7-a2a4-47737dfa2152', 'KR', 3.43),
    ('c4613c4f-0a3b-4d07-a3fb-e602c052ca48', '24070ba3-6675-46e7-a2a4-47737dfa2152', 'TW', 3.41),
    ('4eb5b4a5-2bbc-4a54-a128-0ce1fa396e11', '24070ba3-6675-46e7-a2a4-47737dfa2152', 'IT', 3.01),
    ('3006b775-bd43-4f48-a745-88dca61193c7', '24070ba3-6675-46e7-a2a4-47737dfa2152', 'IL', 1.96)
;

-- Global X Uranium UCITS ETF USD (Acc)
UPDATE assets
SET asset_class = 'EQUITY',
    fund_manager = 'Global X'
WHERE id = 'bb3ca0dd-824b-412d-bc19-a2302c93229d';

INSERT INTO etf_sector_allocations (id, etf_id, sector_id, weight_percentage)
VALUES
    ('e1f5f732-2526-40a0-945a-5509b62ca65d', 'bb3ca0dd-824b-412d-bc19-a2302c93229d', 'ef8c30ca-2538-4913-b020-ead2e82644e6', 49.34),    --'Energy'                     -- 10
    ('2b69fb94-2dc6-4243-a6f8-e37bd1417ad0', 'bb3ca0dd-824b-412d-bc19-a2302c93229d', '0b743204-587a-4ba5-baea-8a9e36805063', 3.94),    --'Materials'                  -- 15
    ('5ce0b684-7f89-409a-81ca-8fdc845ad124', 'bb3ca0dd-824b-412d-bc19-a2302c93229d', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 20.32),    --'Industrials'                -- 20
    ('5ed8c594-0e6a-4518-ab06-4711db831b46', 'bb3ca0dd-824b-412d-bc19-a2302c93229d', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 1.39)     --'Information Technology'   -- 45
;

INSERT INTO etf_country_allocations (id, etf_id, country_code, weight_percentage)
VALUES
    ('2ed775bf-a13d-4852-94f3-af1de1b9f8db', 'bb3ca0dd-824b-412d-bc19-a2302c93229d', 'US', 42.35),
    ('009b09f7-86a1-4699-819d-a5a719ad2841', 'bb3ca0dd-824b-412d-bc19-a2302c93229d', 'CA', 25.51),
    ('537345a7-ca47-4cf0-89d9-e963d49421c0', 'bb3ca0dd-824b-412d-bc19-a2302c93229d', 'NL', 13.93),
    ('53fb8947-277f-4212-862c-aa2ab240b8ba', 'bb3ca0dd-824b-412d-bc19-a2302c93229d', 'AU', 7.99),
    ('f8b4bd62-2e13-4a8b-8e0d-a95846756b62', 'bb3ca0dd-824b-412d-bc19-a2302c93229d', 'KR', 7.93),
    ('22454d5e-dce8-4775-9e58-bf180754db0d', 'bb3ca0dd-824b-412d-bc19-a2302c93229d', 'JP', 3.47),
    ('16f65ed2-3faa-49c7-9188-c61267aa1235', 'bb3ca0dd-824b-412d-bc19-a2302c93229d', 'JE', 1.85),
    ('c4733751-7c79-436c-9f58-5f1bfc48d486', 'bb3ca0dd-824b-412d-bc19-a2302c93229d', 'ZA', 1.72),
    ('0e9c4ab7-e627-4eac-a5d0-cc2e6b7793e8', 'bb3ca0dd-824b-412d-bc19-a2302c93229d', 'HK', 1.47)
;

-- WisdomTree Silver
UPDATE assets
SET asset_class = 'COMMODITY'
WHERE id = '0fe95d56-be4d-4e15-a06b-fda00130fc40';

-- iShares $ Treasury Bond 0-1yr UCITS ETF
UPDATE assets
SET asset_class = 'BOND'
WHERE id = 'a82f3551-f7df-42b5-a934-fddf43d02175';

INSERT INTO etf_country_allocations (id, etf_id, country_code, weight_percentage)
VALUES
    ('59d8c319-fc79-4325-b803-b9a62f4ded8f', 'a82f3551-f7df-42b5-a934-fddf43d02175', 'US', 38.58)
;

-- iShares Silver Trust
UPDATE assets
SET asset_class = 'COMMODITY'
WHERE id = '250ba221-f82c-4d79-91e8-cfa539256d4f';

-- iShares VII PLC -iShares Core MSCI EMU UCITS ETF EUR (Acc)
UPDATE assets
SET asset_class = 'EQUITY'
WHERE id = '40b35093-06c8-40e6-8ab9-05a6dbe30d68';

INSERT INTO etf_sector_allocations (id, etf_id, sector_id, weight_percentage)
VALUES
    ('6c922cdf-b7a3-41fd-be92-a86a5fba0d97', '40b35093-06c8-40e6-8ab9-05a6dbe30d68', 'ef8c30ca-2538-4913-b020-ead2e82644e6', 3.05),    --'Energy'                     -- 10
    ('aeb75d7c-24c7-4c99-8a90-07882d19638c', '40b35093-06c8-40e6-8ab9-05a6dbe30d68', '0b743204-587a-4ba5-baea-8a9e36805063', 3.90),    --'Materials'                  -- 15
    ('492326ad-dff2-4115-8525-25695113b910', '40b35093-06c8-40e6-8ab9-05a6dbe30d68', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 20.54),    --'Industrials'                -- 20
    ('e1021f48-1f96-4097-91e6-bf71b9ec01b7', '40b35093-06c8-40e6-8ab9-05a6dbe30d68', '528fa9c4-bb5a-4d58-818f-71e65be26b35', 10.97),    --'Consumer Discretionary'    -- 25
    ('5c972235-1086-4006-8ee5-e9232d7e0b7b', '40b35093-06c8-40e6-8ab9-05a6dbe30d68', '134d7ecb-7480-44f9-a6b5-8e9211212f1c', 5.80),     --'Consumer Staples'          -- 30
    ('762f8c74-52b1-4fb0-b654-0cdae79ea049', '40b35093-06c8-40e6-8ab9-05a6dbe30d68', '75536f22-aa7f-4bb3-95c9-c7ae66295a13', 6.82),     --'Health Care'               -- 35
    ('8531a6fd-035a-4636-a438-e7ccf2d4f1d2', '40b35093-06c8-40e6-8ab9-05a6dbe30d68', 'a45f3a5a-0319-4dcb-86e1-9478b16adfa4', 25.23),     --'Financials'               -- 40
    ('28360447-b109-4288-80cd-616996e1de37', '40b35093-06c8-40e6-8ab9-05a6dbe30d68', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 12.32),     --'Information Technology'   -- 45
    ('af99bb6b-a947-4efe-abc7-028d4c0ce1c4', '40b35093-06c8-40e6-8ab9-05a6dbe30d68', 'ee4c5e5c-def2-4965-8779-00b32b1edf70', 3.85),     --'Communication Services'   -- 50
    ('7484a7c8-71d5-43ae-9722-3e055eabe3aa', '40b35093-06c8-40e6-8ab9-05a6dbe30d68', '1a09f1f7-628b-4f9a-a05c-0368cbc3a225', 6.22),     --'Utilities'                 -- 55
    ('4811c449-3669-46bb-84f3-7dc34d0c5cfc', '40b35093-06c8-40e6-8ab9-05a6dbe30d68', 'cf1d4f4e-3b6a-4c2e-9f7a-8e9d6c5b4a3f', 0.78)        --'Real Estate'             -- 60
;

INSERT INTO etf_country_allocations (id, etf_id, country_code, weight_percentage)
VALUES
    ('37b43dd9-d2ad-4943-a2cd-f26ef866f632', '40b35093-06c8-40e6-8ab9-05a6dbe30d68', 'FR', 29.81),
    ('2f935063-caf9-4948-93b3-dbb01a054095', '40b35093-06c8-40e6-8ab9-05a6dbe30d68', 'DE', 27.09),
    ('9df519cb-cbc0-49a0-867a-043a851e8bd8', '40b35093-06c8-40e6-8ab9-05a6dbe30d68', 'NL', 13.93),
    ('d6962136-bd20-4237-a3d0-7106738fe56f', '40b35093-06c8-40e6-8ab9-05a6dbe30d68', 'ES', 10.74),
    ('bc4df670-8e13-4e80-826b-3b817b210908', '40b35093-06c8-40e6-8ab9-05a6dbe30d68', 'IT', 9.00),
    ('f952c2eb-41c2-46cb-a462-bd62982a457b', '40b35093-06c8-40e6-8ab9-05a6dbe30d68', 'FI', 3.17),
    ('413a25cb-9005-4c95-9088-27b546476a2a', '40b35093-06c8-40e6-8ab9-05a6dbe30d68', 'BE', 3.13),
    ('8dfdadce-7822-4d3a-ab5d-8fe0f55d99f6', '40b35093-06c8-40e6-8ab9-05a6dbe30d68', 'IE', 1.33),
    ('92adc998-77f8-47b2-96c3-747e96569dcd', '40b35093-06c8-40e6-8ab9-05a6dbe30d68', 'AT', 0.74),
    ('0c26f1d3-b520-4abd-b7c1-805422614a27', '40b35093-06c8-40e6-8ab9-05a6dbe30d68', 'PT', 0.54)
;

-- SPDR S&P 500 UCITS ETF
UPDATE assets
SET asset_class = 'EQUITY',
    tracked_index_id = 'cf4a597d-bc40-4440-9a54-e719956d1e05'
WHERE id = '0fa99474-638a-4313-bb9f-81ebe87bbc65';

-- iShares Gold Trust
UPDATE assets
SET asset_class = 'COMMODITY'
WHERE id = 'c342c045-c36e-49e9-a65c-41dbe1374495';

-- iShares NASDAQ 100 UCITS ETF USD (Acc)
UPDATE assets
SET asset_class = 'EQUITY',
    tracked_index_id = '99696a98-2667-48e1-9a38-851cfb8ffb45'
WHERE id = 'd9363770-d91b-4913-8973-b236e0371768';

-- iShares Core S&P 500 ETF (Dist)
UPDATE assets
SET asset_class = 'EQUITY',
    tracked_index_id = 'cf4a597d-bc40-4440-9a54-e719956d1e05'
WHERE id = '188936e8-c419-421a-8813-d5c81e325713';

-- Invesco QQQ Trust
UPDATE assets
SET asset_class = 'EQUITY',
    tracked_index_id = '99696a98-2667-48e1-9a38-851cfb8ffb45'
WHERE id = '4aa41c1f-5285-4e93-8ef2-4a1171e90882';

-- iShares Core EURO STOXX 50 UCITS ETF EUR (Dist)
UPDATE assets
SET asset_class = 'EQUITY',
    tracked_index_id = 'b779ffcb-1fa1-49c5-825c-6ff0bb259d1c'
WHERE id = 'f72ab37b-8ef8-427c-9660-df243595e2cb';

-- WisdomTree Core Physical Gold USD ETC
UPDATE assets
SET asset_class = 'COMMODITY'
WHERE id = '142cae00-06fe-465f-bbac-4741caf74cc9';

-- Invesco S&P 500 UCITS ETF (Dist)
UPDATE assets
SET asset_class = 'EQUITY',
    tracked_index_id = 'cf4a597d-bc40-4440-9a54-e719956d1e05'
WHERE id = 'c9d8f0da-45dc-4682-972b-beba6a16d75d';

-- iShares MSCI Europe ESG Enhanced CTB UCITS ETF EUR Inc (Dist)
UPDATE assets
SET asset_class = 'EQUITY'
WHERE id = '3cd133b1-1207-40fe-b91b-ab3e62afbfa6';

INSERT INTO etf_sector_allocations (id, etf_id, sector_id, weight_percentage)
VALUES
    ('11ff37ec-1f36-4950-aab6-95445e77dcce', '3cd133b1-1207-40fe-b91b-ab3e62afbfa6', 'ef8c30ca-2538-4913-b020-ead2e82644e6', 3.83),    --'Energy'                     -- 10
    ('ce71b334-e406-4b85-8210-f54b23cddf23', '3cd133b1-1207-40fe-b91b-ab3e62afbfa6', '0b743204-587a-4ba5-baea-8a9e36805063', 3.73),    --'Materials'                  -- 15
    ('312b18b6-6129-42ab-b01c-6c7b8a9770ad', '3cd133b1-1207-40fe-b91b-ab3e62afbfa6', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 17.61),    --'Industrials'                -- 20
    ('652a0280-f2c8-4d5a-bb87-a1cfcbc1a0ff', '3cd133b1-1207-40fe-b91b-ab3e62afbfa6', '528fa9c4-bb5a-4d58-818f-71e65be26b35', 7.44),    --'Consumer Discretionary'    -- 25
    ('71b7c38e-4f79-44b9-b9d0-396274fe3d46', '3cd133b1-1207-40fe-b91b-ab3e62afbfa6', '134d7ecb-7480-44f9-a6b5-8e9211212f1c', 9.05),     --'Consumer Staples'          -- 30
    ('f2a4e5be-0eff-4b7f-863f-522e2402740f', '3cd133b1-1207-40fe-b91b-ab3e62afbfa6', '75536f22-aa7f-4bb3-95c9-c7ae66295a13', 14.26),     --'Health Care'               -- 35
    ('7cf20247-887e-4388-b07d-ddc9366d8bdb', '3cd133b1-1207-40fe-b91b-ab3e62afbfa6', 'a45f3a5a-0319-4dcb-86e1-9478b16adfa4', 24.87),     --'Financials'               -- 40
    ('5fc043b0-cb8f-456e-911b-007a6aea6f12', '3cd133b1-1207-40fe-b91b-ab3e62afbfa6', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 7.96),     --'Information Technology'   -- 45
    ('3e3b307b-ef1a-4681-bd7c-6f230b2e55a9', '3cd133b1-1207-40fe-b91b-ab3e62afbfa6', 'ee4c5e5c-def2-4965-8779-00b32b1edf70', 3.33),     --'Communication Services'   -- 50
    ('5643bf3f-a7cb-4a1d-a898-33f17d5a5796', '3cd133b1-1207-40fe-b91b-ab3e62afbfa6', '1a09f1f7-628b-4f9a-a05c-0368cbc3a225', 4.91),     --'Utilities'                 -- 55
    ('55a492b0-bf91-4923-9c93-4b20f4842670', '3cd133b1-1207-40fe-b91b-ab3e62afbfa6', 'cf1d4f4e-3b6a-4c2e-9f7a-8e9d6c5b4a3f', 2.21)        --'Real Estate'             -- 60
;

INSERT INTO etf_country_allocations (id, etf_id, country_code, weight_percentage)
VALUES
    ('56f5c974-46ec-41c0-a6c0-b0ede993fe5a', '3cd133b1-1207-40fe-b91b-ab3e62afbfa6', 'GB', 20.52),
    ('fe94d52e-747e-42f8-8c96-b89710e2d421', '3cd133b1-1207-40fe-b91b-ab3e62afbfa6', 'FR', 15.96),
    ('e5d07ef1-9504-4c4f-a0ae-cfcac1db570c', '3cd133b1-1207-40fe-b91b-ab3e62afbfa6', 'DE', 14.84),
    ('80e17149-640f-4775-88c6-f12f761656e0', '3cd133b1-1207-40fe-b91b-ab3e62afbfa6', 'CH', 14.70),
    ('8aa772c6-046c-41d1-805b-e4d717fae39f', '3cd133b1-1207-40fe-b91b-ab3e62afbfa6', 'NL', 7.41),
    ('e3c37c7c-a631-44f7-a88c-2399e29ba1ce', '3cd133b1-1207-40fe-b91b-ab3e62afbfa6', 'ES', 5.88),
    ('8fc4bedc-00e4-4fcc-a533-b038433cee1d', '3cd133b1-1207-40fe-b91b-ab3e62afbfa6', 'IT', 5.47),
    ('8c6c3cb5-2c3f-4041-ab37-2887f98ff49b', '3cd133b1-1207-40fe-b91b-ab3e62afbfa6', 'SE', 4.75),
    ('eac09f17-49ff-4a79-b934-940940804681', '3cd133b1-1207-40fe-b91b-ab3e62afbfa6', 'DK', 2.66),
    ('3f44dc57-9cb9-4d18-b723-9d35df66552a', '3cd133b1-1207-40fe-b91b-ab3e62afbfa6', 'BE', 1.90)
;

-- HSBC MSCI World UCITS ETF (Dist)
UPDATE assets
SET asset_class = 'EQUITY'
WHERE id = '5c526cd2-44f6-4de7-b5ea-fd83e081bf35';

INSERT INTO etf_sector_allocations (id, etf_id, sector_id, weight_percentage)
VALUES
    ('7245e3f4-a5bb-489c-9848-0aee8e53876e', '5c526cd2-44f6-4de7-b5ea-fd83e081bf35', 'ef8c30ca-2538-4913-b020-ead2e82644e6', 3.35),    --'Energy'                     -- 10
    ('7ca613e2-7c71-4896-9678-3c58ccb9b7ad', '5c526cd2-44f6-4de7-b5ea-fd83e081bf35', '0b743204-587a-4ba5-baea-8a9e36805063', 2.25),    --'Materials'                  -- 15
    ('090b13b0-1f6a-4a2a-ada8-f9427870e094', '5c526cd2-44f6-4de7-b5ea-fd83e081bf35', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 10.18),    --'Industrials'                -- 20
    ('fd8d153e-bc5c-4f80-83a4-d9171b44bd4c', '5c526cd2-44f6-4de7-b5ea-fd83e081bf35', '528fa9c4-bb5a-4d58-818f-71e65be26b35', 10.35),    --'Consumer Discretionary'    -- 25
    ('2d2ec031-4e9c-400d-b375-d23567f88266', '5c526cd2-44f6-4de7-b5ea-fd83e081bf35', '134d7ecb-7480-44f9-a6b5-8e9211212f1c', 5.07),     --'Consumer Staples'          -- 30
    ('67ea146a-5adf-4020-b17e-2d5a3dffec3d', '5c526cd2-44f6-4de7-b5ea-fd83e081bf35', '75536f22-aa7f-4bb3-95c9-c7ae66295a13', 8.63),     --'Health Care'               -- 35
    ('c9dcd31e-6a55-4b1c-8fb8-712118e6b3a6', '5c526cd2-44f6-4de7-b5ea-fd83e081bf35', 'a45f3a5a-0319-4dcb-86e1-9478b16adfa4', 14.03),     --'Financials'               -- 40
    ('35976c5c-2bde-4f4f-a669-4d90c22b703d', '5c526cd2-44f6-4de7-b5ea-fd83e081bf35', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 29.92),     --'Information Technology'   -- 45
    ('5ff4e718-9cf8-4475-b8a2-db2da425d356', '5c526cd2-44f6-4de7-b5ea-fd83e081bf35', 'ee4c5e5c-def2-4965-8779-00b32b1edf70', 8.57),     --'Communication Services'   -- 50
    ('fa6e276d-f3f1-4a0b-bb20-c0a3f34f0998', '5c526cd2-44f6-4de7-b5ea-fd83e081bf35', '1a09f1f7-628b-4f9a-a05c-0368cbc3a225', 2.58),     --'Utilities'                 -- 55
    ('11e40c34-d047-4829-bc01-b5b5d45ea05d', '5c526cd2-44f6-4de7-b5ea-fd83e081bf35', 'cf1d4f4e-3b6a-4c2e-9f7a-8e9d6c5b4a3f', 1.78)        --'Real Estate'             -- 60
;

INSERT INTO etf_country_allocations (id, etf_id, country_code, weight_percentage)
VALUES
    ('a55a8581-ae4e-4434-a5da-79ac9593bae6', '5c526cd2-44f6-4de7-b5ea-fd83e081bf35', 'US', 69.33),
    ('3dd0e555-bf89-4f95-bb37-50b4694fa093', '5c526cd2-44f6-4de7-b5ea-fd83e081bf35', 'JP', 5.52),
    ('98a1c596-34bc-4528-8ff8-e29809715c0e', '5c526cd2-44f6-4de7-b5ea-fd83e081bf35', 'GB', 3.41),
    ('860d7c5a-2bba-40c4-a94f-d276b1aa26fa', '5c526cd2-44f6-4de7-b5ea-fd83e081bf35', 'CA', 2.81),
    ('43b981ab-0834-4f98-9230-327c3d0bfcf4', '5c526cd2-44f6-4de7-b5ea-fd83e081bf35', 'FR', 2.42),
    ('9f03bbf8-db20-40c8-bcbc-9c5c0c7974c2', '5c526cd2-44f6-4de7-b5ea-fd83e081bf35', 'CH', 2.39),
    ('d4b41049-6ebf-483b-96ce-18261bed2c71', '5c526cd2-44f6-4de7-b5ea-fd83e081bf35', 'DE', 2.28),
    ('abed767a-215e-4eb7-bdce-58db1bc1a35c', '5c526cd2-44f6-4de7-b5ea-fd83e081bf35', 'AU', 1.55),
    ('cc669ef8-68a6-4a3d-813a-751b5961f1fc', '5c526cd2-44f6-4de7-b5ea-fd83e081bf35', 'NL', 1.49),
    ('07159aa3-71dd-4572-831f-e6c4743a7b4f', '5c526cd2-44f6-4de7-b5ea-fd83e081bf35', 'IR', 1.29)
;

-- iShares VII PLC - iShares NASDAQ 100 UCITS ETF (Acc)
UPDATE assets
SET asset_class = 'EQUITY',
    tracked_index_id = '99696a98-2667-48e1-9a38-851cfb8ffb45'
WHERE id = '6024a513-45cf-4146-9d6f-cba3efd78e60';

-- Vanguard S&P 500 UCITS ETF (Dist)
UPDATE assets
SET asset_class = 'EQUITY',
    tracked_index_id = 'cf4a597d-bc40-4440-9a54-e719956d1e05'
WHERE id = '4d2c27e8-dd0e-4eac-865e-9aabf7a84c83';

-- 9947b11a-69cb-4a8a-a4d0-dd822ac24491	Vanguard FTSE All-World UCITS ETF (Acc)
UPDATE assets
SET asset_class = 'EQUITY'
WHERE id = '9947b11a-69cb-4a8a-a4d0-dd822ac24491';

INSERT INTO etf_sector_allocations (id, etf_id, sector_id, weight_percentage)
VALUES
    ('dd461a49-b760-420c-8cc5-792d85d1bcf6', '9947b11a-69cb-4a8a-a4d0-dd822ac24491', 'ef8c30ca-2538-4913-b020-ead2e82644e6', 3.64),    --'Energy'                     -- 10
    ('544b9650-623a-44ea-b5b0-3b4efd4c3c3f', '9947b11a-69cb-4a8a-a4d0-dd822ac24491', '0b743204-587a-4ba5-baea-8a9e36805063', 2.91),    --'Materials'                  -- 15
    ('669ea676-6e30-4a93-a993-7d4c351a83af', '9947b11a-69cb-4a8a-a4d0-dd822ac24491', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 12.65),    --'Industrials'                -- 20
    ('58082e80-b91c-4ead-92de-a5aff2972ede', '9947b11a-69cb-4a8a-a4d0-dd822ac24491', '528fa9c4-bb5a-4d58-818f-71e65be26b35', 13.25),    --'Consumer Discretionary'    -- 25
    ('74e9157b-930c-4383-92ea-2cec65e57477', '9947b11a-69cb-4a8a-a4d0-dd822ac24491', '134d7ecb-7480-44f9-a6b5-8e9211212f1c', 4.22),     --'Consumer Staples'          -- 30
    ('c16490c8-1fa0-4258-9f5e-f3eb96b3bceb', '9947b11a-69cb-4a8a-a4d0-dd822ac24491', '75536f22-aa7f-4bb3-95c9-c7ae66295a13', 8.11),     --'Health Care'               -- 35
    ('acdea796-daf2-4926-93a3-0288c455b465', '9947b11a-69cb-4a8a-a4d0-dd822ac24491', 'a45f3a5a-0319-4dcb-86e1-9478b16adfa4', 15.18),     --'Financials'               -- 40
    ('fa823805-b21e-4504-b251-45997387f48a', '9947b11a-69cb-4a8a-a4d0-dd822ac24491', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 32.46),     --'Information Technology'   -- 45
    ('fa6b2e03-7c4b-4a03-ab29-ffbdfaf8abda', '9947b11a-69cb-4a8a-a4d0-dd822ac24491', 'ee4c5e5c-def2-4965-8779-00b32b1edf70', 2.81),     --'Communication Services'   -- 50
    ('b7287ed8-a922-41a6-b677-38b35c83f3b1', '9947b11a-69cb-4a8a-a4d0-dd822ac24491', '1a09f1f7-628b-4f9a-a05c-0368cbc3a225', 2.80),     --'Utilities'                 -- 55
    ('c7d013c9-26ce-4595-81d7-10b00f1c6ce2', '9947b11a-69cb-4a8a-a4d0-dd822ac24491', 'cf1d4f4e-3b6a-4c2e-9f7a-8e9d6c5b4a3f', 1.93)        --'Real Estate'             -- 60
;

INSERT INTO etf_country_allocations (id, etf_id, country_code, weight_percentage)
VALUES
    ('537f90ed-7977-45fc-bfaf-d3b41a4d910a', '9947b11a-69cb-4a8a-a4d0-dd822ac24491', 'US', 62.96),
    ('bc45ea03-3935-4c03-b343-b1cc4e5d1927', '9947b11a-69cb-4a8a-a4d0-dd822ac24491', 'JP', 5.73),
    ('fd62b645-3796-490d-bd7d-10e117667a18', '9947b11a-69cb-4a8a-a4d0-dd822ac24491', 'CN', 3.45),
    ('c430186a-69b5-405d-90cd-6cc7fa071434', '9947b11a-69cb-4a8a-a4d0-dd822ac24491', 'GB', 3.32),
    ('ddc18fab-6f08-4510-aac6-c5f64bf707b3', '9947b11a-69cb-4a8a-a4d0-dd822ac24491', 'CA', 2.84),
    ('e26449a6-9a57-4279-9f87-b3c729b1609d', '9947b11a-69cb-4a8a-a4d0-dd822ac24491', 'TW', 2.29),
    ('24effac5-08c5-4617-b03c-a414651ff31c', '9947b11a-69cb-4a8a-a4d0-dd822ac24491', 'FR', 2.12),
    ('10502802-d997-4e4e-9f0a-5e3b7877fd8c', '9947b11a-69cb-4a8a-a4d0-dd822ac24491', 'DE', 2.01),
    ('0ff439c1-0312-4374-9e6a-b2b0c53f22da', '9947b11a-69cb-4a8a-a4d0-dd822ac24491', 'CH', 1.97),
    ('cf209cfe-785a-4ebf-afba-88f7482b1c70', '9947b11a-69cb-4a8a-a4d0-dd822ac24491', 'IN', 1.94)
;