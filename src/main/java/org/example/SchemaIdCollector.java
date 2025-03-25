package org.example;

import java.util.Map;

public class SchemaIdCollector {
    public static void schemaIdCollector(Schema schema, String $id, String idPrefix, Map<String, String> anchors, Map<String, String> dynamicAnchors) {
        //! Prefix id is used to send the id of the parent to the children - for the $refs
        //! $id is used to send the exact id of that specific item

        // Add references to a database.
        SchemaDatabase.schemaList.put($id, schema);

        if (schema.get$id() == null) {
            schema.set$id($id);
        } else {
            idPrefix = schema.get$id(); // Replace the current idPrefix if the id is present
        }

        if (schema.get$anchor() != null) {
            if (anchors.containsKey(schema.get$anchor())) {
                throw new RuntimeException("Anchor " + schema.get$anchor() + " is not unique");
            }
            anchors.put(schema.get$anchor(), $id);
            // TODO: There is a naming convention in JSON Schema.
        }

        if (schema.get$dynamicAnchor() != null) {
            if (dynamicAnchors.containsKey(schema.get$dynamicAnchor())) {
                throw new RuntimeException("Anchor " + schema.get$dynamicAnchor() + " is not unique");
            }
            dynamicAnchors.put(schema.get$dynamicAnchor(), $id);
        }

        if (schema.get$defs() != null) {
            for (Map.Entry<String, Object> entry : schema.get$defs().entrySet()) {
                if (entry.getValue() instanceof Schema) {
                    schemaIdCollector((Schema) entry.getValue(), $id + "/$defs/" + entry.getKey(), idPrefix, anchors, dynamicAnchors);
                }
            }
        }

        if (schema.getItems() != null && schema.getItems() instanceof Schema) {
            schemaIdCollector((Schema) schema.getItems(), $id + "/items", idPrefix, anchors, dynamicAnchors);
        }

        if (schema.getContains() != null && schema.getContains() instanceof Schema) {
            schemaIdCollector((Schema) schema.getContains(), $id + "/contains", idPrefix, anchors, dynamicAnchors);
        }

        if (schema.getAdditionalProperties() != null) {
            if (schema.getAdditionalProperties() instanceof Schema) {
            schemaIdCollector((Schema) schema.getAdditionalProperties(), $id + "/additionalProperties", idPrefix, anchors, dynamicAnchors);
            } else if ((Boolean) schema.getAdditionalProperties()) {
                schema.setAdditionalProperties(null);
            }
        }

        if (schema.getUnevaluatedProperties() != null) {
            if (schema.getUnevaluatedProperties() instanceof Schema) {
                schemaIdCollector((Schema) schema.getUnevaluatedProperties(), $id + "/unevaluatedProperties", idPrefix, anchors, dynamicAnchors);
            } else if ((Boolean) schema.getAdditionalProperties()) {
                schema.setUnevaluatedItems(null);
            }
        }

        if (schema.getProperties() != null) {
            for (Map.Entry<String, Object> entry : (schema.getProperties()).entrySet()) {
                if (entry.getValue() instanceof Schema) {
                    schemaIdCollector((Schema) entry.getValue(), $id + "/properties/" + entry.getKey(), idPrefix, anchors, dynamicAnchors);
                }
            }
        }

        if (schema.getPatternProperties() != null) {
            for (Map.Entry<String, Object> entry : (schema.getPatternProperties()).entrySet()) {
                if (entry.getValue() instanceof Schema) {
                    schemaIdCollector((Schema) entry.getValue(), $id + "/patternProperties/" + entry.getKey(), idPrefix, anchors, dynamicAnchors);
                }
            }
        }

        if (schema.getDependentSchema() != null) {
            for (Map.Entry<String, Object> entry : (schema.getDependentSchema()).entrySet()) {
                if (entry.getValue() instanceof Schema) {
                    schemaIdCollector((Schema) entry.getValue(), $id + "/dependentSchema/" + entry.getKey(), idPrefix, anchors, dynamicAnchors);
                }
            }
        }

        if (schema.getPropertyNames() != null && schema.getPropertyNames() instanceof Schema) {
            schemaIdCollector((Schema) schema.getPropertyNames(), $id + "/propertyNames", idPrefix, anchors, dynamicAnchors);
        }

        if (schema.getIfKeyword() != null && schema.getIfKeyword() instanceof Schema) {
            schemaIdCollector((Schema) schema.getIfKeyword(), $id + "/if", idPrefix, anchors, dynamicAnchors);
        }

        if (schema.getThen() != null && schema.getThen() instanceof Schema) {
            schemaIdCollector((Schema) schema.getThen(), $id + "/then", idPrefix, anchors, dynamicAnchors);
        }

        if (schema.getElseKeyword() != null && schema.getElseKeyword() instanceof Schema) {
            schemaIdCollector((Schema) schema.getElseKeyword(), $id + "/else", idPrefix, anchors, dynamicAnchors);
        }

        if (schema.getNot() != null && schema.getNot() instanceof Schema) {
            schemaIdCollector((Schema) schema.getNot(), $id + "/not", idPrefix, anchors, dynamicAnchors);
        }

        if (schema.getContent() != null && schema.getContent() instanceof Schema) {
            schemaIdCollector((Schema) schema.getContent(), $id + "/content", idPrefix, anchors, dynamicAnchors);
        }

        if (schema.getUnevaluatedItems() != null && schema.getUnevaluatedItems() instanceof Schema) {
            schemaIdCollector((Schema) schema.getUnevaluatedItems(), $id + "/unevaluatedItems", idPrefix, anchors, dynamicAnchors);
        }

        if (schema.get$ref() != null) {
            if (schema.get$ref().startsWith(String.valueOf("#/"))) {
                schema.set$ref(idPrefix + schema.get$ref().substring(1));
            } else if (schema.get$ref().startsWith(String.valueOf("/"))) {
                schema.set$ref(idPrefix + schema.get$ref());
            } else if (schema.get$ref().startsWith(String.valueOf("#"))) {
                String anchorName = schema.get$ref().substring(1);
                if (anchors.containsKey(anchorName)) {
                    schema.set$ref(anchors.get(anchorName));
                } else {
                    throw new RuntimeException("Anchor " + anchorName + " not found");
                }
            } else if (!(schema.get$ref().startsWith(String.valueOf("http://")) || schema.get$ref().startsWith(String.valueOf("https://"))))
            //TODO: Recheck this
            {
                schema.set$ref(idPrefix + "/" + schema.get$ref());
            }
        }

        if (schema.get$dynamicRef() != null) { // TODO: Complete this part
            if (schema.get$dynamicRef().startsWith(String.valueOf("#/"))) {
                String anchorName = schema.get$ref().substring(1);
                if (dynamicAnchors.containsKey(anchorName)) {
                    schema.set$dynamicRef(dynamicAnchors.get(anchorName));
                } else {
                    throw new RuntimeException("Dynamic anchor " + anchorName + " not found");
                }
            } else {
                throw new RuntimeException("Dynamic anchor " + schema.get$dynamicRef() + " not found");
            }
        }

        // Adding default values
        // TODO: Complete this if needed.
    }
}
