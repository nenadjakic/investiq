INSERT INTO tags (id, "name")
VALUES
  (gen_random_uuid(), 'Trading212'),
  (gen_random_uuid(), 'IBKR'),
  (gen_random_uuid(), 'eToro'),
  (gen_random_uuid(), 'Revolut'),
  (gen_random_uuid(), 'Stamp duty reserve tax'),
  (gen_random_uuid(), 'Conversation fee'),
  (gen_random_uuid(), 'French transaction tax');

INSERT INTO sectors (id, "name")
VALUES
  ('e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e', 'Technology'),
  ('75536f22-aa7f-4bb3-95c9-c7ae66295a13', 'Healthcare'),
  ('a45f3a5a-0319-4dcb-86e1-9478b16adfa4', 'Financial Services'),
  ('f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 'Industrials')
 ;

INSERT INTO public.industries (id, "name", sector_id)
VALUES
-- Technology
  ('a73a7e1e-5523-4751-8ace-837949841b2b', 'Information Technology Services', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e'),
  ('bbf4c8e1-3d2a-4f5e-9c1d-2e5f6c3d7a8a', 'Software - Application', 'e0dd8ea8-25ea-46b1-b280-fadf7e0e4d2e'),

-- Healthcare
  ('048553a5-89b4-4de5-b9af-8865c5a3fac5', 'Drug Manufacturers - General', '75536f22-aa7f-4bb3-95c9-c7ae66295a13'),

-- Financials Services
  ('7e543bad-189b-42f0-b7dd-7e0a627db227', 'Banks - Diversified', 'a45f3a5a-0319-4dcb-86e1-9478b16adfa4'),
  ('1f2e3d4c-5b6a-7c8d-9e0f-1a2b3c4d5e6f', 'Banks - Regional', 'a45f3a5a-0319-4dcb-86e1-9478b16adfa4'),

-- Industrials
  ('a1b2c3d4-e5f6-7890-abcd-ef0123456789', 'Aerospace & Defense', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f'),
  ('2a042a16-c2e5-4d53-95fb-2d09362207b8', 'Industrial - Machinery', 'f1c2d3e4-5b6a-7c8d-9e0f-1a2b3c4d5e6f')
 ;