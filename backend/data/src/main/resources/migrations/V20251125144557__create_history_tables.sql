CREATE TABLE currency_histories (
    id uuid NOT NULL,
    currency_code varchar(3) NOT NULL,
    valid_from timestamptz NOT NULL,
    valid_to timestamptz NULL,
    exchange_rate numeric(20, 6) NOT NULL,
    base_currency_code varchar(3) NOT NULL,
    CONSTRAINT pk_currency_histories PRIMARY KEY (id),
    CONSTRAINT fk_currency_histories_currencies_currency FOREIGN KEY (currency_code) REFERENCES currencies(code),
    CONSTRAINT fk_currency_histories_currencies_base_currency FOREIGN KEY (base_currency_code) REFERENCES currencies(code),
    CONSTRAINT ch_currency_histories_valid_dates CHECK (valid_to IS NULL OR valid_to > valid_from)
);

CREATE TABLE asset_histories (
    id uuid NOT NULL,
    asset_id uuid NOT NULL,
    valid_from timestamptz NOT NULL,
    valid_to timestamptz NULL,
    volume bigint NULL,
    open_price numeric(20, 6) NULL,
    high_price numeric(20, 6) NULL,
    low_price numeric(20, 6) NULL,
    close_price numeric(20, 6) NOT NULL,
    adjusted_close numeric(20, 6) NULL,
    CONSTRAINT pk_asset_histories PRIMARY KEY (id),
    CONSTRAINT fk_asset_histories_assets FOREIGN KEY (asset_id) REFERENCES assets(id),
    CONSTRAINT ch_asset_histories_valid_dates CHECK (valid_to IS NULL OR valid_to > valid_from),
    CONSTRAINT ch_asset_histories_positive_prices CHECK (
        (open_price IS NULL OR open_price > 0) AND
        (high_price IS NULL OR high_price > 0) AND
        (low_price IS NULL OR low_price > 0) AND
        (close_price IS NULL OR close_price > 0) AND
        (adjusted_close IS NULL OR adjusted_close > 0)
    ),
    CONSTRAINT ch_asset_histories_high_low CHECK (high_price IS NULL OR low_price IS NULL OR high_price >= low_price)
);