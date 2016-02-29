package org.codecentric.data;

import org.codecentric.tables.pojos.Repository;
import org.codecentric.tables.records.RepositoryRecord;
import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.codecentric.tables.Repository.REPOSITORY;

public class RepositoryStore {

    public void insertAll(Collection<Repository> languages, DSLContext context) {
        List<InsertSetMoreStep<RepositoryRecord>> collect = languages.stream()
                .map(repository -> context.insertInto(REPOSITORY).set(context.newRecord(REPOSITORY, repository)))
                .collect(toList());
        context.batch(collect).execute();
    }

    public void insert(Repository repository, DSLContext context) {
        try {
            context.executeInsert(context.newRecord(REPOSITORY, repository));
        } catch (Exception e) {
            throw new RuntimeException("Failed to store Repository " + repository, e);
        }
    }
}
