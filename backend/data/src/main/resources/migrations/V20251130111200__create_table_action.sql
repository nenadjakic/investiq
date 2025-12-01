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