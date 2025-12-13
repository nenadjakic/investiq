-- public.vw_transaction_analytics source

CREATE OR REPLACE VIEW public.vw_transaction_analytics
AS SELECT t.id AS transaction_id,
    t.platform AS transaction_platform,
    t.transaction_type,
    t.transaction_date,
    date(t.transaction_date) AS transaction_date_only,
    EXTRACT(year FROM t.transaction_date) AS year,
    EXTRACT(quarter FROM t.transaction_date) AS quarter,
    EXTRACT(month FROM t.transaction_date) AS month,
    EXTRACT(week FROM t.transaction_date) AS week,
    t.quantity,
    t.price,
    t.amount,
    t.gross_amount,
    t.tax_amount,
    t.tax_percentage,
    t.currency_code AS currency,
    a.id AS asset_id,
    a.name AS asset_name,
    a.symbol AS asset_symbol,
    a.asset_type,
    a.fund_manager,
    c.id AS company_id,
    c.name AS company_name,
    co.iso2_code AS company_country_code,
    co.name AS company_country_name,
    i.id AS industry_id,
    i.name AS industry_name,
    s.id AS sector_id,
    s.name AS sector_name,
    e.id AS exchange_id,
    e.name AS exchange_name,
    e.acronym AS exchange_acronym,
    e.mic AS exchange_mic,
    co_ex.iso2_code AS exchange_country_code,
    co_ex.name AS exchange_country_name,
    COALESCE(fee.fee_amount_eur, 0::numeric) AS fee_amount_eur,
        CASE
            WHEN t.transaction_type::text = 'BUY'::text THEN
            CASE
                WHEN t.currency_code::text = 'GBX'::text THEN t.price / 100.0
                ELSE t.price
            END * COALESCE(t.quantity, 0::numeric)
            WHEN t.transaction_type::text = 'SELL'::text THEN '-1'::integer::numeric *
            CASE
                WHEN t.currency_code::text = 'GBX'::text THEN t.price / 100.0
                ELSE t.price
            END * COALESCE(t.quantity, 0::numeric)
            WHEN t.transaction_type::text = 'DIVIDEND'::text THEN
            CASE
                WHEN t.currency_code::text = 'GBX'::text THEN t.amount / 100.0
                ELSE t.amount
            END
            WHEN t.transaction_type::text = 'DIVIDEND_ADJUSTMENT'::text THEN '-1'::integer::numeric *
            CASE
                WHEN t.currency_code::text = 'GBX'::text THEN t.amount / 100.0
                ELSE t.amount
            END
            WHEN t.transaction_type::text = 'DEPOSIT'::text THEN
            CASE
                WHEN t.currency_code::text = 'GBX'::text THEN t.amount / 100.0
                ELSE t.amount
            END
            WHEN t.transaction_type::text = 'WITHDRAWAL'::text THEN '-1'::integer::numeric *
            CASE
                WHEN t.currency_code::text = 'GBX'::text THEN t.amount / 100.0
                ELSE t.amount
            END
            ELSE
            CASE
                WHEN t.currency_code::text = 'GBX'::text THEN t.amount / 100.0
                ELSE t.amount
            END
        END *
        CASE
            WHEN
            CASE
                WHEN t.currency_code::text = 'GBX'::text THEN 'GBP'::character varying
                ELSE t.currency_code
            END::text = 'EUR'::text THEN 1.0
            ELSE COALESCE(ch1.exchange_rate,
            CASE
                WHEN ch2.exchange_rate IS NOT NULL THEN 1.0 / ch2.exchange_rate
                ELSE NULL::numeric
            END)
        END AS transaction_value_eur,
        CASE
            WHEN t.transaction_type::text = 'BUY'::text THEN
            CASE
                WHEN t.currency_code::text = 'GBX'::text THEN ah.close_price / 100::numeric
                ELSE ah.close_price
            END * COALESCE(t.quantity, 0::numeric)
            WHEN t.transaction_type::text = 'SELL'::text THEN '-1'::integer::numeric *
            CASE
                WHEN t.currency_code::text = 'GBX'::text THEN ah.close_price / 100::numeric
                ELSE ah.close_price
            END * COALESCE(t.quantity, 0::numeric)
            WHEN t.transaction_type::text = 'DIVIDEND'::text THEN
            CASE
                WHEN t.currency_code::text = 'GBX'::text THEN t.amount / 100::numeric
                ELSE t.amount
            END
            WHEN t.transaction_type::text = 'DIVIDEND_ADJUSTMENT'::text THEN '-1'::integer::numeric *
            CASE
                WHEN t.currency_code::text = 'GBX'::text THEN t.amount / 100::numeric
                ELSE t.amount
            END
            WHEN t.transaction_type::text = 'DEPOSIT'::text THEN
            CASE
                WHEN t.currency_code::text = 'GBX'::text THEN t.amount / 100::numeric
                ELSE t.amount
            END
            WHEN t.transaction_type::text = 'WITHDRAWAL'::text THEN '-1'::integer::numeric *
            CASE
                WHEN t.currency_code::text = 'GBX'::text THEN t.amount / 100::numeric
                ELSE t.amount
            END
            ELSE
            CASE
                WHEN t.currency_code::text = 'GBX'::text THEN t.amount / 100::numeric
                ELSE t.amount
            END
        END *
        CASE
            WHEN
            CASE
                WHEN t.currency_code::text = 'GBX'::text THEN 'GBP'::character varying
                ELSE t.currency_code
            END::text = 'EUR'::text THEN 1.0
            ELSE COALESCE(fx_from_eur.exchange_rate,
            CASE
                WHEN fx_to_eur.exchange_rate IS NOT NULL THEN 1.0 / fx_to_eur.exchange_rate
                ELSE NULL::numeric
            END)
        END AS market_value_eur
   FROM transactions t
     LEFT JOIN assets a ON t.asset_id = a.id
     LEFT JOIN currencies curr ON t.currency_code::text = curr.code::text
     LEFT JOIN currencies curr_asset ON a.currency_code::text = curr_asset.code::text
     LEFT JOIN companies c ON a.company_id = c.id
     LEFT JOIN countries co ON c.country_code::text = co.iso2_code::text
     LEFT JOIN industries i ON c.industry_id = i.id
     LEFT JOIN sectors s ON i.sector_id = s.id
     LEFT JOIN exchanges e ON a.exchange_id = e.id
     LEFT JOIN countries co_ex ON e.country_iso2_code::text = co_ex.iso2_code::text
     LEFT JOIN LATERAL ( SELECT sum(
                CASE
                    WHEN f.currency_code::text = 'GBX'::text THEN f.amount / 100.0
                    ELSE f.amount
                END *
                CASE
                    WHEN f.currency_code::text = 'EUR'::text THEN 1.0
                    ELSE COALESCE(ch1_1.exchange_rate,
                    CASE
                        WHEN ch2_1.exchange_rate IS NOT NULL THEN 1.0 / ch2_1.exchange_rate
                        ELSE NULL::numeric
                    END)
                END) AS fee_amount_eur
           FROM transactions f
             LEFT JOIN LATERAL ( SELECT ch.exchange_rate
                   FROM currency_histories ch
                  WHERE ch.from_currency_code::text =
                        CASE
                            WHEN f.currency_code::text = 'GBX'::text THEN 'GBP'::character varying
                            ELSE f.currency_code
                        END::text AND ch.to_currency_code::text = 'EUR'::text AND ch.valid_date <= f.transaction_date::date
                  ORDER BY ch.valid_date DESC
                 LIMIT 1) ch1_1 ON true
             LEFT JOIN LATERAL ( SELECT ch.exchange_rate
                   FROM currency_histories ch
                  WHERE ch.from_currency_code::text = 'EUR'::text AND ch.to_currency_code::text =
                        CASE
                            WHEN f.currency_code::text = 'GBX'::text THEN 'GBP'::character varying
                            ELSE f.currency_code
                        END::text AND ch.valid_date <= f.transaction_date::date
                  ORDER BY ch.valid_date DESC
                 LIMIT 1) ch2_1 ON true
          WHERE f.transaction_type::text = 'FEE'::text AND f.related_transaction_id = t.id) fee ON true
     LEFT JOIN LATERAL ( SELECT ch.exchange_rate
           FROM currency_histories ch
          WHERE ch.from_currency_code::text =
                CASE
                    WHEN t.currency_code::text = 'GBX'::text THEN 'GBP'::character varying
                    ELSE t.currency_code
                END::text AND ch.to_currency_code::text = 'EUR'::text AND ch.valid_date <= t.transaction_date::date
          ORDER BY ch.valid_date DESC
         LIMIT 1) ch1 ON
        CASE
            WHEN t.currency_code::text = 'GBX'::text THEN 'GBP'::character varying
            ELSE t.currency_code
        END::text <> 'EUR'::text
     LEFT JOIN LATERAL ( SELECT ch.exchange_rate
           FROM currency_histories ch
          WHERE ch.from_currency_code::text = 'EUR'::text AND ch.to_currency_code::text =
                CASE
                    WHEN t.currency_code::text = 'GBX'::text THEN 'GBP'::character varying
                    ELSE t.currency_code
                END::text AND ch.valid_date <= t.transaction_date::date
          ORDER BY ch.valid_date DESC
         LIMIT 1) ch2 ON
        CASE
            WHEN t.currency_code::text = 'GBX'::text THEN 'GBP'::character varying
            ELSE t.currency_code
        END::text <> 'EUR'::text
     LEFT JOIN LATERAL ( SELECT ah_1.close_price
           FROM asset_histories ah_1
          WHERE ah_1.asset_id = a.id AND ah_1.valid_date <= CURRENT_DATE
          ORDER BY ah_1.valid_date DESC
         LIMIT 1) ah ON true
     LEFT JOIN LATERAL ( SELECT ch.exchange_rate
           FROM currency_histories ch
          WHERE ch.from_currency_code::text =
                CASE
                    WHEN t.currency_code::text = 'GBX'::text THEN 'GBP'::character varying
                    ELSE t.currency_code
                END::text AND ch.to_currency_code::text = 'EUR'::text AND ch.valid_date <= t.transaction_date::date
          ORDER BY ch.valid_date DESC
         LIMIT 1) fx_from_eur ON
        CASE
            WHEN t.currency_code::text = 'GBX'::text THEN 'GBP'::character varying
            ELSE t.currency_code
        END::text <> 'EUR'::text
     LEFT JOIN LATERAL ( SELECT ch.exchange_rate
           FROM currency_histories ch
          WHERE ch.from_currency_code::text = 'EUR'::text AND ch.to_currency_code::text =
                CASE
                    WHEN t.currency_code::text = 'GBX'::text THEN 'GBP'::character varying
                    ELSE t.currency_code
                END::text AND ch.valid_date <= t.transaction_date::date
          ORDER BY ch.valid_date DESC
         LIMIT 1) fx_to_eur ON
        CASE
            WHEN t.currency_code::text = 'GBX'::text THEN 'GBP'::character varying
            ELSE t.currency_code
        END::text <> 'EUR'::text;