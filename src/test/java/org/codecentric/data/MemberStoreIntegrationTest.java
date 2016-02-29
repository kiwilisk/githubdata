package org.codecentric.data;

import org.codecentric.Sequences;
import org.codecentric.connection.DatabaseConnection;
import org.codecentric.tables.pojos.Member;
import org.jooq.DSLContext;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;

import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.codecentric.tables.Member.MEMBER;

public class MemberStoreIntegrationTest {

    @Rule
    public final TestDataBase dataBase = new TestDataBase();

    @Test
    public void test_single_insert() throws Exception {
        MemberStore memberStore = new MemberStore();
        DatabaseConnection databaseConnection = dataBase.getConnection();
        try (Connection connection = databaseConnection.create()) {
            DSLContext context = databaseConnection.createContextUsing(connection);
            Member daniel = new Member(context.nextval(Sequences.SEQUENCE_ID), 3456L, "daniel.hill");

            memberStore.insert(daniel, context);

            List<Member> loadedMembers = context.select().from(MEMBER).fetchInto(Member.class);
            assertThat(getOnlyElement(loadedMembers)).isEqualTo(daniel);
        }
    }

    @Test
    public void test_multiple_insert() throws Exception {
        MemberStore memberStore = new MemberStore();
        DatabaseConnection databaseConnection = dataBase.getConnection();
        try (Connection connection = databaseConnection.create()) {
            DSLContext context = databaseConnection.createContextUsing(connection);
            Member daniel = new Member(context.nextval(Sequences.SEQUENCE_ID), 3456L, "daniel.hill");
            Member heinz = new Member(context.nextval(Sequences.SEQUENCE_ID), 123L, "karl.heinz");

            memberStore.insertAll(asList(daniel, heinz), context);

            List<Member> loadedMembers = context.select().from(MEMBER).fetchInto(Member.class);
            assertThat(loadedMembers).contains(daniel, heinz);
        }
    }
}