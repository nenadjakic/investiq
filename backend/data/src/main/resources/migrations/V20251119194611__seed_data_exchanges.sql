INSERT INTO public.exchanges (id, "name", mic, acronym, country_iso2_code)
VALUES
  (gen_random_uuid(), 'New York Stock Exchange', 'XNYS', 'NYSE', 'US'),
  (gen_random_uuid(), 'NASDAQ Stock Market', 'XNAS', 'NASDAQ', 'US'),
  (gen_random_uuid(), 'London Stock Exchange', 'XLON', 'LSE', 'GB'),
  (gen_random_uuid(), 'Euronext Paris', 'XPAR', 'EPA', 'FR'),
  (gen_random_uuid(), 'Frankfurt Stock Exchange', 'XFRA', 'FWB', 'DE'),
  (gen_random_uuid(), 'Madrid Stock Exchange', 'XMAD', 'BME', 'ES'),
  (gen_random_uuid(), 'Borsa Italiana', 'XMIL', 'BIT', 'IT'),
  (gen_random_uuid(), 'Nasdaq Stockholm', 'XSTO', 'OMX', 'SE'),
  (gen_random_uuid(), 'Euronext Amsterdam', 'XAMS', 'AMS', 'NL'),
  (gen_random_uuid(), 'Euronext Dublin (Irish Stock Exchange)', 'XDUB', 'ISE', 'IE'),
  (gen_random_uuid(), 'Swiss Exchange', 'XSWX', 'SIX', 'CH'),
  (gen_random_uuid(), 'Wiener Börse (Vienna Stock Exchange)', 'XWBO', 'WBAG', 'AT');
