package org.codecentric.data;

import org.codecentric.tables.pojos.Language;
import org.codecentric.tables.records.LanguageRecord;
import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.codecentric.tables.Language.LANGUAGE;

public class LanguageStore {

    public void insertAll(Collection<Language> languages, DSLContext context) {
        List<InsertSetMoreStep<LanguageRecord>> collect = languages.stream()
                .map(l -> context.insertInto(LANGUAGE).set(context.newRecord(LANGUAGE, l)))
                .collect(toList());
        context.batch(collect).execute();
    }

    public void insert(Language language, DSLContext context) {
        try {
            context.executeInsert(context.newRecord(LANGUAGE, language));
        } catch (Exception e) {
            throw new RuntimeException("Failed to store language " + language, e);
        }
    }
}
