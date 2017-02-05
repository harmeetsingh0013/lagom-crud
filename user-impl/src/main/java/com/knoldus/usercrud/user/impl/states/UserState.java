package com.knoldus.usercrud.user.impl.states;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.knoldus.usercrud.user.api.User;
import com.lightbend.lagom.serialization.CompressedJsonable;
import lombok.Builder;
import lombok.Value;

import java.util.Optional;

/**
 * Created by harmeet on 2/2/17.
 */
@Value
@Builder
@JsonDeserialize
public class UserState implements CompressedJsonable {
    Optional<User> user;
    String timestamp;
}
