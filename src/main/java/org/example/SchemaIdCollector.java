package org.example;

import java.util.*;

public class SchemaIdCollector {
    private static void visitObject(Object attribute, Schema schema) {
        if (true) {}
    }

    private static void visitMapStringObject(Map<String, Object> attribute, Schema schema) {

    }

    public static void schemaIdCollector(Schema schema){
        if (schema.get$id() != null) {
            SchemaDatabase.schemaList.put(schema.get$id(), schema);
        }

        if (schema.getPrefixItems() != null) {
            for (Object item : schema.getPrefixItems()) {
                if (item instanceof Schema) {
                    schemaIdCollector((Schema) item);  // Safe cast
                }
            }
        }

        if (schema.getItems() != null && schema.getItems() instanceof Schema) {
            schemaIdCollector((Schema) schema.getItems());
        }
//        Object contains,
//        Object additionalProperties,
//        Map<String, Object> properties,
//        Map<String, Object> patternProperties,
//        Map<String, Object> dependentSchema,
//        Object propertyNames,
//        Object ifKeyword,
//        Object then,
//        Object elseKeyword,
//        List<Object> allOf,
//        List<Object> oneOf,
//        List<Object> anyOf,
//        Object not,
//        String contentEncoding,
//        String contentMediaType,
//        Object content,
//        String $id,
//        String $schema,
//        String $ref,
//        String $anchor,
//        String $dynamicRef,
//        String $dynamicAnchor,
//        String $vocabulary,
//        String $comment,
//        Map<String, Object> $defs,
//        String format,
//        String title,
//        String description,
//        Object defaultKeyword,
//        Boolean deprecated,
//        Boolean readOnly,
//        Boolean writeOnly,
//        List<Object> examples,
//        Object unevaluatedItems,
//        Object unevaluatedProperties,
//        ArrayList<String> type,
//        Object constKeyword,
//        ArrayList<Object> enumKeyword,
//        Double multipleOf,
//        Double maximum,
//        Double exclusiveMaximum,
//        Double minimum,
//        Double exclusiveMinimum,
//        Long maxLength,
//        Long minLength,
//        String pattern,
//        Long maxItems,
//        Long minItems,
//        Boolean uniqueItems,
//        Long maxContains,
//        Long minContains,
//        Long maxProperties,
//        Long minProperties,
//        List<String> required,
//        Map<String, List<String>> dependentRequired
    }
}
