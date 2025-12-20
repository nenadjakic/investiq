-- Add asset_class column to assets table
ALTER TABLE assets ADD COLUMN asset_class varchar(50) NULL;

-- Add tracked_index_id column to assets table (for ETFs that track an index)
ALTER TABLE assets ADD COLUMN tracked_index_id uuid NULL;
ALTER TABLE assets ADD CONSTRAINT fk_assets_tracked_index FOREIGN KEY (tracked_index_id) REFERENCES assets(id);

-- Create etf_sector_allocations table
CREATE TABLE etf_sector_allocations (
	id uuid NOT NULL,
	etf_id uuid NOT NULL,
	sector_id uuid NOT NULL,
	weight_percentage numeric(5, 2) NOT NULL,
	CONSTRAINT pk_etf_sector_allocations PRIMARY KEY (id),
	CONSTRAINT ch_etf_sector_allocations_weight CHECK (weight_percentage >= 0 AND weight_percentage <= 100),
	CONSTRAINT fk_etf_sector_allocations_assets FOREIGN KEY (etf_id) REFERENCES assets(id),
	CONSTRAINT fk_etf_sector_allocations_sectors FOREIGN KEY (sector_id) REFERENCES sectors(id),
	CONSTRAINT uq_etf_sector_allocations UNIQUE (etf_id, sector_id)
);

-- Create etf_country_allocations table
CREATE TABLE etf_country_allocations (
	id uuid NOT NULL,
	etf_id uuid NOT NULL,
	country_code varchar(2) NOT NULL,
	weight_percentage numeric(5, 2) NOT NULL,
	CONSTRAINT pk_etf_country_allocations PRIMARY KEY (id),
	CONSTRAINT ch_etf_country_allocations_weight CHECK (weight_percentage >= 0 AND weight_percentage <= 100),
	CONSTRAINT fk_etf_country_allocations_assets FOREIGN KEY (etf_id) REFERENCES assets(id),
	CONSTRAINT fk_etf_country_allocations_countries FOREIGN KEY (country_code) REFERENCES countries(iso2_code),
	CONSTRAINT uq_etf_country_allocations UNIQUE (etf_id, country_code)
);

-- Create index_sector_allocations table
CREATE TABLE index_sector_allocations (
	id uuid NOT NULL,
	index_id uuid NOT NULL,
	sector_id uuid NOT NULL,
	weight_percentage numeric(5, 2) NOT NULL,
	CONSTRAINT pk_index_sector_allocations PRIMARY KEY (id),
	CONSTRAINT ch_index_sector_allocations_weight CHECK (weight_percentage >= 0 AND weight_percentage <= 100),
	CONSTRAINT fk_index_sector_allocations_assets FOREIGN KEY (index_id) REFERENCES assets(id),
	CONSTRAINT fk_index_sector_allocations_sectors FOREIGN KEY (sector_id) REFERENCES sectors(id),
	CONSTRAINT uq_index_sector_allocations UNIQUE (index_id, sector_id)
);

-- Create index_country_allocations table
CREATE TABLE index_country_allocations (
	id uuid NOT NULL,
	index_id uuid NOT NULL,
	country_code varchar(2) NOT NULL,
	weight_percentage numeric(5, 2) NOT NULL,
	CONSTRAINT pk_index_country_allocations PRIMARY KEY (id),
	CONSTRAINT ch_index_country_allocations_weight CHECK (weight_percentage >= 0 AND weight_percentage <= 100),
	CONSTRAINT fk_index_country_allocations_assets FOREIGN KEY (index_id) REFERENCES assets(id),
	CONSTRAINT fk_index_country_allocations_countries FOREIGN KEY (country_code) REFERENCES countries(iso2_code),
	CONSTRAINT uq_index_country_allocations UNIQUE (index_id, country_code)
);
