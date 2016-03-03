package org.codecentric.connection;

import org.jooq.DSLContext;
import org.jooq.util.h2.H2DataType;

import java.sql.Connection;

import static org.jooq.impl.DSL.constraint;
import static org.jooq.impl.DSL.count;

public class DatabaseCreator {

    private final DatabaseConnection databaseConnection;

    public DatabaseCreator(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    /**
     * Convenience method for delivery.
     * Creates H2 database without having to install H2 and inserting sql dump.
     */
    public void create() {
        try (Connection connection = databaseConnection.create()) {
            DSLContext context = databaseConnection.createContextUsing(connection);
            if (!dataBaseExists(context)) {
                createDataBase(context);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error creating database.", e);
        }
    }

    private boolean dataBaseExists(DSLContext context) {
        return context.select(count())
                .from("information_schema.tables")
                .where("table_schema = 'PUBLIC'")
                .fetchOne(0, int.class) != 0;
    }

    private void createDataBase(DSLContext context) {
        context.batch(
                context.dropTableIfExists("MEMBER"),
                context.dropTableIfExists("REPOSITORY"),
                context.dropTableIfExists("REPOSITORY_TO_LANGUAGE"),
                context.dropTableIfExists("LANGUAGE"),
                context.dropViewIfExists("REPOSITORY_LANGUAGE"),

                context.createTable("MEMBER")
                        .column("id", H2DataType.BIGINT.nullable(false))
                        .column("github_id", H2DataType.BIGINT)
                        .column("login", H2DataType.VARCHAR.length(50)),


                context.alterTable("MEMBER")
                        .add(constraint("MEMBER_PK").unique("id")),

                context.alterTable("MEMBER")
                        .add(constraint("MEMBER_UNIQUE_GITHUB").unique("github_id")),

                context.alterTable("MEMBER")
                        .add(constraint("MEMBER_UNIQUE_LOGIN").unique("login")),

                context.createTable("LANGUAGE")
                        .column("id", H2DataType.BIGINT.nullable(false))
                        .column("name", H2DataType.VARCHAR.length(50)),

                context.alterTable("LANGUAGE")
                        .add(constraint("LANGUAGE_PK").unique("id")),

                context.alterTable("LANGUAGE")
                        .add(constraint("LANGUAGE_UNIQUE_NAME").unique("name")),

                context.createTable("REPOSITORY")
                        .column("id", H2DataType.BIGINT.nullable(false))
                        .column("github_id", H2DataType.BIGINT)
                        .column("name", H2DataType.VARCHAR.length(100))
                        .column("member_id", H2DataType.BIGINT)
                        .column("language_id", H2DataType.BIGINT),

                context.alterTable("REPOSITORY")
                        .add(constraint("REPOSITORY_PK").unique("id")),

                context.alterTable("REPOSITORY")
                        .add(constraint("REPOSITORY_UNIQUE_GITHUB").unique("github_id")),

                context.alterTable("REPOSITORY")
                        .add(constraint("FK_MEMBER_ID").foreignKey("member_id").references("MEMBER", "id")),

                context.alterTable("REPOSITORY")
                        .add(constraint("FK_LANGUAGE_ID").foreignKey("language_id").references("LANGUAGE", "id")),

                context.createSequence("SEQUENCE_ID"),

                context.query("CREATE VIEW REPOSITORY_LANGUAGE AS SELECT m.login, l.name, count(l.name) AS amount FROM MEMBER m inner join REPOSITORY r on m.id = r.member_id INNER JOIN LANGUAGE l on r.language_id = l.id GROUP BY (m.login,l.name) ORDER BY m.login")
        ).execute();
    }
}
