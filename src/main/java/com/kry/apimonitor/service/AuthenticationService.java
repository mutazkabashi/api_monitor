package com.kry.apimonitor.service;


import com.kry.apimonitor.domain.User;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;
import io.vertx.ext.web.api.generator.WebApiServiceGen;

/**
 * Authentication service which is responsible for authentication operations such as
 * AuthenticateUser (for signin)
 *
 * @author Mutaz Hussein Kabashi
 * @version 1.0
 */
@WebApiServiceGen
@ProxyGen
public interface AuthenticationService {


    /**
     * A method  responsible for authentication/signin
     *
     * @param body
     * @param context
     * @param resultHandler
     */
    void AuthenticateUser(User body, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler);

    static AuthenticationService create(Vertx vertx) {

        return new AuthenticationServiceImpl(vertx);
    }

    static AuthenticationService createProxy(Vertx vertx, String address) {
        return new AuthenticationServiceVertxEBProxy(vertx, address);
    }


}
