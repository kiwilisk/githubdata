package org.codecentric;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codecentric.connection.ConnectionFactory;
import org.codecentric.data.LanguageStore;
import org.codecentric.data.MemberStore;
import org.codecentric.data.ProjectStore;
import org.codecentric.data.RepositoryStore;
import org.codecentric.github.GitOrganization;
import org.codecentric.github.GitRepository;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.OrganizationService;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.util.Collection;

class Synchronize {

    private static final Logger LOGGER = LogManager.getLogger(Synchronize.class);

    public static void main(String[] args) {
        try {
            GitHubClient gitHubClient = GitHubClient.createClient("https://api.github.com");
            OrganizationService organizationService = new OrganizationService(gitHubClient);
            GitOrganization gitOrganization = new GitOrganization(organizationService);
            String organization = "codecentric";
            Collection<User> usersOfCodecentric = gitOrganization.loadMembersOf(organization);
            LOGGER.info("Fetched " + usersOfCodecentric.size() + " users of " + organization);

            RepositoryService repositoryService = new RepositoryService(gitHubClient);
            GitRepository gitRepository = new GitRepository(repositoryService);
            Collection<Repository> repositories = gitRepository.loadRepositoriesOf(usersOfCodecentric);
            LOGGER.info("Fetched " + repositories.size() + " repositories)");
            Collection<String> languageNames = gitRepository.getLanguageNamesFrom(repositories);

            LOGGER.info("Starting import");
            ProjectStore projectStore = new ProjectStore(usersOfCodecentric, repositories, languageNames, new LanguageStore(), new MemberStore(), new RepositoryStore());
            projectStore.insert(ConnectionFactory.INSTANCE.get());
            LOGGER.info("Done.");
        } catch (Exception e) {
            LOGGER.error("Could not load and insert data into database.", e);
        }
    }
}
