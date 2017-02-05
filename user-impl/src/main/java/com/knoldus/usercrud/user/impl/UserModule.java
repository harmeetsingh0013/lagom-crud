package com.knoldus.usercrud.user.impl;

import com.google.inject.AbstractModule;
import com.knoldus.usercrud.user.api.UserService;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

/**
 * Created by harmeet on 30/1/17.
 */
public class UserModule extends AbstractModule implements ServiceGuiceSupport {

    @Override
    protected void configure() {
        bindServices(serviceBinding(UserService.class, UserServiceImpl.class));
    }
}
