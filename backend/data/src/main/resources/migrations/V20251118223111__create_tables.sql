CREATE TABLE currencies (
	code varchar(3) NOT NULL,
	"name" varchar(100) NOT NULL,
	symbol varchar(10) NULL,
	to_parent_factor numeric(20, 6) NULL,
	parent_code varchar(3) NULL,
	CONSTRAINT pk_currencies PRIMARY KEY (code),
	CONSTRAINT fk_currenceies_currencies FOREIGN KEY (parent_code) REFERENCES currencies(code)
);

CREATE TABLE countries (
	iso2_code varchar(2) NOT NULL,
	"name" varchar(255) NOT NULL,
	CONSTRAINT pk_countries PRIMARY KEY (iso2_code),
	CONSTRAINT uq_countries_name UNIQUE (name)
);

CREATE TABLE sectors (
	id uuid NOT NULL,
	"name" varchar(255) NOT NULL,
	CONSTRAINT pk_sectors PRIMARY KEY (id),
	CONSTRAINT uq_sectors_name UNIQUE (name)
);

CREATE TABLE industries (
	id uuid NOT NULL,
	"name" varchar(255) NOT NULL,
	sector_id uuid NOT NULL,
	CONSTRAINT pk_industries PRIMARY KEY (id),
	CONSTRAINT uq_industries_name UNIQUE (name),
	CONSTRAINT fk_industries_sectors FOREIGN KEY (sector_id) REFERENCES sectors(id)
);

CREATE TABLE exchanges (
	exchange_id uuid NOT NULL,
	acronym varchar(10) NULL,
	mic varchar(10) NOT NULL,
	"name" varchar(100) NOT NULL,
	country_iso2_code varchar(2) NOT NULL,
	CONSTRAINT pk_exchanges PRIMARY KEY (exchange_id),
	CONSTRAINT uq_exchanges_mic UNIQUE (mic),
	CONSTRAINT fk_exchanges_countries FOREIGN KEY (country_iso2_code) REFERENCES countries(iso2_code)
);

CREATE TABLE companies (
	company_id uuid NOT NULL,
	"name" varchar(100) NOT NULL,
	country_code varchar(2) NOT NULL,
	industry_id uuid NOT NULL,
	CONSTRAINT pk_companies PRIMARY KEY (company_id),
	CONSTRAINT fk_companies_countries FOREIGN KEY (country_code) REFERENCES countries(iso2_code),
	CONSTRAINT fk_companies_industries FOREIGN KEY (industry_id) REFERENCES industries(id)
);

CREATE TABLE assets (
	asset_type varchar(20) NOT NULL,
	id uuid NOT NULL,
	"name" varchar(150) NOT NULL,
	symbol varchar(20) NOT NULL,
	fund_manager varchar(255) NULL,
	currency_code varchar(3) NOT NULL,
	exchange_id uuid NOT NULL,
	company_id uuid NULL,
	CONSTRAINT ch_assets_asset_type CHECK (((asset_type)::text = ANY ((ARRAY['ETF'::character varying, 'STOCK'::character varying])::text[]))),
	CONSTRAINT pk_assets PRIMARY KEY (id),
	CONSTRAINT uq_assets_symbol_exchange_id UNIQUE (symbol, exchange_id),
	CONSTRAINT fk_assets_companies FOREIGN KEY (company_id) REFERENCES companies(company_id),
	CONSTRAINT fk_assets_currencies FOREIGN KEY (currency_code) REFERENCES currencies(code),
	CONSTRAINT fk_assets_exchanges FOREIGN KEY (exchange_id) REFERENCES exchanges(exchange_id)
);

CREATE TABLE asset_aliases (
	asset_alias_id uuid NOT NULL,
	external_symbol varchar(50) NOT NULL,
	platform varchar(20) NOT NULL,
	asset_id uuid NOT NULL,
	CONSTRAINT pk_asset_aliases PRIMARY KEY (asset_alias_id),
	CONSTRAINT ch_asset_aliases_platform CHECK (((platform)::text = ANY ((ARRAY['TRADING212'::character varying, 'ETORO'::character varying, 'IBKR'::character varying])::text[]))),
	CONSTRAINT fk_asset_aliases_assets FOREIGN KEY (asset_id) REFERENCES assets(id)
);

CREATE TABLE tags (
	id uuid NOT NULL,
	"name" varchar(50) NOT NULL,
	CONSTRAINT pk_tags PRIMARY KEY (id),
	CONSTRAINT uq_tags_name UNIQUE (name)
);

CREATE TABLE transactions (
	transaction_type varchar(20) NOT NULL,
	id uuid NOT NULL,
	transaction_date timestamptz(6) NOT NULL,
	description varchar(500) NULL,
	price numeric(20, 6) NULL,
	quantity numeric(20, 6) NULL,
	asset_id uuid NOT NULL,
	CONSTRAINT pk_transactions PRIMARY KEY (id),
	CONSTRAINT ch_transactions_transaction_type CHECK (((transaction_type)::text = 'OPEN_POSITION'::text)),
	CONSTRAINT fk_transactions_assets FOREIGN KEY (asset_id) REFERENCES assets(id)
);