package com.example.testmaven.http;

import com.example.testmaven.business.EntityBusiness;
import com.example.testmaven.dto.EntityImpl;
import com.example.testmaven.utils.Constants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.api.RequestParameters;
import io.vertx.ext.web.api.validation.HTTPRequestValidationHandler;
import io.vertx.ext.web.api.validation.ValidationException;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Gadiel
 * @apiNote HTTP Server Class using VertX with API validation
 */
public class Server extends AbstractVerticle {

    private static final Logger logger
            = LoggerFactory.getLogger(Server.class);

    //Singleton providing in memory storage
    EntityBusiness entityBusiness = new EntityBusiness();

    private static final int HTTP_STATUS_OK = 200;
    private static final int HTTP_STATUS_BAD_REQUEST = 400;
    private static final int HTTP_STATUS_SERVER_ERROR = 500;
    private static final String HTTP_HEADER_X_FORWARDED_FOR = "X-FORWARDED-FOR";
    private static final String HTTP_HEADER_CONTENT_TYPE = "content-type";
    private static final String HTTP_CONTENT_TYPE_JSON = "application/json";
    private static final String HEALTH_CHECK_JSON = "{\"status\":\"UP\"}";
    private static final String VERTX_BODY_PARAMS_MAP_KEY = "parsedParameters";


    @Override
    public void start() throws Exception {

        Router router = Router.router(vertx);

        //Set max body size
        router.route().handler(BodyHandler.create().setBodyLimit(Constants.HTTP_POST_MAX_BODY_SIZE));

        //TODO: Set max header/request size

        //Get callers information on all requests
        router.route().handler(routingContext -> {
            logger.debug(routingContext.normalisedPath() + "-" + routingContext.request().getHeader(HTTP_HEADER_X_FORWARDED_FOR));
            routingContext.response().setChunked(true);
            routingContext.next();
        });

        router.get("/status")
                .handler((routingContext -> {
                    routingContext.response()
                            .setStatusCode(HTTP_STATUS_OK)
                            .putHeader(HTTP_HEADER_CONTENT_TYPE,HTTP_CONTENT_TYPE_JSON)
                            .end(HEALTH_CHECK_JSON);
                }));

        router.get("/list")
                .handler((routingContext) -> {
                    routingContext.response()
                            .putHeader(HTTP_HEADER_CONTENT_TYPE,HTTP_CONTENT_TYPE_JSON)
                            .write(
                                    Json.encodePrettily(entityBusiness.listEntities()))
                            .setStatusCode(HTTP_STATUS_OK)
                            .end();
                })
                .failureHandler((routingContext) -> {
                    Throwable failure = routingContext.failure();
                    logger.warn(failure.getMessage());
                    routingContext.response().setStatusCode(HTTP_STATUS_SERVER_ERROR).end();

                });

        //TODO: DEV-ONLY
        router.get("/populate")
                .handler((routingContext) -> {
                    routingContext.response()
                            .putHeader(HTTP_HEADER_CONTENT_TYPE,HTTP_CONTENT_TYPE_JSON)
                            .write(
                                    Json.encodePrettily(entityBusiness.addMockEntity()))
                            .setStatusCode(HTTP_STATUS_OK)
                            .end();
                })
                .failureHandler((routingContext) -> {
                    Throwable failure = routingContext.failure();
                    logger.warn(failure.getMessage());
                    routingContext.response().setStatusCode(HTTP_STATUS_SERVER_ERROR).end();
                });

        router.post("/read")
                .handler(HTTPRequestValidationHandler.create().addJsonBodySchema(Constants.JSON_SCHEMA_READ_REQUEST))
                .handler((routingContext -> {
                    RequestParameters params = routingContext.get(VERTX_BODY_PARAMS_MAP_KEY);
                    routingContext.response()
                            .putHeader(HTTP_HEADER_CONTENT_TYPE,HTTP_CONTENT_TYPE_JSON)
                            .write(Json.encodePrettily(entityBusiness.readEntity(params.body().getJsonObject().getString("id"))))
                            .setStatusCode(HTTP_STATUS_OK)
                            .end();
                }))
                .failureHandler((routingContext) -> {
                    Throwable failure = routingContext.failure();
                    if (failure instanceof ValidationException) {
                        // Improper object
                        logger.warn(failure.getMessage());
                        routingContext.response().setStatusCode(HTTP_STATUS_BAD_REQUEST).end();
                    } else {
                        logger.warn(failure.getMessage());
                        routingContext.response().setStatusCode(HTTP_STATUS_SERVER_ERROR).end();
                    }
                });

        router.post("/send")
                .handler(HTTPRequestValidationHandler.create().addJsonBodySchema(Constants.JSON_SCHEMA_SEND_REQUEST))
                .handler((routingContext -> {
                    RequestParameters params = routingContext.get(VERTX_BODY_PARAMS_MAP_KEY);
                    entityBusiness.saveEntity(params.body().getJsonObject().mapTo(EntityImpl.class));
                    routingContext.response().setStatusCode(HTTP_STATUS_OK).end();
                }))
                .failureHandler((routingContext) -> {
                    Throwable failure = routingContext.failure();
                    if (failure instanceof ValidationException) {
                        // Improper object
                        logger.warn(failure.getMessage());
                        routingContext.response().setStatusCode(HTTP_STATUS_BAD_REQUEST).end();
                    } else {
                        logger.warn(failure.getMessage());
                        routingContext.response().setStatusCode(HTTP_STATUS_SERVER_ERROR).end();
                    }
                });

        vertx.createHttpServer().requestHandler(router).listen(Constants.SERVER_PORT);

    }

}
