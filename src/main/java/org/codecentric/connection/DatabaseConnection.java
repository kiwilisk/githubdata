package org.codecentric.connection;

import org.jooq.DSLContext;
import org.jooq.TransactionalRunnable;

import java.sql.Connection;

public interface DatabaseConnection {

    Connection create();

    DSLContext createContextUsing(Connection connection);

    void withTransaction(TransactionalRunnable transactionalCode);
}
