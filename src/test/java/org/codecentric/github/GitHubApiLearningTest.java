package org.codecentric.github;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.OrganizationService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GitHubApiLearningTest {

    @Test
    public void test_get_public_members() throws Exception {
        GitHubClient gitHubClient = GitHubClient.createClient("https://api.github.com");
        OrganizationService organizationService = new OrganizationService(gitHubClient);
        List<User> membersOfCodecentric = organizationService.getPublicMembers("codecentric");

        assertThat(membersOfCodecentric).isNotEmpty();
        membersOfCodecentric.stream().findAny().ifPresent(user -> System.out.println(user.getLogin()));
    }

    @Test
    public void test_get_repository_of_member() throws Exception {
        GitHubClient gitHubClient = GitHubClient.createClient("https://api.github.com");
        RepositoryService repositoryService = new RepositoryService(gitHubClient);
        List<Repository> repositories = repositoryService.getRepositories("rfalke");

        assertThat(repositories).isNotEmpty();
        repositories.stream().findAny().ifPresent(repo -> System.out.println(repo.getLanguage()));
    }
}
