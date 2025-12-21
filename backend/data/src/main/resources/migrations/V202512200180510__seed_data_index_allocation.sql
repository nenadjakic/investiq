-- S&P 500 Index Allocations
INSERT INTO public.index_sector_allocations (id, index_id, sector_id, weight_percentage)
VALUES
    ('1e2f3a4b-5c6d-7e8f-9012-3456789abcde', 'cf4a597d-bc40-4440-9a54-e719956d1e05', 'ef8c30ca-2538-4913-b020-ead2e82644e6', 2.86),    --'Energy'                     -- 10
    ('2b3c4d5e-6f70-8123-4567-89abcdef0123', 'cf4a597d-bc40-4440-9a54-e719956d1e05', '0b743204-587a-4ba5-baea-8a9e36805063', 1.69),    --'Materials'                  -- 15
    ('3c4d5e6f-7081-2345-6789-abcd01234567', 'cf4a597d-bc40-4440-9a54-e719956d1e05', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 8.20),    --'Industrials'                -- 20
    ('4d5e6f70-8123-4567-89ab-cdef01234567', 'cf4a597d-bc40-4440-9a54-e719956d1e05', '528fa9c4-bb5a-4d58-818f-71e65be26b35', 10.38),    --'Consumer Discretionary'    -- 25
    ('5e6f7081-2345-6789-abcd-ef0123456789', 'cf4a597d-bc40-4440-9a54-e719956d1e05', '134d7ecb-7480-44f9-a6b5-8e9211212f1c', 4.80),     --'Consumer Staples'          -- 30
    ('5c6ac27e-f94c-47a3-b3ea-1ba58b493466', 'cf4a597d-bc40-4440-9a54-e719956d1e05', '75536f22-aa7f-4bb3-95c9-c7ae66295a13', 9.54),     --'Health Care'               -- 35
    ('8dfe974e-49ff-4c87-af1f-9c27d8ea347a', 'cf4a597d-bc40-4440-9a54-e719956d1e05', 'a45f3a5a-0319-4dcb-86e1-9478b16adfa4', 13.51),     --'Financials'               -- 40
    ('4ddda72e-1a6d-4b13-af0b-56cd117f7308', 'cf4a597d-bc40-4440-9a54-e719956d1e05', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 34.25),     --'Information Technology'   -- 45
    ('c5d5e5a3-ec64-41ca-9d99-1a8a2a65a266', 'cf4a597d-bc40-4440-9a54-e719956d1e05', 'ee4c5e5c-def2-4965-8779-00b32b1edf70', 10.45),     --'Communication Services'   -- 50
    ('2c617574-dbf9-499d-9ee4-df52e409784f', 'cf4a597d-bc40-4440-9a54-e719956d1e05', '1a09f1f7-628b-4f9a-a05c-0368cbc3a225', 2.24),     --'Utilities'                 -- 55
    ('d045dea0-50de-4f55-9016-e7c67bfbd276', 'cf4a597d-bc40-4440-9a54-e719956d1e05', 'cf1d4f4e-3b6a-4c2e-9f7a-8e9d6c5b4a3f', 1.83)        --'Real Estate'             -- 60
;

INSERT INTO public.index_country_allocations (id, index_id, country_code, weight_percentage)
VALUES
    ('6f708123-4567-89ab-cdef-0123456789ab', 'cf4a597d-bc40-4440-9a54-e719956d1e05', 'US', 100)
;

-- NASDAQ Composite Index Allocations
INSERT INTO public.index_sector_allocations (id, index_id, sector_id, weight_percentage)
VALUES
    ('9ab55df6-4e0c-4e8e-a057-697ab503d9dd', '99696a98-2667-48e1-9a38-851cfb8ffb45', 'ef8c30ca-2538-4913-b020-ead2e82644e6', 0.48),    --'Energy'                     -- 10
    ('52dea33e-a2d1-4e0a-9107-19fc20d57235', '99696a98-2667-48e1-9a38-851cfb8ffb45', '0b743204-587a-4ba5-baea-8a9e36805063', 1.02),    --'Materials'                  -- 15
    ('f4a20fae-a410-4d89-ba57-ea68ea8a9a6a', '99696a98-2667-48e1-9a38-851cfb8ffb45', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 3.82),    --'Industrials'                -- 20
    ('db98b9d2-e2d0-4685-8265-e9f3200bdb8e', '99696a98-2667-48e1-9a38-851cfb8ffb45', '528fa9c4-bb5a-4d58-818f-71e65be26b35', 13.11),    --'Consumer Discretionary'    -- 25
    ('9a527b80-5d9c-44a6-addf-816bb10bec75', '99696a98-2667-48e1-9a38-851cfb8ffb45', '134d7ecb-7480-44f9-a6b5-8e9211212f1c', 4.47),     --'Consumer Staples'          -- 30
    ('aafd9382-67ee-4025-89e0-98e6ef0c913f', '99696a98-2667-48e1-9a38-851cfb8ffb45', '75536f22-aa7f-4bb3-95c9-c7ae66295a13', 4.75),     --'Health Care'               -- 35
    ('9d716b82-3c18-4994-905a-2c1249ad4004', '99696a98-2667-48e1-9a38-851cfb8ffb45', 'a45f3a5a-0319-4dcb-86e1-9478b16adfa4', 0.31),     --'Financials'               -- 40
    ('90d35f98-0d33-4a2b-9904-f3bca941958d', '99696a98-2667-48e1-9a38-851cfb8ffb45', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 55.01),     --'Information Technology'   -- 45
    ('e3d6cf1f-3e4a-4338-aa8e-e142aef6e86a', '99696a98-2667-48e1-9a38-851cfb8ffb45', 'ee4c5e5c-def2-4965-8779-00b32b1edf70', 15.33),     --'Communication Services'   -- 50
    ('16b23fa6-9b03-41be-8f9c-182dbc935d26', '99696a98-2667-48e1-9a38-851cfb8ffb45', '1a09f1f7-628b-4f9a-a05c-0368cbc3a225', 1.36),     --'Utilities'                 -- 55
    ('c1c2fce1-ca85-4285-a488-2344e86a2d9b', '99696a98-2667-48e1-9a38-851cfb8ffb45', 'cf1d4f4e-3b6a-4c2e-9f7a-8e9d6c5b4a3f', 0.00)        --'Real Estate'             -- 60
;

INSERT INTO public.index_country_allocations (id, index_id, country_code, weight_percentage)
VALUES
    ('ab248bb5-4eaa-4694-a61a-85baa732ec83', '99696a98-2667-48e1-9a38-851cfb8ffb45', 'US', 96.99),
    ('bc359cc6-5fbb-57a5-b72b-96cbb843fd94', '99696a98-2667-48e1-9a38-851cfb8ffb45', 'CA', 1.36),
    ('fef9d6f0-ab8b-4aa2-9588-a7f9640fc665', '99696a98-2667-48e1-9a38-851cfb8ffb45', 'NL', 0.70),
    ('8a80cf01-3c61-4ab0-b65c-40e6ee65548d', '99696a98-2667-48e1-9a38-851cfb8ffb45', 'CN', 0.39),
    ('dbb3e996-5ca7-466e-b74f-ba532d4f0817', '99696a98-2667-48e1-9a38-851cfb8ffb45', 'GB', 0.37)
;

-- EURO STOXX 50 Index Allocations
INSERT INTO public.index_sector_allocations (id, index_id, sector_id, weight_percentage)
VALUES
    ('9b4c58ca-ad5e-4fd1-a77b-bd3b4617f533', 'b779ffcb-1fa1-49c5-825c-6ff0bb259d1c', 'ef8c30ca-2538-4913-b020-ead2e82644e6', 3.85),    --'Energy'                     -- 10
    ('3fb3c2c8-a9bf-4d18-876d-46b6aff6516a', 'b779ffcb-1fa1-49c5-825c-6ff0bb259d1c', '0b743204-587a-4ba5-baea-8a9e36805063', 3.15),    --'Materials'                  -- 15
    ('a382c354-a6ad-48de-9ee0-fbea5f5fa643', 'b779ffcb-1fa1-49c5-825c-6ff0bb259d1c', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 20.88),    --'Industrials'                -- 20
    ('50424e5a-4c37-4e74-8ce6-8b1e9161c39a', 'b779ffcb-1fa1-49c5-825c-6ff0bb259d1c', '528fa9c4-bb5a-4d58-818f-71e65be26b35', 12.48),    --'Consumer Discretionary'    -- 25
    ('ce9a8765-c3a6-437e-aa96-864d66bdf006', 'b779ffcb-1fa1-49c5-825c-6ff0bb259d1c', '134d7ecb-7480-44f9-a6b5-8e9211212f1c', 5.37),     --'Consumer Staples'          -- 30
    ('bc505187-5842-4d7a-80c3-816738e26b13', 'b779ffcb-1fa1-49c5-825c-6ff0bb259d1c', '75536f22-aa7f-4bb3-95c9-c7ae66295a13', 6.34),     --'Health Care'               -- 35
    ('abc3e8cb-6d2f-42cb-87e4-12f222c02d5c', 'b779ffcb-1fa1-49c5-825c-6ff0bb259d1c', 'a45f3a5a-0319-4dcb-86e1-9478b16adfa4', 25.86),     --'Financials'               -- 40
    ('0ae397bb-b522-449c-9978-aab56bd127ba', 'b779ffcb-1fa1-49c5-825c-6ff0bb259d1c', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 14.92),     --'Information Technology'   -- 45
    ('b22cfa0e-dc5c-4593-9bd7-bdd6d42aa5f3', 'b779ffcb-1fa1-49c5-825c-6ff0bb259d1c', 'ee4c5e5c-def2-4965-8779-00b32b1edf70', 2.26),     --'Communication Services'   -- 50
    ('d4ade7d6-9f11-4932-8cc4-41bf11a17fa7', 'b779ffcb-1fa1-49c5-825c-6ff0bb259d1c', '1a09f1f7-628b-4f9a-a05c-0368cbc3a225', 4.24),     --'Utilities'                 -- 55
    ('18e6952e-f654-4ccb-bb91-e85ddc2a8c26', 'b779ffcb-1fa1-49c5-825c-6ff0bb259d1c', 'cf1d4f4e-3b6a-4c2e-9f7a-8e9d6c5b4a3f', 0.00)        --'Real Estate'             -- 60
;

INSERT INTO public.index_country_allocations (id, index_id, country_code, weight_percentage)
VALUES
    ('cc679892-f8c8-43a1-aa37-b1b3f9f629c6', 'b779ffcb-1fa1-49c5-825c-6ff0bb259d1c', 'FR', 33.51),
    ('5717e0db-4b61-4fba-a009-8b6e48216346', 'b779ffcb-1fa1-49c5-825c-6ff0bb259d1c', 'DE', 29.99),
    ('2168baae-f84a-4275-b3a3-4e59c9f58e30', 'b779ffcb-1fa1-49c5-825c-6ff0bb259d1c', 'NL', 14.19),
    ('69d41b0f-b25f-4767-b392-f1021fb8e854', 'b779ffcb-1fa1-49c5-825c-6ff0bb259d1c', 'ES', 10.17),
    ('7cbbd3b5-53bb-46a6-a7f2-76ea30ee0a9c', 'b779ffcb-1fa1-49c5-825c-6ff0bb259d1c', 'IT', 7.85),
    ('db086895-82d9-4010-9dc3-ab1f87779276', 'b779ffcb-1fa1-49c5-825c-6ff0bb259d1c', 'BE', 2.43),
    ('bc469de1-81a5-454b-8521-382b255f43eb', 'b779ffcb-1fa1-49c5-825c-6ff0bb259d1c', 'FI', 1.21)
;

-- STOXX Europe 600 Index Allocations
INSERT INTO public.index_sector_allocations (id, index_id, sector_id, weight_percentage)
VALUES
    ('f0b2f3dd-e6e5-4521-9e73-e3d1226ed527', 'b98c0954-19bf-4968-9e5e-08ecd358cd44', 'ef8c30ca-2538-4913-b020-ead2e82644e6', 4.38),    --'Energy'                     -- 10
    ('0229c62c-d57b-4aa6-aa01-6be0d79d93ed', 'b98c0954-19bf-4968-9e5e-08ecd358cd44', '0b743204-587a-4ba5-baea-8a9e36805063', 5.33),    --'Materials'                  -- 15
    ('e071ed4e-cb0a-4868-9fc6-f474e6e49f65', 'b98c0954-19bf-4968-9e5e-08ecd358cd44', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 19.21),    --'Industrials'                -- 20
    ('f67e22a3-f888-4198-afcc-67f7f32f41c2', 'b98c0954-19bf-4968-9e5e-08ecd358cd44', '528fa9c4-bb5a-4d58-818f-71e65be26b35', 8.31),    --'Consumer Discretionary'    -- 25
    ('85a2137e-b5c5-4928-9b53-79a255c67e75', 'b98c0954-19bf-4968-9e5e-08ecd358cd44', '134d7ecb-7480-44f9-a6b5-8e9211212f1c', 8.66),     --'Consumer Staples'          -- 30
    ('58dcc93a-f4ef-423c-95c6-184e6681e95d', 'b98c0954-19bf-4968-9e5e-08ecd358cd44', '75536f22-aa7f-4bb3-95c9-c7ae66295a13', 13.41),     --'Health Care'               -- 35
    ('b95a3277-c0cc-464a-bd8a-c462709b3c50', 'b98c0954-19bf-4968-9e5e-08ecd358cd44', 'a45f3a5a-0319-4dcb-86e1-9478b16adfa4', 24.25),     --'Financials'               -- 40
    ('24eb20db-a59c-4e38-a1b2-2bd9dc6270be', 'b98c0954-19bf-4968-9e5e-08ecd358cd44', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 7.12),     --'Information Technology'   -- 45
    ('95632e79-c446-4cda-91b6-63a11ba2d5de', 'b98c0954-19bf-4968-9e5e-08ecd358cd44', 'ee4c5e5c-def2-4965-8779-00b32b1edf70', 2.97),     --'Communication Services'   -- 50
    ('057880e2-c6b6-42a5-8c2e-80de813ff57c', 'b98c0954-19bf-4968-9e5e-08ecd358cd44', '1a09f1f7-628b-4f9a-a05c-0368cbc3a225', 4.43),     --'Utilities'                 -- 55
    ('61bbfc7b-a932-4c75-9274-8e5473df7493', 'b98c0954-19bf-4968-9e5e-08ecd358cd44', 'cf1d4f4e-3b6a-4c2e-9f7a-8e9d6c5b4a3f', 1.15)        --'Real Estate'             -- 60
;

INSERT INTO public.index_country_allocations (id, index_id, country_code, weight_percentage)
VALUES
    ('feb72c2d-1412-4d6d-a656-0a5683734160', 'b98c0954-19bf-4968-9e5e-08ecd358cd44', 'GB', 22.62),
    ('36338a52-9973-409d-97bf-78ca65cdd90e', 'b98c0954-19bf-4968-9e5e-08ecd358cd44', 'FR', 16.43),
    ('fbda978f-3ada-4c2f-a11f-d6a64e92e91d', 'b98c0954-19bf-4968-9e5e-08ecd358cd44', 'DE', 14.05),
    ('8c5f69e1-a3ae-45dd-84a4-093c1d25085e', 'b98c0954-19bf-4968-9e5e-08ecd358cd44', 'CH', 13.76),
    ('7a42e3aa-d558-4f67-b940-6000befeec6d', 'b98c0954-19bf-4968-9e5e-08ecd358cd44', 'NL', 6.87),
    ('49837ded-a8f3-4998-8b83-752500129915', 'b98c0954-19bf-4968-9e5e-08ecd358cd44', 'ES', 5.66),
    ('652a3a6c-4b12-4fd9-83d5-021106d31b64', 'b98c0954-19bf-4968-9e5e-08ecd358cd44', 'IT', 5.14),
    ('fbdd4448-7d83-49e6-86ad-b4d880a26d61', 'b98c0954-19bf-4968-9e5e-08ecd358cd44', 'SE', 4.90),
    ('f7230538-e967-40d8-9b54-0104a5f0f446', 'b98c0954-19bf-4968-9e5e-08ecd358cd44', 'DK', 3.05),
    ('9bf2a773-5ed6-452b-b137-9ab2a013fe1d', 'b98c0954-19bf-4968-9e5e-08ecd358cd44', 'FI', 1.84)
;