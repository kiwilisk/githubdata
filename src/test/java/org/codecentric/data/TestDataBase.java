package org.codecentric.data;

import org.codecentric.connection.DatabaseConnection;
import org.codecentric.connection.DatabaseCreator;
import org.codecentric.connection.H2DatabaseConnection;
import org.jooq.DSLContext;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.codecentric.tables.Language.LANGUAGE;
import static org.codecentric.tables.Member.MEMBER;
import static org.codecentric.tables.Repository.REPOSITORY;

public class TestDataBase implements TestRule {

    private static final String CONNECTION_URI = "jdbc:h2:";
    private static final String DATA_BASE_PATH = "./data/codecentric_test";
    private static final String USER = "sa";
    private static final String PW = "test";

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    createDatabase();
                    wipeTables();
                    base.evaluate();
                } catch (Throwable t) {
                    throw new RuntimeException("Failed to create test database", t);
                }
            }
        };
    }

    private void createDatabase() {
        DatabaseCreator database = new DatabaseCreator(getConnection());
        database.create();
    }

    public DatabaseConnection getConnection() {
        return new H2DatabaseConnection() {
            @Override
            public Connection create() {
                try {
                    return DriverManager.getConnection(CONNECTION_URI + DATA_BASE_PATH, USER, PW);
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to establish connection to " + CONNECTION_URI + DATA_BASE_PATH, e);
                }
            }
        };
    }

    private void wipeTables() throws SQLException {
        DatabaseConnection databaseConnection = getConnection();
        try (Connection connection = databaseConnection.create()) {
            DSLContext context = databaseConnection.createContextUsing(connection);
            context.deleteFrom(REPOSITORY).execute();
            context.deleteFrom(LANGUAGE).execute();
            context.deleteFrom(MEMBER).execute();
        }
    }
}
