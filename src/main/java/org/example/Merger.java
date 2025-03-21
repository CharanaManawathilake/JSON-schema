package org.example;

import java.util.*;

public class Merger {
    public static Schema unionOfSchemas(Schema schema1, Schema schema2){
        //TODO: This will combine 2 schemas. Will perform the function of AllOf combination for non conflicting items.
        Schema schema = new Schema();


        return schema1;
    }

    public static Schema factorizeASchema(Schema schema){
        //TODO: This will factorize a schema. Only the common parts (not the combination of properties)
        return new Schema();
    }

    public static Schema factorize(List<Object> schemas){
        return new Schema();
    }
}
