package org.codecentric.data;

import org.codecentric.tables.pojos.Member;
import org.codecentric.tables.records.MemberRecord;
import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.codecentric.tables.Member.MEMBER;

public class MemberStore {

    public void insertAll(Collection<Member> languages, DSLContext context) {
        List<InsertSetMoreStep<MemberRecord>> collect = languages.stream()
                .map(member -> context.insertInto(MEMBER).set(context.newRecord(MEMBER, member)))
                .collect(toList());
        context.batch(collect).execute();
    }

    public void insert(Member member, DSLContext context) {
        try {
            context.executeInsert(context.newRecord(MEMBER, member));
        } catch (Exception e) {
            throw new RuntimeException("Failed to store member " + member, e);
        }
    }
}
