package com.example.testmaven.dto;

import java.util.Map;
import java.util.Set;

interface Entity {
    // Returns a unique identifier
    String getID();
    // Returns the sub-entities of this entity
    Set getSubEntities();
    // Returns a set of key-value data belonging to this entity
    Map getData();
}