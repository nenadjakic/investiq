INSERT INTO sectors (id, "name")
VALUES
  ('e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 'Technology'),
  ('75536f22-aa7f-4bb3-95c9-c7ae66295a13', 'Healthcare'),
  ('a45f3a5a-0319-4dcb-86e1-9478b16adfa4', 'Financials'),
  ('48c8d081-76ef-44e7-9214-2e0163d7df14', 'Consumer Discretionary'),
  ('654b735b-0307-493d-ac85-8db016ff8c71', 'Communication Services'),
  ('99f8ca19-13c7-40bc-8423-255c8cb6defd', 'Industrials'),
  ('8f787dbc-811f-4d83-8ed0-a88eb2b894f6', 'Consumer Staples'),
  ('9d6c6c47-b1c2-4c80-ac97-e2932074b60f', 'Energy'),
  ('1cc2d021-368f-4a1b-a353-ab7b5185ee4d', 'Utilities'),
  ('67250269-e719-4853-9a0f-c4c0d4df918c', 'Real Estate'),
  ('40395229-9ea2-42b0-aa71-502fd9b7918e', 'Materials');

INSERT INTO public.industries (id, "name", sector_id)
VALUES
-- Information Technology
  (gen_random_uuid(), 'Information Technology Services', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e'), -- OK
  (gen_random_uuid(), 'Software', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e'),
  (gen_random_uuid(), 'Hardware', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e'),
  (gen_random_uuid(), 'Semiconductors', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e'),
-- Healthcare
  (gen_random_uuid(), 'Drug Manufacturers - General', '75536f22-aa7f-4bb3-95c9-c7ae66295a13'), -- OK
  (gen_random_uuid(), 'Pharmaceuticals', '75536f22-aa7f-4bb3-95c9-c7ae66295a13'),
  (gen_random_uuid(), 'Biotechnology', '75536f22-aa7f-4bb3-95c9-c7ae66295a13'),
  (gen_random_uuid(), 'Healthcare Equipment', '75536f22-aa7f-4bb3-95c9-c7ae66295a13'),
-- Financials
  (gen_random_uuid(), 'Banks', 'a45f3a5a-0319-4dcb-86e1-9478b16adfa4'),
  (gen_random_uuid(), 'Insurance', 'a45f3a5a-0319-4dcb-86e1-9478b16adfa4'),
  (gen_random_uuid(), 'Capital Markets', 'a45f3a5a-0319-4dcb-86e1-9478b16adfa4'),
-- Consumer Discretionary
  (gen_random_uuid(), 'Retail', '48c8d081-76ef-44e7-9214-2e0163d7df14'),
  (gen_random_uuid(), 'Automotive', '48c8d081-76ef-44e7-9214-2e0163d7df14'),
  (gen_random_uuid(), 'Consumer Services', '48c8d081-76ef-44e7-9214-2e0163d7df14'),
-- Communication Services
  (gen_random_uuid(), 'Media', '654b735b-0307-493d-ac85-8db016ff8c71'),
  (gen_random_uuid(), 'Telecommunications', '654b735b-0307-493d-ac85-8db016ff8c71'),
-- Industrials
  (gen_random_uuid(), 'Capital Goods', '99f8ca19-13c7-40bc-8423-255c8cb6defd'),
  (gen_random_uuid(), 'Transportation', '99f8ca19-13c7-40bc-8423-255c8cb6defd'),
  (gen_random_uuid(), 'Commercial Services', '99f8ca19-13c7-40bc-8423-255c8cb6defd'),
-- Consumer Staples
  (gen_random_uuid(), 'Food & Staples Retailing', '8f787dbc-811f-4d83-8ed0-a88eb2b894f6'),
  (gen_random_uuid(), 'Beverages', '8f787dbc-811f-4d83-8ed0-a88eb2b894f6'),
  (gen_random_uuid(), 'Household Products', '8f787dbc-811f-4d83-8ed0-a88eb2b894f6'),
-- Energy
  (gen_random_uuid(), 'Oil, Gas & Consumable Fuels', '9d6c6c47-b1c2-4c80-ac97-e2932074b60f'),
  (gen_random_uuid(), 'Energy Equipment & Services', '9d6c6c47-b1c2-4c80-ac97-e2932074b60f'),
-- Utilities
  (gen_random_uuid(), 'Electric Utilities', '1cc2d021-368f-4a1b-a353-ab7b5185ee4d'),
  (gen_random_uuid(), 'Water Utilities', '1cc2d021-368f-4a1b-a353-ab7b5185ee4d'),
  (gen_random_uuid(), 'Multi-Utilities', '1cc2d021-368f-4a1b-a353-ab7b5185ee4d'),
-- Real Estate
  (gen_random_uuid(), 'Real Estate Investment Trusts (REITs)', '67250269-e719-4853-9a0f-c4c0d4df918c'),
  (gen_random_uuid(), 'Real Estate Management & Development', '67250269-e719-4853-9a0f-c4c0d4df918c'),
-- Materials
  (gen_random_uuid(), 'Chemicals', '40395229-9ea2-42b0-aa71-502fd9b7918e'),
  (gen_random_uuid(), 'Construction Materials', '40395229-9ea2-42b0-aa71-502fd9b7918e'),
  (gen_random_uuid(), 'Metals & Mining', '40395229-9ea2-42b0-aa71-502fd9b7918e');