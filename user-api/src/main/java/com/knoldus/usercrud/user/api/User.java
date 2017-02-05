package com.knoldus.usercrud.user.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Builder;
import lombok.Value;

/**
 * Created by harmeet on 1/2/17.
 */
@Value
@Builder
@JsonDeserialize
public class User implements Jsonable {
    String id;
    String name;
    int age;
}
