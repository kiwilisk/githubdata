package org.codecentric.data;

import org.codecentric.connection.DatabaseConnection;
import org.codecentric.tables.pojos.Language;
import org.codecentric.tables.pojos.Member;
import org.codecentric.tables.pojos.Repository;
import org.eclipse.egit.github.core.User;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.Collection;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.codecentric.Sequences.SEQUENCE_ID;

public class ProjectStore {

    private final Collection<User> users;
    private final Collection<org.eclipse.egit.github.core.Repository> repositories;
    private final Collection<String> languageNames;
    private final LanguageStore languageStore;
    private final MemberStore memberStore;
    private final RepositoryStore repositoryStore;

    public ProjectStore(Collection<User> users, Collection<org.eclipse.egit.github.core.Repository> repositories, Collection<String> languageNames, LanguageStore languageStore, MemberStore memberStore, RepositoryStore repositoryStore) {
        this.users = users;
        this.repositories = repositories;
        this.languageNames = languageNames;
        this.languageStore = languageStore;
        this.memberStore = memberStore;
        this.repositoryStore = repositoryStore;
    }

    public void insert(DatabaseConnection databaseConnection) {
        databaseConnection.withTransaction(transaction -> {
            DSLContext context = DSL.using(transaction);
            Collection<Language> languages = insertLanguages(context);
            Collection<Member> members = insertMembers(context);
            insertRepositories(context, languages, members);
        });
    }

    private Collection<Language> insertLanguages(DSLContext create) {
        Collection<Language> languages = languageNames.stream()
                .map(language -> new Language(create.nextval(SEQUENCE_ID), language))
                .collect(toList());
        languageStore.insertAll(languages, create);
        return languages;
    }

    private Collection<Member> insertMembers(DSLContext context) {
        Collection<Member> members = users.stream()
                .map(user -> new Member(context.nextval(SEQUENCE_ID), (long) user.getId(), user.getLogin()))
                .collect(toList());
        memberStore.insertAll(members, context);
        return members;
    }

    private void insertRepositories(DSLContext create, Collection<Language> languages, Collection<Member> members) {
        Map<String, Long> nameToLanguageId = languages.stream().collect(toMap(Language::getName, Language::getId));
        Map<String, Long> memberLoginToId = members.stream().collect(toMap(Member::getLogin, Member::getId));
        Collection<Repository> repositories = this.repositories.stream()
                .map(repository -> new Repository(create.nextval(SEQUENCE_ID), repository.getId(), repository.getName(), memberLoginToId.get(repository.getOwner().getLogin()), nameToLanguageId.get(repository.getLanguage())))
                .collect(toList());
        repositoryStore.insertAll(repositories, create);
    }
}
