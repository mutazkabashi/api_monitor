package com.kry.apimonitor.service;

import com.kry.apimonitor.domain.User;
import com.kry.apimonitor.verticle.DatabaseVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Mutaz Hussein Kabashi
 */
@ExtendWith(VertxExtension.class)
public class UserServiceImplTest {

    private UserService userService;
    private String databaseVerticleId = "";

    @BeforeEach
    void prepare(Vertx vertx, VertxTestContext ctx) {
        vertx.deployVerticle(new DatabaseVerticle(), ctx.succeeding(id -> {
            userService = UserService.createProxy(vertx, "db_service.userService");
            databaseVerticleId = id;
            ctx.completeNow();
        }));
    }

    //https://dev.to/sip3/how-to-write-beautiful-unit-tests-in-vert-x-2kg7

    @Test
    @Order(1)
    void testInsert(Vertx vertx, VertxTestContext ctx) {
        Checkpoint checkUserEmail = ctx.checkpoint();
        JsonObject m1 = new JsonObject()
                .put("email", "mutaz2@gmail.com")
                .put("firstName", "Mutaz").put("lastName", "Abdelgadir").put("password", "12345");
        JsonObject m2 = new JsonObject()
                .put("body", m1);
        DeliveryOptions options = new DeliveryOptions();
        options.addHeader("action", "insertUser");
        OperationRequest context = new OperationRequest();
        context.setHeaders(options.getHeaders());
        context.setParams(m2);
        JsonObject mBody = new JsonObject()
                .put("context", context.toJson());
        vertx.eventBus()
                .publish("db_service.userService", mBody, options);

        User user = new User();
        user.setEmail("mutaz2@gmail.com");
        userService.findUser(user, context, ctx.succeeding(data -> ctx.verify(() -> {
            assertThat(getEmail(data.getPayload().toJson().toString())).isEqualTo("mutaz2@gmail.com");
            checkUserEmail.flag();

        })));

    }

    @Test
    @Order(2)
    void testInsertWithNullEmail(Vertx vertx, VertxTestContext ctx) {
        Checkpoint checkListSize = ctx.checkpoint();
        User user = new User(null, "Mutaz", "Abdelgadir", "123456");
        JsonObject userAsJson = createUserAsAJsonObject(user);
        OperationRequest context = userOperation(vertx, userAsJson, "insertUser");//new OperationRequest();
        User expectedUser = new User();
        expectedUser.setEmail(null);
        userService.findUser(expectedUser, context, ctx.failing(e -> ctx.verify(() -> {
            assertThat(e == null);
            checkListSize.flag();
        })));
    }

    @Test
    @Order(3)
    void testInsertWithNullFirstName(Vertx vertx, VertxTestContext ctx) {
        Checkpoint checkListSize = ctx.checkpoint();
        User user = new User("mutaz3@gmail.com", null, "Abdelgadir", "123456");
        JsonObject userAsJson = createUserAsAJsonObject(user);
        userOperation(vertx, userAsJson, "insertUser");
        OperationRequest context = new OperationRequest();
        userService.findUser(user, context, ctx.failing(e -> ctx.verify(() -> {
            assertThat(e == null);
            checkListSize.flag();
        })));
    }

    @Test
    @Order(4)
    void testInsertWithEmptyFirstName(Vertx vertx, VertxTestContext ctx) {
        Checkpoint checkListSize = ctx.checkpoint();
        User user = new User("mutaz2@gmail.com", "", "Abdelgadir", "123456");
        JsonObject userAsJson = createUserAsAJsonObject(user);
        userOperation(vertx, userAsJson, "insertUser");
        OperationRequest context = new OperationRequest();
        userService.findUser(user, context, ctx.failing(e -> ctx.verify(() -> {
            assertThat(e == null);
            checkListSize.flag();
        })));
    }

    @Test
    @Order(5)
    void testInsertWithNullLastName(Vertx vertx, VertxTestContext ctx) {
        Checkpoint checkListSize = ctx.checkpoint();
        User user = new User("mutaz2@gmail.com", "Mutaz", null, "123456");
        JsonObject userAsJson = createUserAsAJsonObject(user);
        userOperation(vertx, userAsJson, "insertUser");
        OperationRequest context = new OperationRequest();
        userService.findUser(user, context, ctx.failing(e -> ctx.verify(() -> {
            assertThat(e == null);
            checkListSize.flag();
        })));
    }

    @Test
    @Order(6)
    void testInsertWithNullPassword(Vertx vertx, VertxTestContext ctx) {
        Checkpoint checkListSize = ctx.checkpoint();
        User user = new User("mutaz2@gmail.com", "Mutaz", "Abdelgadir", null);
        JsonObject userAsJson = createUserAsAJsonObject(user);
        userOperation(vertx, userAsJson, "insertUser");
        OperationRequest context = new OperationRequest();
        userService.findUser(user, context, ctx.failing(e -> ctx.verify(() -> {
            assertThat(e == null);
            checkListSize.flag();
        })));
    }

    @AfterEach
    void tearDown(Vertx vertx, VertxTestContext ctx) {
        vertx.undeploy(databaseVerticleId, ctx.succeeding(id -> {
            ctx.completeNow();
        }));

    }

    //util method
    private String getEmail(String json) {
        return json.substring(json.indexOf("email:") + 11, json.indexOf(",") - 1);
    }

    /**
     * this method will be used by different methods that why we create method for it
     *
     * @param user
     * @return
     */
    private JsonObject createUserAsAJsonObject(User user) {
        JsonObject UserAsJson = new JsonObject()
                .put("email", user.getEmail())
                .put("firstName", user.getFirstName())
                .put("lastName", user.getLastName())
                .put("password", user.getPassword());
        ;
        if (user.getId() != null && user.getId() > 0) {
            UserAsJson.put("id", user.getId());
        }
        return UserAsJson;
    }

    /**
     * we create this method to be used by insert/update/delete webservice, and instead of just copy and past
     * the same code to all these methods we just put the common code here
     */
    private OperationRequest userOperation(Vertx vertx, JsonObject userAsJson, String operation) {

        JsonObject requestParameters = new JsonObject()
                .put("body", userAsJson);
        DeliveryOptions options = new DeliveryOptions();
        options.addHeader("action", operation);
        OperationRequest context = new OperationRequest();
        context.setHeaders(options.getHeaders());
        context.setParams(requestParameters);
        JsonObject mBody = new JsonObject()
                .put("context", context.toJson());
        vertx.eventBus()
                .publish("db_service.userService", mBody, options);
        return context;
    }

}
