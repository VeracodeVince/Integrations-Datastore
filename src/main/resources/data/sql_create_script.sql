drop table scm_repos;
drop table scm_orgs;
drop table scms;

CREATE TABLE scms
(
	id				SERIAL PRIMARY KEY,
	name			varchar(50),
	base_url		varchar(50),
	client_id		varchar(50),
	client_secret	varchar(50)

);

CREATE TABLE scm_orgs
(
	id				SERIAL PRIMARY KEY,
	scm_id			int,
	name			varchar(50),
	cx_flow_url 	varchar(50),
  cx_flow_config 	varchar(1000),
	cx_go_token 	varchar(50),
	team			varchar(50),

	CONSTRAINT fk_scm_orgs
		FOREIGN KEY (scm_id)
			REFERENCES scms(id)
);

CREATE TABLE scm_repos
(
	id						SERIAL PRIMARY KEY,
	org_id					int,
	name					varchar(50),
	is_webhook_configured	BOOLEAN,

	CONSTRAINT fk_scm_repos
		FOREIGN KEY (org_id)
			REFERENCES scm_orgs(id)
);