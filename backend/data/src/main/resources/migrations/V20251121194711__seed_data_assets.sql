INSERT INTO companies (id, name, country_code, industry_id)
VALUES
    ('6e1fd695-45ed-40c0-a918-f5691ae8f732', 'Johnson & Johnson', 'US', '048553a5-89b4-4de5-b9af-8865c5a3fac5'),
    ('df19bf92-3041-4191-b994-c54315f34ebc', 'International Business Machines Corporation', 'US', 'a73a7e1e-5523-4751-8ace-837949841b2b'),
    ('61b9d9e8-0b43-475e-9e8b-ef095ed23f01', 'Roche Holding Ltd', 'CH','048553a5-89b4-4de5-b9af-8865c5a3fac5'),
    ('7ffd69e8-30e5-4e51-8ec8-8eb2b8c57b91', 'HSBC Holdings PLC', 'GB', '7e543bad-189b-42f0-b7dd-7e0a627db227'),
    ('c1a5f4e2-3d6b-4f5e-9c1d-2e5f6c3d7a8d', 'Banco Santander, S.A.', 'ES', '7e543bad-189b-42f0-b7dd-7e0a627db227'),
    ('fb5a1c2e-3c4d-4f5e-9c1d-2e5f6c3d7a8e', 'Airbus SE', 'US', 'a1b2c3d4-e5f6-7890-abcd-ef0123456789'),
    ('d2b2c3d4-e5f6-7890-abcd-ef0123456789', 'Siemens Aktiengesellschaft', 'DE', 'a1b2c3d4-e5f6-7890-abcd-ef0123456789'),
    ('3f4e5d6c-7b8a-9c0d-e1f2-a3b4c5d6e7f8', 'Deutsche Bank', 'DE', '1f2e3d4c-5b6a-7c8d-9e0f-1a2b3c4d5e6f'),
    ('eb4b8383-a5ee-4161-afe9-22f48a4f1521', 'SAP SE', 'DE', 'a73a7e1e-5523-4751-8ace-837949841b2b'),
    ('fdc12345-6789-4abc-def0-1234567890ab', 'Unilever PLC', 'GB', '7e543bad-189b-42f0-b7dd-7e0a627db227'),
    ('6ab6457e-c14e-4a6d-8ccf-a21b24370aea', 'Novartis AG', 'CH', '048553a5-89b4-4de5-b9af-8865c5a3fac5'),
    ('c8452227-612e-433c-8774-0b3d316a90a6', 'Allianz SE', 'DE', 'e5c5bc46-ca43-467e-9395-e54e3bfa6704'),
    ('fb2a1c2e-3c4d-4f5e-9c1d-2e5f6c3d7a8f', 'Apple Inc.', 'US', 'c4d5e6f7-8a9b-0c1d-2e3f-4a5b6c7d8e9f'),
    ('c1234567-89ab-4cde-f012-3456789abcde', 'Meta Platforms, Inc', 'US', '048553a5-89b4-4de5-b9af-8865c5a3fac5'),
    ('92b660af-fee2-4027-a7d2-35c05cbe3156', 'MARA Holdings, Inc.', 'US', '3b8e21a7-d3ff-4609-a9fe-8804a28f0c0a'),
    ('0be15e0c-9eb2-4bad-a867-c3052d0a9d1c', 'Broadcom Inc.', 'US', '75cff663-9ad5-4e30-97cd-6837b47b270f')
;

INSERT INTO assets (id, asset_type, "name", symbol, fund_manager, currency_code, exchange_id, company_id)
VALUES
    ('a2e818c7-416b-4561-b25b-af8cf2307578', 'STOCK', 'Johnson & Johnson', 'JNJ', NULL, 'USD', 'e698eb12-504d-4247-b65c-ef711e4a0003', '6e1fd695-45ed-40c0-a918-f5691ae8f732'),
    ('3e367f20-5253-42ea-8598-2280a9630704', 'STOCK', 'International Business Machines Corporation', 'IBM', NULL, 'USD', 'e698eb12-504d-4247-b65c-ef711e4a0003', '6e1fd695-45ed-40c0-a918-f5691ae8f732'),
    ('2314d8b1-0e0a-4fdc-becc-01ec3dc0cbec', 'STOCK', 'Roche Holding Ltd', 'ROG.SW', NULL, 'CHF', '6dcc99d9-8812-40f4-a439-ae0db1034388', '61b9d9e8-0b43-475e-9e8b-ef095ed23f01'),
    ('9f4c8e2d-3b6a-4f5e-9c1d-2e5f6c3d7a8c', 'STOCK', 'HSBC Holdings plc', 'HSBA.L', NULL, 'GBP', '0cafbed5-1a23-4f1f-a5db-418c60b3e482', '7ffd69e8-30e5-4e51-8ec8-8eb2b8c57b91'),
    ('80835d4f-47db-4756-a6b4-b216404c7706', 'STOCK', 'Banco Santander, S.A.', 'SAN.MC', NULL, 'EUR', 'fccfc161-8c41-40af-b340-41bf77f6c483', 'c1a5f4e2-3d6b-4f5e-9c1d-2e5f6c3d7a8d'),
    ('d4f5e6a7-b8c9-0d1e-2f3a-4b5c6d7e8f90', 'STOCK', 'Airbus SE', 'AIR.PA', NULL, 'EUR', '258de74f-06ce-480b-85c5-2834e05cd7ac', 'fb5a1c2e-3c4d-4f5e-9c1d-2e5f6c3d7a8e'),
    ('df295f45-2d9a-4bd1-98fd-a43df868f7d7', 'STOCK', 'Airbus SE', 'AIR.DE', NULL, 'EUR', 'b3c1b9fe-5a55-4e9e-9f6c-2f6f2cd13c30', 'fb5a1c2e-3c4d-4f5e-9c1d-2e5f6c3d7a8e'),
    ('c3b4d5e6-f7a8-9b0c-d1e2-f3a4b5c6d7e8', 'STOCK', 'Siemens Aktiengesellschaft', 'SIE.DE', NULL, 'EUR', 'b3c1b9fe-5a55-4e9e-9f6c-2f6f2cd13c30', 'd2b2c3d4-e5f6-7890-abcd-ef0123456789'),
    ('111c1ded-f1b2-41a9-aecc-5dd25dc4ad31', 'STOCK', 'Deutsche Bank', 'DBK.DE', NULL, 'EUR', 'b3c1b9fe-5a55-4e9e-9f6c-2f6f2cd13c30', '3f4e5d6c-7b8a-9c0d-e1f2-a3b4c5d6e7f8'),
    ('9f1e2d3c-4b5a-6c7d-8e9f-0a1b2c3d4e5f', 'STOCK', 'Deutsche Bank', 'DB', NULL, 'USD', 'e698eb12-504d-4247-b65c-ef711e4a0003', '3f4e5d6c-7b8a-9c0d-e1f2-a3b4c5d6e7f8'),
    ('91966245-4129-470e-b075-680f0623b476', 'STOCK', 'SAP SE', 'SAP.DE', NULL, 'EUR', 'b3c1b9fe-5a55-4e9e-9f6c-2f6f2cd13c30', 'eb4b8383-a5ee-4161-afe9-22f48a4f1521'),
    ('94fb39b3-0464-4244-ba39-b0e343d78318', 'STOCK', 'Unilever PLC', 'ULVR.L', NULL, 'GBP', '0cafbed5-1a23-4f1f-a5db-418c60b3e482', 'fdc12345-6789-4abc-def0-1234567890ab'),
    ('a3b2c1d4-e5f6-7890-abcd-ef0123456789', 'STOCK', 'Novartis AG', 'NOVN.SW', NULL, 'CHF', '6dcc99d9-8812-40f4-a439-ae0db1034388', '6ab6457e-c14e-4a6d-8ccf-a21b24370aea'),
    ('36a4699c-c185-4399-912d-35a17b8f22f6', 'STOCK', 'Allianz SE', 'ALV.DE', NULL, 'EUR', 'b3c1b9fe-5a55-4e9e-9f6c-2f6f2cd13c30', 'c8452227-612e-433c-8774-0b3d316a90a6'),
    ('ca3717a7-df4e-4474-994f-6d5a8bd32bb4', 'STOCK', 'Apple Inc.', 'AAPL', NULL, 'USD', '2bcbd047-9de4-4220-a385-d431544a942a', 'fb2a1c2e-3c4d-4f5e-9c1d-2e5f6c3d7a8f'),
    ('9f4c8e2d-3b6a-4f5e-9c1d-2e5f6c3d7a8d', 'STOCK', 'Meta Platforms, Inc.', 'META', NULL, 'USD', '2bcbd047-9de4-4220-a385-d431544a942a', 'c1234567-89ab-4cde-f012-3456789abcde'),
    ('1c46d982-868f-4565-a8a1-4e2f29f54d8b', 'STOCK', 'MARA Holdings, Inc.', 'MARA', NULL, 'USD', '2bcbd047-9de4-4220-a385-d431544a942a', '92b660af-fee2-4027-a7d2-35c05cbe3156'),
    ('0ac84421-d8a5-4412-a2fd-1aafc90bb044', 'STOCK', 'Broadcom Inc.', 'AVGO', NULL, 'USD', '2bcbd047-9de4-4220-a385-d431544a942a', '0be15e0c-9eb2-4bad-a867-c3052d0a9d1c')
;

INSERT INTO asset_aliases (id, asset_id, platform, external_symbol)
VALUES
    ('0643eeb3-061f-4a1b-91c2-7015e53def28', 'a2e818c7-416b-4561-b25b-af8cf2307578', 'TRADING212', 'JNJ'),
    ('4c0f8228-78ea-4f18-8f8b-5b3e1f83fc63', '3e367f20-5253-42ea-8598-2280a9630704', 'TRADING212', 'IBM'),
    ('5f5e1c3a-9d4b-4e2e-8f4b-2e5f6c3d7a8b', '2314d8b1-0e0a-4fdc-becc-01ec3dc0cbec', 'TRADING212', 'ROG'),
    ('80a0ae28-d38e-4dd6-b452-f97e98676314', '9f4c8e2d-3b6a-4f5e-9c1d-2e5f6c3d7a8c', 'TRADING212', 'HSBA'),
    ('57a1943e-91c3-44a8-a36b-bc644e23253d', '80835d4f-47db-4756-a6b4-b216404c7706', 'TRADING212', 'SAN'),
    ('1b2c3d4e-5f6a-7b8c-9d0e-1f2a3b4c5d6e', 'd4f5e6a7-b8c9-0d1e-2f3a-4b5c6d7e8f90', 'TRADING212', 'AIR'),
    ('42ea2c64-9ee0-4731-afee-941c28547f22', 'c3b4d5e6-f7a8-9b0c-d1e2-f3a4b5c6d7e8', 'TRADING212', 'SIE'),
    ('5e292511-6ed4-4ed8-9624-5a8701c46f88', '111c1ded-f1b2-41a9-aecc-5dd25dc4ad31', 'TRADING212', 'DBK'),
    ('d1f2e3d4-c5b6-a7b8-9c0d-e1f2a3b4c5d6', '91966245-4129-470e-b075-680f0623b476', 'TRADING212', 'SAP'),
    ('e5f6a7b8-c9d0-e1f2-a3b4-c5d6e7f8091a', '94fb39b3-0464-4244-ba39-b0e343d78318', 'TRADING212', 'ULVR'),
    ('f1b2c3d4-e5f6-7890-abcd-ef0123456789', 'a3b2c1d4-e5f6-7890-abcd-ef0123456789', 'TRADING212', 'NOVN'),
    ('ab12cd34-ef56-7890-abcd-ef0123456789', '36a4699c-c185-4399-912d-35a17b8f22f6', 'TRADING212', 'ALV'),
    ('bc23de45-f678-90ab-cdef-0123456789ab', 'ca3717a7-df4e-4474-994f-6d5a8bd32bb4', 'TRADING212', 'AAPL'),
    ('cd34ef56-7890-abcd-ef01-23456789abcd', '9f4c8e2d-3b6a-4f5e-9c1d-2e5f6c3d7a8d', 'TRADING212', 'META'),
    ('ed21382f-2fb8-4f17-935d-7d1b21e9baf9', '1c46d982-868f-4565-a8a1-4e2f29f54d8b', 'TRADING212', 'MARA'),
    ('b201ddb0-0598-4631-8ba0-aed6d4693fbf', '0ac84421-d8a5-4412-a2fd-1aafc90bb044', 'TRADING212', 'AVGO'),


    ('dca31bb9-743c-4345-83ea-416991b757ea', '3e367f20-5253-42ea-8598-2280a9630704', 'ETORO', 'IBM'),
    ('4fe5a662-9d2b-4bcf-b07f-72c45b8f504b', '2314d8b1-0e0a-4fdc-becc-01ec3dc0cbec', 'ETORO', 'ROG.ZU'),
    ('21993b85-8cb4-4308-8cf9-0ccd70169da3', 'd4f5e6a7-b8c9-0d1e-2f3a-4b5c6d7e8f90', 'ETORO', 'AIR'),
    ('b9d2d4cb-f73c-46d1-9048-8b35328b3beb', 'c3b4d5e6-f7a8-9b0c-d1e2-f3a4b5c6d7e8', 'ETORO', 'SIE.DE'),
    ('91966245-4129-470e-b075-680f0623b476', '9f1e2d3c-4b5a-6c7d-8e9f-0a1b2c3d4e5f', 'ETORO', 'DB'),
    ('c859a4cc-c922-4c18-b9bb-cdaaded486da', '91966245-4129-470e-b075-680f0623b476', 'ETORO', 'SAP.DE'),
    ('4861b90a-0027-4d65-99a4-73700c25a1b2', '9f4c8e2d-3b6a-4f5e-9c1d-2e5f6c3d7a8d', 'ETORO', 'META'),

    ('3662a0a0-66c1-407f-93d7-92fea293aece', 'ca3717a7-df4e-4474-994f-6d5a8bd32bb4', 'IBKR', 'AAPL'),
    ('6e08e85f-7b74-4dd3-bf73-afd80c270462', '0ac84421-d8a5-4412-a2fd-1aafc90bb044', 'IBKR', 'AVGO')

;