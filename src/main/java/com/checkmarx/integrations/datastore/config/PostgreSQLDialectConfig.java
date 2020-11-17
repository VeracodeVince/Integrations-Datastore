package com.checkmarx.integrations.datastore.config;

import org.hibernate.dialect.PostgreSQL10Dialect;

public class PostgreSQLDialectConfig extends PostgreSQL10Dialect {

    @Override
    public String getDropSequenceString(String sequenceName) {
        return "drop sequence if exists " + sequenceName;
    }

    @Override
    public boolean dropConstraints() {
        return false;
    }
}