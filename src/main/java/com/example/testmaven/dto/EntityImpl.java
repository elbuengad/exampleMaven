package com.example.testmaven.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Map;


public class EntityImpl extends BaseEntity implements Serializable {

    private Long creationTime;

    public EntityImpl(@JsonProperty("id") String ID) {
        super(ID);
        setCreationTime(System.currentTimeMillis());
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        EntityImpl entity = (EntityImpl) o;

        return creationTime.equals(entity.creationTime);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + creationTime.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "EntityImpl{" +
                "creationTime=" + creationTime + super.toString() +
                '}';
    }

    @Override
    public synchronized Map getData() {
        Map entityAsDict = super.getData();
        entityAsDict.put("creationTime",getCreationTime());
        return entityAsDict;
    }
}
