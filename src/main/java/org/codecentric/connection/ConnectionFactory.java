package org.codecentric.connection;

public enum ConnectionFactory {

    INSTANCE;

    public DatabaseConnection get() {
        return new H2DatabaseConnection();
    }
}
