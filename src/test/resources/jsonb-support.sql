-- Support PostgreSQL "jsonb" type in the test H2 database.
create domain if not exists jsonb as other;