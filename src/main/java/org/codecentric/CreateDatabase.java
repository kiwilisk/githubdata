package org.codecentric;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codecentric.connection.ConnectionFactory;
import org.codecentric.connection.DatabaseConnection;
import org.codecentric.connection.DatabaseImpl;


class CreateDatabase {

    private static final Logger LOGGER = LogManager.getLogger(CreateDatabase.class);

    public static void main(String[] args) {
        DatabaseConnection databaseConnection = ConnectionFactory.INSTANCE.get();
        DatabaseImpl databaseImpl = new DatabaseImpl(databaseConnection);
        try {
            databaseImpl.create();
        } catch (Exception e) {
            LOGGER.error("Could not create database.", e);
        }
    }
}
