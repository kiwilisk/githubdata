package org.codecentric.data;

import org.codecentric.Sequences;
import org.codecentric.connection.DatabaseConnection;
import org.codecentric.tables.pojos.Language;
import org.codecentric.tables.pojos.Member;
import org.codecentric.tables.pojos.Repository;
import org.jooq.DSLContext;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;

import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.codecentric.tables.Repository.REPOSITORY;

public class RepositoryStoreIntegrationTest {

    @Rule
    public final TestDataBase dataBase = new TestDataBase();

    @Test
    public void test_single_insert() throws Exception {
        RepositoryStore repositoryStore = new RepositoryStore();
        DatabaseConnection databaseConnection = dataBase.getConnection();
        try (Connection connection = databaseConnection.create()) {
            DSLContext context = databaseConnection.createContextUsing(connection);
            MemberStore memberStore = new MemberStore();
            Member daniel = new Member(context.nextval(Sequences.SEQUENCE_ID), 1234L, "daniel.hill");
            memberStore.insert(daniel, context);
            LanguageStore languageStore = new LanguageStore();
            Language java = new Language(context.nextval(Sequences.SEQUENCE_ID), "Java");
            languageStore.insert(java, context);
            Repository javaRepository = new Repository(context.nextval(Sequences.SEQUENCE_ID), 7676L, "My Repository", daniel.getId(), java.getId());

            repositoryStore.insert(javaRepository, context);

            List<Repository> repositories = context.select().from(REPOSITORY).fetchInto(Repository.class);
            assertThat(getOnlyElement(repositories)).isEqualTo(javaRepository);
        }
    }

    @Test
    public void test_multiple_insert() throws Exception {
        RepositoryStore repositoryStore = new RepositoryStore();
        DatabaseConnection databaseConnection = dataBase.getConnection();
        try (Connection connection = databaseConnection.create()) {
            DSLContext context = databaseConnection.createContextUsing(connection);
            MemberStore memberStore = new MemberStore();
            Member daniel = new Member(context.nextval(Sequences.SEQUENCE_ID), 1233L, "daniel.hill");
            memberStore.insert(daniel, context);
            LanguageStore languageStore = new LanguageStore();
            Language java = new Language(context.nextval(Sequences.SEQUENCE_ID), "Java");
            languageStore.insert(java, context);
            Repository javaRepository = new Repository(context.nextval(Sequences.SEQUENCE_ID), 7767L, "My Repository", daniel.getId(), java.getId());
            Repository cSharpRepository = new Repository(context.nextval(Sequences.SEQUENCE_ID), 5445L, "My Repository", daniel.getId(), java.getId());

            repositoryStore.insertAll(asList(javaRepository, cSharpRepository), context);

            List<Repository> repositories = context.select().from(REPOSITORY).fetchInto(Repository.class);
            assertThat(repositories).contains(javaRepository, cSharpRepository);
        }
    }
}