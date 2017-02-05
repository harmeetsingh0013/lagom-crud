package com.knoldus.usercrud.user.impl;

import akka.Done;
import akka.NotUsed;
import com.knoldus.usercrud.user.api.User;
import com.knoldus.usercrud.user.api.UserService;
import com.knoldus.usercrud.user.impl.commands.UserCommand;
import com.knoldus.usercrud.user.impl.commands.UserCommand.CreateUser;
import com.knoldus.usercrud.user.impl.commands.UserCommand.DeleteUser;
import com.knoldus.usercrud.user.impl.commands.UserCommand.UpdateUser;
import com.knoldus.usercrud.user.impl.commands.UserCommand.UserCurrentState;
import com.knoldus.usercrud.user.impl.events.UserEventProcessor;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Created by harmeet on 30/1/17.
 */
public class UserServiceImpl implements UserService {

    private final PersistentEntityRegistry persistentEntityRegistry;
    private final CassandraSession session;

    @Inject
    public UserServiceImpl(final PersistentEntityRegistry registry, ReadSide readSide, CassandraSession session) {
        this.persistentEntityRegistry = registry;
        this.session = session;

        persistentEntityRegistry.register(UserEntity.class);
        readSide.register(UserEventProcessor.class);
    }

    @Override
    public ServiceCall<NotUsed, Optional<User>> user(String id) {
        return request -> {
            CompletionStage<Optional<User>> userFuture =
                    session.selectAll("SELECT * FROM users WHERE id = ?", id)
                            .thenApply(rows ->
                                    rows.stream()
                                            .map(row -> User.builder().id(row.getString("id"))
                                                    .name(row.getString("name")).age(row.getInt("age"))
                                                    .build()
                                            )
                                            .findFirst()
                            );
            return userFuture;
        };
    }

    @Override
    public ServiceCall<User, Done> newUser() {
        return user -> {
            PersistentEntityRef<UserCommand> ref = userEntityRef(user);
            return ref.ask(CreateUser.builder().user(user).build());
        };
    }

    @Override
    public ServiceCall<User, Done> updateUser() {
        return user -> {
            PersistentEntityRef<UserCommand> ref = userEntityRef(user);
            return ref.ask(UpdateUser.builder().user(user).build());
        };
    }

    @Override
    public ServiceCall<NotUsed, User> delete(String id) {
        return request -> {
            User user = User.builder().id(id).build();
            PersistentEntityRef<UserCommand> ref = userEntityRef(user);
            return ref.ask(DeleteUser.builder().user(user).build());
        };
    }

    @Override
    public ServiceCall<NotUsed, Optional<User>> currentState(String id) {
        return request -> {
            User user = User.builder().id(id).build();
            PersistentEntityRef<UserCommand> ref = userEntityRef(user);
            return ref.ask(new UserCurrentState());
        };
    }

    private PersistentEntityRef<UserCommand> userEntityRef(User user) {
        return persistentEntityRegistry.refFor(UserEntity.class, user.getId());
    }
}
