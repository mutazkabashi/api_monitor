package com.kry.apimonitor.service;

import com.kry.apimonitor.domain.WebService;
import io.vertx.core.*;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

/**
 * An implemenation to {@link ApiService}
 *
 * @author Mutaz Hussein Kabashi
 * @version 1.0
 */
public class APIServiceImpl implements ApiService {


    private static final Logger LOGGER = LoggerFactory.getLogger(APIServiceImpl.class);
    private WebClient webClient;
    private final Vertx vertx;

    public APIServiceImpl(WebClient webClient, Vertx vertx) {
        this.webClient = webClient;
        this.vertx = vertx;

    }

    @Override
    public void getApiStatus(OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
        LOGGER.info("get API web services  which are saved/inserted into the Database ");
        JsonArray apisResultStatus = new JsonArray();
        WebServiceService webServiceService = WebServiceService.createProxy(vertx, "db_service.webService");
        //FIXME BLOCK
        webServiceService.findWebServicesByUser(context, ar -> {
            if (ar.succeeded()) {
                //Close/release web client connection first
                JsonArray apis = ar.result().getPayload().toJsonArray();
                int apisSize = 0;
                for (Object object : apis) {
                    LOGGER.debug("api " + object);
                    WebService webservice = new WebService((JsonObject) object);
                    Promise<Void> promise = Promise.promise();
                    callWebService(webservice, apisResultStatus, promise);
                    promise.future().setHandler(asyncResult -> {
                        if (asyncResult.succeeded() && apisResultStatus.size() == apis.size()) {
                            resultHandler.handle(Future.succeededFuture(OperationResponse.completedWithJson(apisResultStatus)));
                        } else if (asyncResult.failed() && apisResultStatus.size() == apis.size()) {
                            resultHandler.handle(Future.succeededFuture(OperationResponse.completedWithJson(apisResultStatus)));
                        }
                    });

                }
            } else {
                LOGGER.error("Exception while getting apis from Datbase ", ar.cause());
                resultHandler.handle(Future.failedFuture(ar.cause()));
            }
        });
        LOGGER.info("insertion completed succefully");
        // FIXME should we return the record or just uccuess message

    }

    /**
     * call passed webservice , and add the call result/repsonse to the passed apiResultStatus array
     * .Moreover it mark the promise paramater as complete/failed after each call
     *
     * @param webService       : webservice to call
     * @param apisResultStatus : JsonArray which hold the webservices status (up/Down)
     * @param promise          : promise which will be marked as complete/failed after each web service call
     */
    private void callWebService(WebService webService, JsonArray apisResultStatus, Promise promise) {
		if(!webService.getUrl().contains("?")){
			webserviceWithoutParameters(webService,apisResultStatus ,promise);
		}
		else{
			webserviceWithParameters(webService,apisResultStatus ,promise);
		}
    }

    /**
     * TODO could be moved to utility/helper class
     * This method Handles Rest-api GET service, which dosnt needs Parameters
     * @param webService
     * @param apisResultStatus
     * @param promise
     */
    private void webserviceWithoutParameters(WebService webService, JsonArray apisResultStatus, Promise promise) {
        WebClient webClient = WebClient.create(vertx);
        webClient.getAbs(webService.getUrl()).send(ar -> {
            if (ar.succeeded()) {
                HttpResponse<Buffer> response = ar.result();
                LOGGER.info("result from webservice " + webService.getUrl() + "  " + response.statusCode());
                String result = "{\"name\":\"" + webService.getName() + "\",\"url\":\"" + webService.getUrl() + "\",\"statusCode\":\"" + response.statusCode() + "\"}";
                LOGGER.debug("result " + result);
                apisResultStatus.add(new JsonObject(result));
                LOGGER.debug("result2 " + result + apisResultStatus);
                webClient.close();
                promise.complete();
                LOGGER.debug("result3 " + result);
            } else {
                String result = "{\"name\":\"" + webService.getName() + "\",\"url\":\"" + webService.getUrl() + "\",\"statusCode\":\"" + " No Response/Down " + "\"}";
                apisResultStatus.add(new JsonObject(result));
                LOGGER.error("Exception while calling rest api ", ar.cause());
                promise.fail(ar.cause());
            }
        });
    }

    /**
     * TODO could be moved to utility/helper class
     * This method Handles Rest-api GET service, which  needs Parameters
     * @param webService
     * @param apisResultStatus
     * @param promise
     */
    private void webserviceWithParameters(WebService webService, JsonArray apisResultStatus, Promise promise) {
        String url = webService.getUrl().substring(0, webService.getUrl().indexOf("?"));
        String parameter = getWebserviceParameter(webService);
        String value = getWebserviceParameterValue(webService);
        WebClient webClient = WebClient.create(vertx);
        HttpRequest<Buffer> request = webClient.request(HttpMethod.GET,url)
                .putHeader("Content-Type", "application/json")
                .addQueryParam(parameter, value);
        request.send(ar -> {
            if (ar.succeeded()) {
                HttpResponse<Buffer> response = ar.result();
                LOGGER.info("result from webservice " + webService.getUrl() + "  " + response.statusCode()+" response "+response.bodyAsString());
                String result = "{\"name\":\"" + webService.getName() + "\",\"url\":\"" + webService.getUrl() + "\",\"statusCode\":\"" + ar.result().statusCode() + "\"}";
                LOGGER.debug("webservice call response " + result);
                apisResultStatus.add(new JsonObject(result));
                LOGGER.debug("Webservices response list after adding the current webservice call response " + result + apisResultStatus);
                webClient.close();
                promise.complete();
            } else {
                String result = "{\"name\":\"" + webService.getName() + "\",\"url\":\"" + webService.getUrl() + "\",\"statusCode\":\"" + " No Response/Down " + "\"}";
                apisResultStatus.add(new JsonObject(result));
                LOGGER.error("Exception while calling rest api ", ar.cause());
                promise.fail(ar.cause());
            }
        });
    }


    private String getWebserviceParameter(WebService webservice) {
        String url = webservice.getUrl();
        return url.substring(url.indexOf("?") + 1, url.lastIndexOf("=")).trim();
    }

    private String getWebserviceParameterValue(WebService webservice) {
        String url = webservice.getUrl();
        return url.substring(url.indexOf("=") + 1).trim();
    }

}
