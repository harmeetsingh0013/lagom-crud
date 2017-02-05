package com.knoldus.usercrud.user.impl.events;

import akka.Done;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.knoldus.usercrud.user.impl.events.UserEvent.UserCreated;
import com.knoldus.usercrud.user.impl.events.UserEvent.UserDeleted;
import com.knoldus.usercrud.user.impl.events.UserEvent.UserUpdated;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Created by harmeet on 31/1/17.
 */
public class UserEventProcessor extends ReadSideProcessor<UserEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserEventProcessor.class);

    private final CassandraSession session;
    private final CassandraReadSide readSide;

    private PreparedStatement writeUsers;
    private PreparedStatement deleteUsers;

    @Inject
    public UserEventProcessor(final CassandraSession session, final CassandraReadSide readSide) {
        this.session = session;
        this.readSide = readSide;
    }

    @Override
    public PSequence<AggregateEventTag<UserEvent>> aggregateTags() {
        LOGGER.info(" aggregateTags method ... ");
        return TreePVector.singleton(UserEventTag.INSTANCE);
    }

    @Override
    public ReadSideHandler<UserEvent> buildHandler() {
        LOGGER.info(" buildHandler method ... ");
        return readSide.<UserEvent>builder("users_offset")
                .setGlobalPrepare(this::createTable)
                .setPrepare(evtTag -> prepareWriteUser()
                        .thenCombine(prepareDeleteUser(), (d1, d2) -> Done.getInstance())
                )
                .setEventHandler(UserCreated.class, this::processPostAdded)
                .setEventHandler(UserUpdated.class, this::processPostUpdated)
                .setEventHandler(UserDeleted.class, this::processPostDeleted)
                .build();
    }

    // Execute only once while application is start
    private CompletionStage<Done> createTable() {
        return session.executeCreateTable(
                "CREATE TABLE IF NOT EXISTS users ( " +
                        "id TEXT, name TEXT, age INT, PRIMARY KEY(id))"
        );
    }

    /*
    * START: Prepare statement for insert user values into users table.
    * This is just creation of prepared statement, we will map this statement with our event
    */
    private CompletionStage<Done> prepareWriteUser() {
        return session.prepare(
                "INSERT INTO users (id, name, age) VALUES (?, ?, ?)"
        ).thenApply(ps -> {
            setWriteUsers(ps);
            return Done.getInstance();
        });
    }

    private void setWriteUsers(PreparedStatement statement) {
        this.writeUsers = statement;
    }

    // Bind prepare statement while UserCreate event is executed
    private CompletionStage<List<BoundStatement>> processPostAdded(UserCreated event) {
        BoundStatement bindWriteUser = writeUsers.bind();
        bindWriteUser.setString("id", event.getUser().getId());
        bindWriteUser.setString("name", event.getUser().getName());
        bindWriteUser.setInt("age", event.getUser().getAge());
        return CassandraReadSide.completedStatements(Arrays.asList(bindWriteUser));
    }
    /* ******************* END ****************************/

    /* START: Prepare statement for update the data in users table.
    * This is just creation of prepared statement, we will map this statement with our event
    */
    private CompletionStage<List<BoundStatement>> processPostUpdated(UserUpdated event) {
        BoundStatement bindWriteUser = writeUsers.bind();
        bindWriteUser.setString("id", event.getUser().getId());
        bindWriteUser.setString("name", event.getUser().getName());
        bindWriteUser.setInt("age", event.getUser().getAge());
        return CassandraReadSide.completedStatements(Arrays.asList(bindWriteUser));
    }
    /* ******************* END ****************************/

    /* START: Prepare statement for delete the the user from table.
    * This is just creation of prepared statement, we will map this statement with our event
    */
    private CompletionStage<Done> prepareDeleteUser() {
        return session.prepare(
                "DELETE FROM users WHERE id=?"
        ).thenApply(ps -> {
            setDeleteUsers(ps);
            return Done.getInstance();
        });
    }

    private void setDeleteUsers(PreparedStatement deleteUsers) {
        this.deleteUsers = deleteUsers;
    }

    private CompletionStage<List<BoundStatement>> processPostDeleted(UserDeleted event) {
        BoundStatement bindWriteUser = deleteUsers.bind();
        bindWriteUser.setString("id", event.getUser().getId());
        return CassandraReadSide.completedStatements(Arrays.asList(bindWriteUser));
    }
    /* ******************* END ****************************/
}
