package com.example.testmaven.dto;

import io.vertx.core.impl.ConcurrentHashSet;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseEntity implements Entity, Serializable {

    private String ID;
    private Set entitySet = new ConcurrentHashSet();

    public BaseEntity(String ID) {
        this.ID = ID;
    }

    public synchronized boolean addSubEntity(Object o) {
        if (entitySet.contains(o)) return false;
        entitySet.add(o);
        return true;
    }

    @Override
    public String getID() {
        return this.ID;
    }

    @Override
    public synchronized Set getSubEntities() {
        return this.entitySet;
    }

    @Override
    public synchronized Map getData() {

        Map<String, Object> entityAsDict = new HashMap<>();
        entityAsDict.put("ID", getID());

        //IMPORTANT. Here is vital to understand the kind of objects stored in the Set
        // as well as the postprocessing stage of this data.
        // While I am leaving the simplest scenario which is to only retrieve "as is",
        // it may be needed to dive into the possible several nested objects
        // and/or create a set of template-based objects.
        if (!getSubEntities().isEmpty())
            for (Object o : getSubEntities())
                entityAsDict.put(o.getClass().getCanonicalName() + "_" + o.hashCode(), o);

        return entityAsDict;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseEntity that = (BaseEntity) o;

        if (!ID.equals(that.ID)) return false;
        return entitySet != null ? entitySet.equals(that.entitySet) : that.entitySet == null;
    }

    @Override
    public int hashCode() {
        int result = ID.hashCode();
        result = 31 * result + (entitySet != null ? entitySet.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "ID='" + ID + '\'' +
                ", entitySet=" + (entitySet != null ? entitySet.toString() : "null") +
                '}';
    }
}
