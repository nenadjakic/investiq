-- Migration: normalize continents into their own table and link countries via continent_id (UUID PK)

-- Ensure uuid functions are available via extensions (created earlier migrations)
-- 1) Create continents table and seed continent rows with stable UUID IDs
CREATE TABLE continents (
  id uuid NOT NULL,
  "name" varchar(30) NOT NULL,
  CONSTRAINT pk_continents PRIMARY KEY (id),
  CONSTRAINT uq_continents_name UNIQUE (name)
);

-- Use fixed UUIDs so seeded rows are stable and updates can reference them
INSERT INTO continents (id, name) VALUES
  ('b90df7e1-0e2e-4ad9-b1e5-a7148a58dc63','Africa'),
  ('b0536170-f569-4613-ab06-58e9adff2c62','Antarctica'),
  ('23976cf6-7cbf-41ea-b93c-b88250c17e27','Asia'),
  ('019fc569-80c6-4406-987c-16f051c0139a','Europe'),
  ('f190ce29-d5d7-4b6e-80c3-564490d73d57','North America'),
  ('d0e64035-cc2e-452d-afad-a781ee88a3f4','Oceania'),
  ('1594893d-ea31-41d2-99a6-6c8b2078ce75','South America')
;

-- 2) Add nullable continent_id (UUID) to countries so we can populate existing rows
ALTER TABLE countries ADD COLUMN continent_id UUID;

-- 3) Populate continent_id for existing countries based on iso2 codes
-- Africa
UPDATE countries SET continent_id = 'b90df7e1-0e2e-4ad9-b1e5-a7148a58dc63' WHERE iso2_code IN (
  'DZ','AO','BJ','BW','BF','BI','CM','CV','CF','TD','KM','CG','CD','CI','DJ','GQ','ER','ET','GA','GM','GH','GN','GW','KE','LS','LR','LY','MG','MW','ML','MR','MU','MA','MZ','NA','NE','NG','RW','RE','SC','SL','SO','ZA','SS','SD','SZ','TG','TN','UG','EH','ZM','ZW','YT','ST','IO','TZ','SN','SH','EG'
);

-- Antarctica and subantarctic territories
UPDATE countries SET continent_id = 'b0536170-f569-4613-ab06-58e9adff2c62' WHERE iso2_code IN ('AQ','BV','HM','TF','GS');

-- Asia
UPDATE countries SET continent_id = '23976cf6-7cbf-41ea-b93c-b88250c17e27' WHERE iso2_code IN (
  'AF','AM','AZ','BH','BD','BT','BN','KH','CN','HK','IN','ID','IR','IQ','IL','JP','JO','KZ','KP','KR','KW','KG','LA','LB','MY','MV','MM','NP','OM','PK','PS','PH','QA','SA','SG','SY','TW','TJ','TM','TR','VN','YE','AE','GE','MN','UZ','TH','TL','MO','LK'
);

-- Europe
UPDATE countries SET continent_id = '019fc569-80c6-4406-987c-16f051c0139a' WHERE iso2_code IN (
  'AL','AD','AT','BY','BE','BG','HR','CY','CZ','DK','EE','FI','FR','DE','GI','GR','GG','IE','IM','IT','JE','LV','LI','LT','LU','MK','MT','MD','MC','ME','NL','NO','PL','PT','RO','RS','SK','SI','ES','SE','CH','GB','AX','IS','VA','SM','RU','UA','SJ','BA','FO','HU'
);

-- North America (including Caribbean & Central America)
UPDATE countries SET continent_id = 'f190ce29-d5d7-4b6e-80c3-564490d73d57' WHERE iso2_code IN (
  'US','CA','MX','BS','BB','BZ','BM','KY','AG','AW','CU','CW','DM','DO','GD','GP','GT','HT','HN','JM','KN','LC','MF','MQ','NI','PA','PR','BL','PM','VC','TT','TC','VG','VI','UM','SV','SX','AI','BQ','CR','MS','GL'
);

-- South America
UPDATE countries SET continent_id = '1594893d-ea31-41d2-99a6-6c8b2078ce75' WHERE iso2_code IN (
  'AR','BO','BR','CL','CO','EC','GF','GY','PY','PE','SR','UY','VE','FK'
);

-- Oceania
UPDATE countries SET continent_id = 'd0e64035-cc2e-452d-afad-a781ee88a3f4' WHERE iso2_code IN (
  'AU','NZ','FJ','KI','MH','FM','NR','PW','PG','SB','TO','TV','VU','WF','WS','AS','GU','NC','PF','CK','PN','TK','NF','MP','CX','CC','NU'
);

-- 4) Enforce NOT NULL and add foreign key
ALTER TABLE countries ALTER COLUMN continent_id SET NOT NULL;

ALTER TABLE countries ADD CONSTRAINT fk_countries_continent
  FOREIGN KEY (continent_id) REFERENCES continents (id);
