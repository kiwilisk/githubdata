package org.codecentric.github;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;
import java.util.Collection;

import static java.util.stream.Collectors.toList;

public class GitRepository {

    private final RepositoryService repositoryService;

    public GitRepository(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    public Collection<Repository> loadRepositoriesOf(User user) {
        try {
            return repositoryService.getRepositories(user.getLogin());
        } catch (IOException e) {
            throw new RuntimeException("Could not load repository of login " + user.getLogin() + " from github api", e);
        }
    }

    public Collection<Repository> loadRepositoriesOf(Collection<User> users) {
        return users.stream()
                .flatMap(user -> loadRepositoriesOf(user).stream())
                .collect(toList());
    }

    public Collection<String> getLanguageNamesFrom(Collection<Repository> repositories) {
        return repositories.stream()
                .map(Repository::getLanguage)
                .distinct()
                .collect(toList());
    }
}
