package com.kry.apimonitor.service;

import com.kry.apimonitor.verticle.DatabaseVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

import com.kry.apimonitor.domain.WebService;

/**
 * @author  Mutaz Hussein Kabashi
 */
@ExtendWith(VertxExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WebServiceServiceImplTest {


    private WebServiceService webServiceService;
    private String databaseVerticleId = "";

    @BeforeEach
    void prepare(Vertx vertx, VertxTestContext ctx) {
        vertx.deployVerticle(new DatabaseVerticle(), ctx.succeeding(id -> {
            webServiceService = WebServiceService.createProxy(vertx, "db_service.webService");
            databaseVerticleId = id;
            ctx.completeNow();
        }));
    }

    @Test
    @Order(1)
    void testInsert(Vertx vertx, VertxTestContext ctx) {
        Checkpoint checkWebServiceName = ctx.checkpoint();
        Checkpoint checkListSize = ctx.checkpoint();
        WebService webService = new WebService("https://catfact.ninja/fact", "Cat Facts", "GET");
        JsonObject webServiceAsJson = createWebServiceAsAJsonObject(webService);
        OperationRequest context = webserviceOperation(vertx, webServiceAsJson, "insert");
        webServiceService.findAll(context, ctx.succeeding(data -> ctx.verify(() -> {
            assertThat(data.getPayload().toJsonArray().getJsonObject(0).getString("name")).isEqualTo("Cat Facts");
            checkWebServiceName.flag();
            assertThat(data.getPayload().toJsonArray().size()).isEqualTo(1);
            checkListSize.flag();
        })));
    }

    @Test
    @Order(2)
    /**
     * FIXME here we are tsing that if we insert non valid user record to the database
     * it wont be saved and the numbers of records on the database will be 1 instead of2 , the beter way is to check exception
     * thrown by WebServiceService, unfournately VertxTestContext could.t catch the excpetion thrown by the user service
     * thats why we just check the number of records in the user table to make sure the inavlid record hasnet
     * been saved and thuus exception has been thworn
     */
    void testInsertWithNullUrl(Vertx vertx, VertxTestContext ctx) {
        Checkpoint checkListSize = ctx.checkpoint();
        WebService webService = new WebService("https://catfact.ninja/fact", "Cat Facts", "GET");
        webService.setUrl(null);
        JsonObject webServiceAsJson = createWebServiceAsAJsonObject(webService);
        OperationRequest context = webserviceOperation(vertx, webServiceAsJson, "insert");
        webServiceService.findAll(context, ctx.succeeding(data -> ctx.verify(() -> {
            assertThat(data.getPayload().toJsonArray().size()).isEqualTo(1);
            checkListSize.flag();
        })));
    }

    @Test
    @Order(3)
    /**
     * FIXME here we are tsing that if we insert non valid user record to the database
     * it wont be saved and the numbers of records on the database will be 1 instead of2 , the beter way is to check exception
     * thrown by WebServiceService, unfournately VertxTestContext could.t catch the excpetion thrown by the user service
     * thats why we just check the number of records in the user table to make sure the inavlid record hasnet
     * been saved and thuus exception has been thworn
     */
    void testInsertWithNullName(Vertx vertx, VertxTestContext ctx) {
        Checkpoint checkListSize = ctx.checkpoint();
        WebService webService = new WebService("https://catfactninjafact", "Cat Facts", "GET");
        webService.setName(null);
        JsonObject webServiceAsJson = createWebServiceAsAJsonObject(webService);
        OperationRequest context = webserviceOperation(vertx, webServiceAsJson, "insert");
        webServiceService.findAll(context, ctx.succeeding(data -> ctx.verify(() -> {
            assertThat(data.getPayload().toJsonArray().size()).isEqualTo(1);
            checkListSize.flag();
        })));
    }

    @Test
    @Order(4)
    /**
     * FIXME here we are tsing that if we insert non valid user record to the database
     * it wont be saved and the numbers of records on the database will be 1 instead of2 , the beter way is to check exception
     * thrown by WebServiceService, unfournately VertxTestContext could.t catch the excpetion thrown by the user service
     * thats why we just check the number of records in the user table to make sure the inavlid record hasnet
     * been saved and thuus exception has been thworn
     */
    void testInsertWithNullMethod(Vertx vertx, VertxTestContext ctx) {
        Checkpoint checkListSize = ctx.checkpoint();
        WebService webService = new WebService("https://catfactninjafact", "Cat Facts2", "GET");
        webService.setMethod(null);
        JsonObject webServiceAsJson = createWebServiceAsAJsonObject(webService);
        OperationRequest context = webserviceOperation(vertx, webServiceAsJson, "insert");
        webServiceService.findAll(context, ctx.succeeding(data -> ctx.verify(() -> {
            assertThat(data.getPayload().toJsonArray().size()).isEqualTo(1);
            checkListSize.flag();
        })));
    }

    @Test
    @Order(5)
    /**
     * FIXME here we are tsing that if we insert non valid user record to the database
     * it wont be saved and the numbers of records on the database will be 1 instead of2 , the beter way is to check exception
     * thrown by WebServiceService, unfournately VertxTestContext could.t catch the excpetion thrown by the user service
     * thats why we just check the number of records in the user table to make sure the inavlid record hasnet
     * been saved and thuus exception has been thworn
     */
    void testInsertWithDuplicateName(Vertx vertx, VertxTestContext ctx) {
        Checkpoint checkListSize = ctx.checkpoint();
        WebService webService = new WebService("https://catfactninjafact", "Cat Facts", "GET");
        webService.setMethod(null);
        JsonObject webServiceAsJson = createWebServiceAsAJsonObject(webService);
        OperationRequest context = webserviceOperation(vertx, webServiceAsJson, "insert");
        webServiceService.findAll(context, ctx.succeeding(data -> ctx.verify(() -> {
            assertThat(data.getPayload().toJsonArray().size()).isEqualTo(1);
            checkListSize.flag();
        })));
    }

    @Test
    @Order(6)
    void testInsertWithDuplicateUrl(Vertx vertx, VertxTestContext ctx) {
        Checkpoint checkListSize = ctx.checkpoint();
        WebService webService = new WebService("https://catfactninjafact", "Cat Facts", "GET");
        webService.setMethod(null);
        JsonObject webServiceAsJson = createWebServiceAsAJsonObject(webService);
        OperationRequest context = webserviceOperation(vertx, webServiceAsJson, "insert");
        webServiceService.findAll(context, ctx.succeeding(data -> ctx.verify(() -> {
            assertThat(data.getPayload().toJsonArray().size()).isEqualTo(1);
            checkListSize.flag();
        })));
    }

    @Test
    @Order(7)
    void testUpdate(Vertx vertx, VertxTestContext ctx) {
        Checkpoint checkUpdatedWebServiceName = ctx.checkpoint();
        Checkpoint checkListSize = ctx.checkpoint();
        OperationRequest context = new OperationRequest();
        webServiceService.findAll(context, ctx.succeeding(data -> ctx.verify(() -> {
            assertThat(data.getPayload().toJsonArray().getJsonObject(0).getString("name")).isEqualTo("Cat Facts");
            WebService ws = new WebService(data.getPayload().toJsonArray().getJsonObject(0));
            ws.setName("Cat Facts2");
            JsonObject webServiceAsJson = createWebServiceAsAJsonObject(ws);
            webserviceOperation(vertx, webServiceAsJson, "update");
            webServiceService.findAll(context, ctx.succeeding(data2 -> ctx.verify(() -> {
                assertThat(data2.getPayload().toJsonArray().getJsonObject(0).getString("name")).isEqualTo("Cat Facts2");
                checkUpdatedWebServiceName.flag();
            })));
            assertThat(data.getPayload().toJsonArray().size()).isEqualTo(1);
            checkListSize.flag();
        })));
    }

    @Test
    @Order(8)
    void testDelete(Vertx vertx, VertxTestContext ctx) {
        Checkpoint checkListSize = ctx.checkpoint();
        WebService webService = new WebService("https://catfact.ninja/fact", "Cat Facts", "GET");
        webService.setId(1);
        JsonObject webServiceAsJson = createWebServiceAsAJsonObject(webService);
        OperationRequest context = new OperationRequest();
        webserviceOperation(vertx, webServiceAsJson, "deleteWebService");
        webServiceService.findAll(context, ctx.succeeding(data -> ctx.verify(() -> {
            assertThat(data.getPayload().toJsonArray().size()).isEqualTo(0);
            checkListSize.flag();
        })));
    }

    @AfterEach
    void tearDown(Vertx vertx, VertxTestContext ctx) {
        vertx.undeploy(databaseVerticleId, ctx.succeeding(id -> {
            ctx.completeNow();
        }));
    }

    //util methods

    /**
     * this method will be used by different methods that why we create method for it
     *
     * @param webService
     * @return
     */
    private JsonObject createWebServiceAsAJsonObject(WebService webService) {
        JsonObject WebServiceAsJson = new JsonObject()
                .put("url", webService.getUrl())
                .put("name", webService.getName()).put("method", webService.getMethod());
        if (webService.getId() != null && webService.getId() > 0) {
            WebServiceAsJson.put("id", webService.getId());
        }
        return WebServiceAsJson;
    }

    /**
     * we create this method to be used by insert/update/delete webservice, and instead of just copy and past
     * the same code to all these methods we just put the common code here
     */
    private OperationRequest webserviceOperation(Vertx vertx, JsonObject WebServiceAsJson, String operation) {

        JsonObject requestParameters = new JsonObject()
                .put("body", WebServiceAsJson);
        DeliveryOptions options = new DeliveryOptions();
        options.addHeader("action", operation);
        OperationRequest context = new OperationRequest();
        context.setHeaders(options.getHeaders());
        context.setParams(requestParameters);
        JsonObject mBody = new JsonObject()
                .put("context", context.toJson());
        vertx.eventBus()
                .publish("db_service.webService", mBody, options);
        return context;
    }

}
