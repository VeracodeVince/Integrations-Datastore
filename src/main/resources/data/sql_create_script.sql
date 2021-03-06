drop table scm_tokens;
drop table scm_repos;
drop table scm_orgs;
drop table scms;

CREATE TABLE scms
(
	id				    SERIAL PRIMARY KEY,
	base_url		  varchar(250),
	client_id		  varchar(250),
	client_secret	varchar(250),
	unique (base_url)

);

CREATE TABLE scm_orgs
(
	id				      SERIAL PRIMARY KEY,
	scm_id			    int,
	org_identity		varchar(250),
	org_name		    varchar(250),
	cx_flow_url 	  varchar(50),
  cx_flow_config 	varchar(1000),
	team			      varchar(50),
	unique (scm_id, org_identity),

	CONSTRAINT fk_scm_orgs
		FOREIGN KEY (scm_id)
			REFERENCES scms(id)
);

CREATE TABLE scm_repos
(
	id						        SERIAL PRIMARY KEY,
	org_id					      int,
	repo_identity					varchar(50),
	webhook_id				    varchar(50),
	is_webhook_configured	boolean,
	unique (org_id, repo_identity),

	CONSTRAINT fk_scm_repos
		FOREIGN KEY (org_id)
			REFERENCES scm_orgs(id)
);

CREATE TABLE scm_tokens (
	id				  SERIAL PRIMARY KEY,
	org_id			int,
	type			  varchar(20),
	token			  varchar(100),
	unique (org_id, type),

	CONSTRAINT fk_tokens
		FOREIGN KEY (org_id)
			REFERENCES scm_orgs(id)
);