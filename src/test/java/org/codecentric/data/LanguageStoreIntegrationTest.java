package org.codecentric.data;

import org.codecentric.Sequences;
import org.codecentric.connection.DatabaseConnection;
import org.codecentric.tables.pojos.Language;
import org.jooq.DSLContext;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;

import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.codecentric.tables.Language.LANGUAGE;

public class LanguageStoreIntegrationTest {

    @Rule
    public final TestDataBase dataBase = new TestDataBase();

    @Test
    public void test_single_insert() throws Exception {
        LanguageStore languageStore = new LanguageStore();
        DatabaseConnection databaseConnection = dataBase.getConnection();

        try (Connection connection = databaseConnection.create()) {
            DSLContext context = databaseConnection.createContextUsing(connection);
            Language java = new Language(context.nextval(Sequences.SEQUENCE_ID), "Java");
            languageStore.insert(java, context);

            List<Language> loadedLanguages = context.select().from(LANGUAGE).fetchInto(Language.class);
            assertThat(getOnlyElement(loadedLanguages)).isEqualTo(java);
        }
    }

    @Test
    public void test_multiple_insert() throws Exception {
        LanguageStore languageStore = new LanguageStore();
        DatabaseConnection databaseConnection = dataBase.getConnection();

        try (Connection connection = databaseConnection.create()) {
            DSLContext context = databaseConnection.createContextUsing(connection);
            Language java = new Language(context.nextval(Sequences.SEQUENCE_ID), "Java");
            Language cSharp = new Language(context.nextval(Sequences.SEQUENCE_ID), "C#");
            languageStore.insertAll(asList(java, cSharp), context);

            List<Language> loadedLanguages = context.select().from(LANGUAGE).fetchInto(Language.class);
            assertThat(loadedLanguages).contains(java, cSharp);
        }
    }
}