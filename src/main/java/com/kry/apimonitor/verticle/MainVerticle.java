package com.kry.apimonitor.verticle;

import io.vertx.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Vert.x verticle which is responsible for starting and deploying application's verticles
 *
 * @author Mutaz Hussein Kabashi
 * @version 1.0
 */
public class MainVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);
    List<String> verticlesIds = new ArrayList<>();

    //Blocking method but we need it to make sure the config file is existed
    // https://www.bookstack.cn/read/guide-for-java-devs/spilt.4.spilt.3.guide-for-java-devs
    public void start(Promise<Void> promise) throws Exception {
        Promise<String> dbVerticleDeployment = Promise.promise();
        Promise<String> httpVerticleDeployment = Promise.promise();
        Promise<String> apiVerticleDeployment = Promise.promise();
        Verticle dbVerticle = new DatabaseVerticle();
        Verticle httpVerticle = new HttpServerVerticle();
        Verticle apiVerticle = new ApiVerticle();

        vertx.deployVerticle(dbVerticle, dbVerticleDeployment);
        dbVerticleDeployment.future().compose(dbVerticleid ->
        {
            verticlesIds.add(dbVerticleid); //dbverticleid
            vertx.deployVerticle(httpVerticle, httpVerticleDeployment);
            return httpVerticleDeployment.future().compose(httpVerticleId ->
            {
                verticlesIds.add(httpVerticleId); //httpverticleid
                vertx.deployVerticle(apiVerticle, apiVerticleDeployment);
                return apiVerticleDeployment.future().compose(apiVerticleId ->
                {
                    verticlesIds.add(apiVerticleId); //httpverticleid

                    return Future.succeededFuture();
                });
            });
        })
                .onSuccess((result) ->
                {
                    LOGGER.info("Application Statred Successfully");
                    promise.complete();
                })
                .onFailure((ex) -> {
                    LOGGER.error("An Exception Occurred while starting the Application ");
                    promise.fail(ex.getCause());
                });
    }

    /**
     * This method closes the http server and unregister all services loaded to
     * Event Bus
     */
    @Override
    public void stop() {
        //TODO undeploy Deplyed Vertciles
        for (int i = verticlesIds.size() - 1; i >= 0; i--) {
            vertx.undeploy(verticlesIds.get(i), (id) ->
            {
                LOGGER.info("a verticle has been undeployed successfully");
            });
        }

    }


}
