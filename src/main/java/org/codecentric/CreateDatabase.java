package org.codecentric;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codecentric.connection.ConnectionFactory;
import org.codecentric.connection.DatabaseConnection;
import org.codecentric.connection.DatabaseCreator;


class CreateDatabase {

    private static final Logger LOGGER = LogManager.getLogger(CreateDatabase.class);

    public static void main(String[] args) {
        DatabaseConnection databaseConnection = ConnectionFactory.INSTANCE.get();
        DatabaseCreator databaseCreator = new DatabaseCreator(databaseConnection);
        try {
            databaseCreator.create();
        } catch (Exception e) {
            LOGGER.error("Could not create database.", e);
        }
    }
}
