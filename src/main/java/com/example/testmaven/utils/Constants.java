package com.example.testmaven.utils;

import io.vertx.core.VertxOptions;

/**
 * @apiNote Default configuration values.
 * @implNote IMPORTANT. These values may be overriden by properties file
 * @author Gadiel
 *
 */
public class Constants {

    public static int SERVER_PORT = 8080;
    public static int VERTX_WORKER_POOL_SIZE = VertxOptions.DEFAULT_WORKER_POOL_SIZE;
    public static long MAX_WORKER_EXECUTE_TIME = VertxOptions.DEFAULT_MAX_WORKER_EXECUTE_TIME;
    public static long HTTP_POST_MAX_BODY_SIZE = 2048L;

    /**
     *
     *   {
     * 		"definitions": {
     * 			"entityImpl": {
     * 				"type": "object",
     * 				"properties": {
     * 					"id": {
     * 						"type": "string"
     *                    },
     * 					"creationTime": {
     * 						"type": "long"
     *                    },
     * 					"subEntities": {
     * 						"type": "array"
     *                    }
     *                },
     * 				"required":["id"]
     *            }
     *        }
     *    }
     * */
    public static String JSON_SCHEMA_SEND_REQUEST = "{\"definitions\":{\"entityImpl\":{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"string\"},\"creationTime\":{\"type\":\"long\"},\"subEntities\":{\"type\":\"array\"}},\"required\":[\"id\"]}}}";

    /**
     *        {
     * 		"type": "object",
     * 		"properties": {
     * 			"id": {
     * 				"type": "string"
     *            }
     *        },
     * 		"required":["id"]
     *    }
     */
    public static String JSON_SCHEMA_READ_REQUEST = "{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"string\"}},\"required\":[\"id\"]}";
}

