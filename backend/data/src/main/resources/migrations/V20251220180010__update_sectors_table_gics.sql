INSERT INTO sectors (id, "name")
VALUES
  ('ef8c30ca-2538-4913-b020-ead2e82644e6', 'Energy'),                   -- 10
  ('0b743204-587a-4ba5-baea-8a9e36805063', 'Materials'),                -- 15
  ('f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 'Industrials'),              -- 20
  ('528fa9c4-bb5a-4d58-818f-71e65be26b35', 'Consumer Discretionary'),   -- 25
  ('134d7ecb-7480-44f9-a6b5-8e9211212f1c', 'Consumer Staples'),         -- 30
  ('75536f22-aa7f-4bb3-95c9-c7ae66295a13', 'Health Care'),              -- 35
  ('a45f3a5a-0319-4dcb-86e1-9478b16adfa4', 'Financials'),               -- 40
  ('e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 'Information Technology'),   -- 45
  ('ee4c5e5c-def2-4965-8779-00b32b1edf70', 'Communication Services'),    -- 50
  ('1a09f1f7-628b-4f9a-a05c-0368cbc3a225', 'Utilities'),                -- 55
  ('cf1d4f4e-3b6a-4c2e-9f7a-8e9d6c5b4a3f', 'Real Estate')               -- 60
ON CONFLICT (id)
DO UPDATE SET
    "name" = EXCLUDED."name";
