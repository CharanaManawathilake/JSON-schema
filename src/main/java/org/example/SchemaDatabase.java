package org.example;

import java.util.HashMap;
import java.util.Map;

public class SchemaDatabase {
    // Mapping the id to schema.
    public static Map<String, Schema> schemaList = new HashMap<>();

    // Mapping the id to created ballerina data types.
    public static Map<String, String> idToTypeMap = new HashMap<>();
}
