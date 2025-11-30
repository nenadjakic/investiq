CREATE TABLE actions (
	id uuid NOT NULL,
	"date" date NOT NULL,
	description varchar(1000) NULL,
	executed bool NOT NULL,
	"name" varchar(100) NOT NULL,
	"rule" varchar(500) NOT NULL,
	asset_id uuid NOT NULL,
	CONSTRAINT pk_actions PRIMARY KEY (id),
	CONSTRAINT fk_actions_assets FOREIGN KEY (asset_id) REFERENCES assets(id)
);
CREATE TABLE action_platforms (
	action_id uuid NOT NULL,
	platform varchar(20) NOT NULL,
	CONSTRAINT ch_action_platforms_platform CHECK (((platform)::text = ANY ((ARRAY['TRADING212'::character varying, 'ETORO'::character varying, 'IBKR'::character varying, 'REVOLUT'::character varying])::text[]))),
	CONSTRAINT fk_action_platforms_actions FOREIGN KEY (action_id) REFERENCES actions(id)
);