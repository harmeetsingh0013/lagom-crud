package com.knoldus.usercrud.user.api;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;

import java.util.Optional;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.restCall;
import static com.lightbend.lagom.javadsl.api.transport.Method.*;

/**
 * Created by harmeet on 30/1/17.
 */
public interface UserService extends Service {

    ServiceCall<NotUsed, Optional<User>> user(String id);

    ServiceCall<User, Done> newUser();

    ServiceCall<User, Done> updateUser();

    ServiceCall<NotUsed, User> delete(String id);

    ServiceCall<NotUsed, Optional<User>> currentState(String id);

    @Override
    default Descriptor descriptor() {

        return named("user").withCalls(
                restCall(GET, "/api/user/:id", this::user),
                restCall(POST, "/api/user", this::newUser),
                restCall(PUT, "/api/user", this::updateUser),
                restCall(DELETE, "/api/user/:id", this::delete),
                restCall(GET, "/api/user/current-state/:id", this::currentState)
        ).withAutoAcl(true);
    }
}
