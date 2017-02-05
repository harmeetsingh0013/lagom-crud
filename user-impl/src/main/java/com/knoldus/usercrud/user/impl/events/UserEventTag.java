package com.knoldus.usercrud.user.impl.events;

import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;

/**
 * Created by harmeet on 31/1/17.
 */
public class UserEventTag {

    public static final AggregateEventTag<UserEvent> INSTANCE = AggregateEventTag.of(UserEvent.class);
}
