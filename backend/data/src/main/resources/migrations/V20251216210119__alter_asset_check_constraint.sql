ALTER TABLE assets DROP CONSTRAINT IF EXISTS ch_assets_asset_type;
ALTER TABLE assets ADD CONSTRAINT ch_assets_asset_type 
    CHECK (((asset_type)::text = ANY ((ARRAY['ETF'::character varying, 'STOCK'::character varying, 'INDEX'::character varying])::text[])));
