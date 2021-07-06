package com.kry.apimonitor.verticle;

import java.util.Properties;

import com.kry.apimonitor.service.APIServiceImpl;
import com.kry.apimonitor.service.ApiService;
import com.kry.apimonitor.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.serviceproxy.ServiceBinder;

/**
 * a vert.x Verticle, which is responsilobe for debploying {@link APIServiceImpl}
 *
 * @author Mutaz Hussein Kabashi
 * @version 1.0
 */
public class ApiVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiVerticle.class);

    ServiceBinder serviceBinder;
    MessageConsumer<JsonObject> consumer;
    WebClient webClient;

    public ApiVerticle() {
        super();
    }

    @Override
    public void start(Promise<Void> promise) throws Exception {
        try {
            serviceBinder = new ServiceBinder(vertx);
            webClient = WebClient.create(vertx);
            LOGGER.debug("Webclient has been created succefully");
            // Create an instance of WebClient and mount to event bus
            //FIXME we pass vertx since we need it to create multiple instance of webclient inside APIService
            // am not sure about that may be am wrong
            ApiService apiService = ApiService.create(webClient, vertx);
            LOGGER.debug("ApiService has been created successfully");
            consumer = serviceBinder
                    //FIXME address should be in the config file
                    .setAddress("api_service")
                    .register(ApiService.class, apiService);
            LOGGER.debug("ApiService has been registred successfully to  Event Bus");
            AuthenticationService authenticationService = AuthenticationService.create(vertx);
            LOGGER.debug("authenticationService has been created successfully");
            consumer = serviceBinder
                    //FIXME address should be in the config file
                    .setAddress("authentication")
                    .register(AuthenticationService.class, authenticationService);
            LOGGER.debug("authenticationService has been registred successfully to  Event Bus");
            // promise to check if database startup is succefull or not
            promise.complete();
            LOGGER.info("API Satarted Successfully");
        } catch (Exception ex) {
            promise.fail(ex.getCause());
            LOGGER.error("Could not start the api service");
            throw new Exception(ex);
        }
    }

    @Override
    public void stop() throws Exception {
        consumer.unregister();
        LOGGER.info("Unreagterster from Event Bus completed successfully");

    }
}
