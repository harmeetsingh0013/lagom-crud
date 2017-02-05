package com.knoldus.usercrud.user.impl;

import akka.Done;
import com.knoldus.usercrud.user.impl.commands.UserCommand;
import com.knoldus.usercrud.user.impl.commands.UserCommand.CreateUser;
import com.knoldus.usercrud.user.impl.commands.UserCommand.DeleteUser;
import com.knoldus.usercrud.user.impl.commands.UserCommand.UpdateUser;
import com.knoldus.usercrud.user.impl.commands.UserCommand.UserCurrentState;
import com.knoldus.usercrud.user.impl.events.UserEvent;
import com.knoldus.usercrud.user.impl.events.UserEvent.UserCreated;
import com.knoldus.usercrud.user.impl.events.UserEvent.UserDeleted;
import com.knoldus.usercrud.user.impl.events.UserEvent.UserUpdated;
import com.knoldus.usercrud.user.impl.states.UserState;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Created by harmeet on 30/1/17.
 */
public class UserEntity extends PersistentEntity<UserCommand, UserEvent, UserState> {

    @Override
    public Behavior initialBehavior(Optional<UserState> snapshotState) {

        // initial behaviour of user
        BehaviorBuilder behaviorBuilder = newBehaviorBuilder(
                UserState.builder().user(Optional.empty())
                        .timestamp(LocalDateTime.now().toString()).build()
        );

        behaviorBuilder.setCommandHandler(CreateUser.class, (cmd, ctx) ->
                ctx.thenPersist(UserCreated.builder().user(cmd.getUser())
                        .entityId(entityId()).build(), evt -> ctx.reply(Done.getInstance()))
        );

        behaviorBuilder.setEventHandler(UserCreated.class, evt ->
                UserState.builder().user(Optional.of(evt.getUser()))
                        .timestamp(LocalDateTime.now().toString()).build()
        );

        behaviorBuilder.setCommandHandler(UpdateUser.class, (cmd, ctx) ->
                ctx.thenPersist(UserUpdated.builder().user(cmd.getUser()).entityId(entityId()).build()
                        , evt -> ctx.reply(Done.getInstance()))
        );

        behaviorBuilder.setEventHandler(UserUpdated.class, evt ->
                UserState.builder().user(Optional.of(evt.getUser()))
                        .timestamp(LocalDateTime.now().toString()).build()
        );

        behaviorBuilder.setCommandHandler(DeleteUser.class, (cmd, ctx) ->
                ctx.thenPersist(UserDeleted.builder().user(cmd.getUser()).entityId(entityId()).build(),
                        evt -> ctx.reply(cmd.getUser()))
        );

        behaviorBuilder.setEventHandler(UserDeleted.class, evt ->
                UserState.builder().user(Optional.empty())
                        .timestamp(LocalDateTime.now().toString()).build()
        );

        behaviorBuilder.setReadOnlyCommandHandler(UserCurrentState.class, (cmd, ctx) ->
                ctx.reply(state().getUser())
        );

        return behaviorBuilder.build();
    }
}
