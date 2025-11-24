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
	id uuid NOT NULL,
	acronym varchar(10) NULL,
	mic varchar(10) NOT NULL,
	"name" varchar(100) NOT NULL,
	country_iso2_code varchar(2) NOT NULL,
	CONSTRAINT pk_exchanges PRIMARY KEY (id),
	CONSTRAINT uq_exchanges_mic UNIQUE (mic),
	CONSTRAINT fk_exchanges_countries FOREIGN KEY (country_iso2_code) REFERENCES countries(iso2_code)
);

CREATE TABLE companies (
	id uuid NOT NULL,
	"name" varchar(100) NOT NULL,
	country_code varchar(2) NOT NULL,
	industry_id uuid NOT NULL,
	CONSTRAINT pk_companies PRIMARY KEY (id),
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
	CONSTRAINT fk_assets_companies FOREIGN KEY (company_id) REFERENCES companies(id),
	CONSTRAINT fk_assets_currencies FOREIGN KEY (currency_code) REFERENCES currencies(code),
	CONSTRAINT fk_assets_exchanges FOREIGN KEY (exchange_id) REFERENCES exchanges(id)
);

CREATE TABLE asset_aliases (
	id uuid NOT NULL,
	external_symbol varchar(50) NOT NULL,
	platform varchar(20) NOT NULL,
	asset_id uuid NOT NULL,
	CONSTRAINT pk_asset_aliases PRIMARY KEY (id),
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
    external_id varchar(100) NULL,
    price numeric(20, 8) NULL,
    quantity numeric(20, 12) NULL,
    amount numeric(20, 8) NULL,
    gross_amount numeric(20, 8) NULL,
    tax_amount numeric(20, 8) NULL,
    tax_percentage numeric(10, 4) NULL,
    price_amount numeric(20, 8) NULL,
    asset_id uuid NOT NULL,
    related_transaction_id uuid NULL,
    currency_code varchar(3) NOT NULL,
	CONSTRAINT pk_transactions PRIMARY KEY (id),
	CONSTRAINT ch_transactions_transaction_type CHECK (((transaction_type)::text =  ANY ((ARRAY['BUY'::character varying, 'FEE'::character varying, 'SELL'::character varying, 'DEPOSIT'::character varying, 'WITHDRAWAL'::character varying, 'DIVIDEND'::character varying, 'DIVIDEND_ADJUSTMENT'::character varying, 'UNKNOWN'::character varying])::text[]))),
	CONSTRAINT fk_transactions_assets FOREIGN KEY (asset_id) REFERENCES assets(id),
	CONSTRAINT fk_transactions_transactions FOREIGN KEY (related_transaction_id) REFERENCES transactions(id),
	CONSTRAINT fk_transactions_currencies FOREIGN KEY (currency_code) REFERENCES currencies(code),
);

CREATE TABLE staging_transactions (
	id uuid NOT NULL,
    description varchar(255) NULL,
    external_id varchar(100) NULL,
    external_symbol varchar(255) NULL,
    gross_amount float8 NULL,
    import_status varchar(255) NOT NULL,
    notes varchar(255) NULL,
    price float8 NULL,
    amount float8 NULL,
    quantity float8 NULL,
    resolution_note varchar(255) NULL,
    tax_amount float8 NULL,
    tax_percentage float8 NULL,
    transaction_date timestamptz(6) NOT NULL,
    transaction_type varchar(255) NOT NULL,
    currency_code varchar(3) NULL,
    related_staging_transaction_id uuid NULL,
    resolved_asset_id uuid NULL,
	CONSTRAINT ch_staging_transactions_import_status CHECK (((import_status)::text = ANY ((ARRAY['PENDING'::character varying, 'VALIDATED'::character varying, 'FAILED'::character varying, 'IMPORTED'::character varying])::text[]))),
	CONSTRAINT pk_staging_transactions PRIMARY KEY (id),
	CONSTRAINT ch_staging_transactions_transaction_type CHECK (((transaction_type)::text = ANY ((ARRAY['BUY'::character varying, 'FEE'::character varying, 'SELL'::character varying, 'DEPOSIT'::character varying, 'WITHDRAWAL'::character varying, 'DIVIDEND'::character varying, 'DIVIDEND_ADJUSTMENT'::character varying, 'UNKNOWN'::character varying])::text[]))),
	CONSTRAINT fk_staging_transactions_assets FOREIGN KEY (resolved_asset_id) REFERENCES assets(id),
	CONSTRAINT fk_staging_transactions_currencies FOREIGN KEY (currency_code) REFERENCES currencies(code),
    CONSTRAINT fk_staging_transactions_staging_transactions FOREIGN KEY (related_staging_transaction_id) REFERENCES staging_transactions(id)
);

CREATE TABLE staging_transaction_tags (
	staging_transaction_id uuid NOT NULL,
	tag_id uuid NOT NULL,
	CONSTRAINT pk_staging_transaction_tags PRIMARY KEY (staging_transaction_id, tag_id),
	CONSTRAINT fk_staging_transaction_tags_staging_transactions FOREIGN KEY (staging_transaction_id) REFERENCES staging_transactions(id),
	CONSTRAINT fk_staging_transaction_tags_tags FOREIGN KEY (tag_id) REFERENCES tags(id)
);