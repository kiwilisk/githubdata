package org.codecentric.github;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

public class GitRepositoryUnitTest {

    @Test
    public void test_load_repositories_of_single_user() throws Exception {
        User user = new User();
        user.setLogin("daniel.hill");
        Repository firstRepository = new Repository();
        firstRepository.setOwner(user);
        Repository secondRepository = new Repository();
        secondRepository.setOwner(user);
        RepositoryService repositoryService = mock(RepositoryService.class);
        when(repositoryService.getRepositories(eq("daniel.hill"))).thenReturn(asList(firstRepository, secondRepository));
        GitRepository gitRepository = new GitRepository(repositoryService);

        Collection<Repository> repositories = gitRepository.loadRepositoriesOf(user);

        assertThat(repositories).contains(firstRepository, secondRepository);
    }

    @Test
    public void test_load_repositories_of_multiple_users() throws Exception {
        User firstUser = new User();
        firstUser.setLogin("daniel.hill");
        User secondUser = new User();
        secondUser.setLogin("creativeLogin");
        Repository firstRepository = new Repository();
        firstRepository.setOwner(firstUser);
        Repository secondRepository = new Repository();
        secondRepository.setOwner(secondUser);
        RepositoryService repositoryService = mock(RepositoryService.class);
        when(repositoryService.getRepositories(eq("daniel.hill"))).thenReturn(singletonList(firstRepository));
        when(repositoryService.getRepositories(eq("creativeLogin"))).thenReturn(singletonList(secondRepository));
        GitRepository gitRepository = new GitRepository(repositoryService);

        Collection<Repository> repositories = gitRepository.loadRepositoriesOf(asList(firstUser, secondUser));

        assertThat(repositories).contains(firstRepository, secondRepository);
    }

    @Test
    public void test_get_languages() throws Exception {
        Repository javaRepository = new Repository();
        javaRepository.setLanguage("Java");
        Repository cSharpRepository = new Repository();
        cSharpRepository.setLanguage("C#");
        RepositoryService repositoryService = mock(RepositoryService.class);
        GitRepository gitRepository = new GitRepository(repositoryService);

        Collection<String> languageNames = gitRepository.getLanguageNamesFrom(asList(javaRepository, cSharpRepository));

        assertThat(languageNames).contains("Java", "C#");
    }

    @Test
    public void test_failed_to_load_repository() throws Exception {
        User user = new User();
        user.setLogin("daniel.hill");
        RepositoryService repositoryService = mock(RepositoryService.class);
        doThrow(new IOException("Somehting went wrong")).when(repositoryService).getRepositories(anyString());
        GitRepository gitRepository = new GitRepository(repositoryService);

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> gitRepository.loadRepositoriesOf(user))
                .withMessage("Could not load repository of login daniel.hill from github api");
    }


}