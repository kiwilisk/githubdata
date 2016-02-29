package org.codecentric.connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.TransactionalRunnable;
import org.jooq.conf.RenderNameStyle;
import org.jooq.conf.Settings;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2DatabaseConnection implements DatabaseConnection {

    private static final String CONNECTION_URI = "jdbc:h2:";
    private static final String DATA_BASE_PATH = "./data/codecentric";
    private static final String USER = "sa";
    private static final String PW = ":dbA!";

    @Override
    public Connection create() {
        try {
            return DriverManager.getConnection(CONNECTION_URI + DATA_BASE_PATH, USER, PW);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to establish connection to " + CONNECTION_URI + DATA_BASE_PATH, e);
        }
    }

    @Override
    public DSLContext createContextUsing(Connection connection) {
        Settings settings = new Settings();
        settings.setRenderNameStyle(RenderNameStyle.AS_IS);
        return DSL.using(connection, SQLDialect.H2, settings);
    }

    @Override
    public void withTransaction(TransactionalRunnable transactionalCode) {
        try (Connection connection = create()) {
            createContextUsing(connection).transaction(transactionalCode);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to execute with transaction", e);
        }
    }
}
