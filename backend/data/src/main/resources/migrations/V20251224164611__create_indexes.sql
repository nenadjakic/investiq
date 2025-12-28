CREATE INDEX IF NOT EXISTS  idx_asset_histories_asset_date
    ON asset_histories (asset_id, valid_date DESC);

CREATE INDEX IF NOT EXISTS idx_currency_histories_from_to_date
    ON currency_histories (from_currency_code, to_currency_code, valid_date DESC);