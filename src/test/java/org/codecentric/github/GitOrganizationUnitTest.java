package org.codecentric.github;

import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.OrganizationService;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

public class GitOrganizationUnitTest {

    @Test
    public void test_load_members_of_organization() throws Exception {
        OrganizationService organizationService = mock(OrganizationService.class);
        User firstUser = new User();
        firstUser.setLogin("LoginName");
        User secondUser = new User();
        secondUser.setLogin("anotherLogin");
        List<User> users = asList(firstUser, secondUser);
        when(organizationService.getMembers(eq("codecentric"))).thenReturn(users);
        GitOrganization gitOrganization = new GitOrganization(organizationService);

        Collection<User> membersOfCodecentric = gitOrganization.loadMembersOf("codecentric");

        assertThat(membersOfCodecentric).contains(firstUser, secondUser);
    }

    @Test
    public void test_failed_to_load_members() throws Exception {
        OrganizationService organizationService = mock(OrganizationService.class);
        doThrow(new IOException("Somehting went wrong")).when(organizationService).getMembers(anyString());
        GitOrganization gitOrganization = new GitOrganization(organizationService);

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> gitOrganization.loadMembersOf("codecentric"))
                .withMessage("Could not load members of organization codecentric from github api");
    }
}