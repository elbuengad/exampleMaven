package com.example.testmaven.business;

import com.example.testmaven.dto.EntityImpl;
import com.example.testmaven.http.Server;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Entity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EntityBusiness {

    private static final Logger logger = LoggerFactory.getLogger(EntityBusiness.class);
    private Map<String, EntityImpl> entityMap = new ConcurrentHashMap();

    //TODO: DEV-ONLY
    public Collection addMockEntity() {
        EntityImpl entity = new EntityImpl(Long.toHexString(System.currentTimeMillis()));
        entity.setCreationTime(System.currentTimeMillis());
        entityMap.put(entity.getID(), entity);
        return listEntities();
    }

    public Collection listEntities() {
        return entityMap.values();
    }

    // Create, Update Operations.
    //To add a subEntity it is enough to send the parent object (same ID) with the new subEntity added.
    //IMPORTANT. This method MUST match requirements and design criteria.
    public void saveEntity(EntityImpl entity) {

        logger.info("Saving entity with ID:" + entity.getID());

        //First we try to find the element at root level
        if (entityMap.containsKey(entity.getID())) {
            if (!entityMap.get(entity.getID()).equals(entity)) {
                //Same root object ID, update subEntities if new or different (view addSubEntity method)
                EntityImpl newEntity = entityMap.get(entity.getID());
                if (!entity.getSubEntities().isEmpty() && entity.getSubEntities().size()>0)
                    entity.getSubEntities().stream().forEach(newEntity::addSubEntity);
                entityMap.replace(entity.getID(), newEntity);
                logger.info("SubEntities updated for entity with ID:" + entity.getID());
            } else
                //Same root element, NOP
                logger.info("Duplicate entity, NOP for entity with ID: " + entity.getID());
        } else {
            //New object, insert
            entityMap.put(entity.getID(), entity);
            logger.info("New entity add with ID:"+entity.getID());
        }
    }

    public Map readEntity(String ID) {
        logger.info("Reading entity with ID:"+ID);
        if (entityMap.containsKey(ID))
            return entityMap.get(ID).getData();
        else
            return new HashMap();
    }

}
