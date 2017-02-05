package com.knoldus.usercrud.user.impl.events;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.knoldus.usercrud.user.api.User;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Builder;
import lombok.Value;

/**
 * Created by harmeet on 30/1/17.
 */
public interface UserEvent extends Jsonable, AggregateEvent<UserEvent> {

    @Override
    default AggregateEventTagger<UserEvent> aggregateTag() {
        return UserEventTag.INSTANCE;
    }

    @Value
    @Builder
    @JsonDeserialize
    final class UserCreated implements UserEvent, CompressedJsonable {
        User user;
        String entityId;
    }

    @Value
    @Builder
    @JsonDeserialize
    final class UserUpdated implements UserEvent, CompressedJsonable {
        User user;
        String entityId;
    }

    @Value
    @Builder
    @JsonDeserialize
    final class UserDeleted implements UserEvent, CompressedJsonable {
        User user;
        String entityId;
    }
}
