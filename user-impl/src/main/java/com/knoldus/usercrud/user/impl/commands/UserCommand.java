package com.knoldus.usercrud.user.impl.commands;

import akka.Done;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.knoldus.usercrud.user.api.User;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Builder;
import lombok.Value;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;

/**
 * Created by harmeet on 30/1/17.
 */
public interface UserCommand extends Jsonable {

    @Value
    @Builder
    @JsonDeserialize
    final class CreateUser implements UserCommand, PersistentEntity.ReplyType<Done> {
        User user;
    }

    @Value
    @Builder
    @JsonDeserialize
    final class UpdateUser implements UserCommand, PersistentEntity.ReplyType<Done> {
        User user;
    }

    @Value
    @Builder
    @JsonDeserialize
    final class DeleteUser implements UserCommand, PersistentEntity.ReplyType<User> {
        User user;
    }

    @Immutable
    @JsonDeserialize
    final class UserCurrentState implements UserCommand, PersistentEntity.ReplyType<Optional<User>> {}
}
