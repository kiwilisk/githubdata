package org.codecentric.github;

import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.OrganizationService;

import java.io.IOException;
import java.util.Collection;

public class GitOrganization {

    private final OrganizationService organizationService;

    public GitOrganization(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    public Collection<User> loadMembersOf(String organization) {
        try {
            return organizationService.getMembers(organization);
        } catch (IOException e) {
            throw new RuntimeException("Could not load members of organization " + organization + " from github api", e);
        }
    }
}
