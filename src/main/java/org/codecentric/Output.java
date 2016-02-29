package org.codecentric;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codecentric.connection.ConnectionFactory;
import org.codecentric.connection.DatabaseConnection;
import org.codecentric.tables.pojos.RepositoryLanguage;
import org.jooq.DSLContext;

import java.sql.Connection;
import java.util.List;

import static org.codecentric.tables.RepositoryLanguage.REPOSITORY_LANGUAGE;

class Output {

    private static final Logger LOGGER = LogManager.getLogger(Output.class);

    public static void main(String[] args) {
        DatabaseConnection databaseConnection = ConnectionFactory.INSTANCE.get();
        try (Connection connection = databaseConnection.create()) {
            DSLContext context = databaseConnection.createContextUsing(connection);
            List<RepositoryLanguage> repositoryLanguages = context.selectFrom(REPOSITORY_LANGUAGE).fetchInto(RepositoryLanguage.class);
            repositoryLanguages.forEach(System.out::println);
        } catch (Exception e) {
            LOGGER.error("Could not load from data database.", e);
        }
    }
}
