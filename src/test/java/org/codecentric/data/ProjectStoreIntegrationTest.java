package org.codecentric.data;

import org.codecentric.connection.DatabaseConnection;
import org.codecentric.tables.pojos.RepositoryLanguage;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.jooq.DSLContext;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.codecentric.tables.RepositoryLanguage.REPOSITORY_LANGUAGE;

public class ProjectStoreIntegrationTest {

    @Rule
    public final TestDataBase dataBase = new TestDataBase();

    @Test
    public void test_insert_project() throws Exception {
        User javaUser = new User();
        javaUser.setLogin("newLogin");
        javaUser.setId(1234);
        User scalaUser = new User();
        scalaUser.setId(4657);
        scalaUser.setLogin("alsoNewLogin");
        Repository javaRepository = new Repository();
        javaRepository.setLanguage("Java");
        javaRepository.setOwner(javaUser);
        javaRepository.setId(858585L);
        Repository scalaRepository = new Repository();
        scalaRepository.setLanguage("Scala");
        scalaRepository.setOwner(scalaUser);
        scalaRepository.setId(8796988L);
        ProjectStore projectStore = new ProjectStore(asList(javaUser, scalaUser), asList(javaRepository, scalaRepository), asList("Java", "Scala"), new LanguageStore(), new MemberStore(), new RepositoryStore());

        projectStore.insert(dataBase.getConnection());

        assertThat(loadRepositoryLanguages()).contains(new RepositoryLanguage("newLogin", "Java", 1L), new RepositoryLanguage("alsoNewLogin", "Scala", 1L));
    }

    private List<RepositoryLanguage> loadRepositoryLanguages() throws SQLException {
        DatabaseConnection databaseConnection = dataBase.getConnection();
        try (Connection connection = databaseConnection.create()) {
            DSLContext context = databaseConnection.createContextUsing(connection);
            return context.selectFrom(REPOSITORY_LANGUAGE).fetchInto(RepositoryLanguage.class);
        }
    }
}