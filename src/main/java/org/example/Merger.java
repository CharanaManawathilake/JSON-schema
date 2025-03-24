package org.example;

import java.util.List;

public class Merger {
    public static Schema combineAllOfSchemas(Schema schema1, Schema schema2) {
        //TODO: This will combine 2 schemas. Will perform the function of AllOf combination for non conflicting items.
        Schema schema = new Schema();

        // For the following keywords, the mainSchema keywords will override that of subSchemas
        //                 List<Object> prefixItems, : Concat
        //                Object items, : recurse
        //                Object contains, : recurse ( All Of annotation )
        //                Object additionalProperties, : recurse
        //                Map<String, Object> properties,
        //                Map<String, Object> patternProperties,
        //                Map<String, Object> dependentSchema,
        //                Object propertyNames,
        //                Object ifKeyword,
        //                Object then,
        //                Object elseKeyword,
        //                List<Object> allOf,
        //                List<Object> oneOf,
        //                List<Object> anyOf,
        //                Object not,
        //                String contentEncoding,
        //                String contentMediaType,
        //                Object content,
        //                String $id,
        //                String $schema,
        //                String $ref,
        //                String $anchor,
        //                String $dynamicRef,
        //                String $dynamicAnchor,
        //                String $vocabulary,
        //                String $comment,
        //                Map<String, Object> $defs,
        //                String format,
        //                String title,
        //                String description,
        //                Object defaultKeyword,
        //                Boolean deprecated,
        //                Boolean readOnly,
        //                Boolean writeOnly,
        //                List<Object> examples,
        //                Object unevaluatedItems,
        //                Object unevaluatedProperties,
        //                ArrayList<String> type,
        //                Object constKeyword,
        //                ArrayList<Object> enumKeyword,
        //                Double multipleOf,
        //                Double maximum,
        //                Double exclusiveMaximum,
        //                Double minimum,
        //                Double exclusiveMinimum,
        //                Long maxLength,
        //                Long minLength,
        //                String pattern,
        //                Long maxItems,
        //                Long minItems,
        //                Boolean uniqueItems,
        //                Long maxContains,
        //                Long minContains,
        //                Long maxProperties,
        //                Long minProperties,
        //                List<String> required,
        //                Map<String, List<String>> dependentRequired


        return schema1;
    }

    public static Object combineSubSchemas(Object mainSchemaObject, Object subSchemaObject) {
        if (mainSchemaObject instanceof Boolean && subSchemaObject instanceof Schema) {
            return (Schema) subSchemaObject;
        }
        if (mainSchemaObject instanceof Schema && subSchemaObject instanceof Boolean) {
            return (Schema) mainSchemaObject;
        }
        if (mainSchemaObject instanceof Boolean && subSchemaObject instanceof Boolean) {
            return (Boolean) mainSchemaObject && (Boolean) subSchemaObject;
        }

        Schema mainSchema = null;
        Schema subSchema = null;
        if (mainSchemaObject instanceof Schema && subSchemaObject instanceof Schema) {
            mainSchema = (Schema) mainSchemaObject;
            subSchema = (Schema) subSchemaObject;
        } else {
            throw new IllegalArgumentException("mainSchemaObject and subSchemaObject are not of the same type");
        }

        // Combining the sub Schema ( An element of a keyword like OneOf ) to the main Schema
        boolean annotationRequired = false;
        Schema newSchema = new Schema();

        // List<Object> PrefixItems
        if (mainSchema.getPrefixItems().isEmpty() || mainSchema.getPrefixItems() == null){
            newSchema.setPrefixItems(subSchema.getPrefixItems());
        } else {
            if (subSchema.getPrefixItems().isEmpty() || subSchema.getPrefixItems() == null) {
                newSchema.setPrefixItems(mainSchema.getPrefixItems());
            } else {
                // AllOf them
            }
        }



        // Object items
        if (mainSchema.getItems() == null){

        }
        //                Object items, : recurse
        //                Object contains, : recurse ( All Of annotation )
        //                Object additionalProperties, : recurse
        //                Map<String, Object> properties,
        //                Map<String, Object> patternProperties,
        //                Map<String, Object> dependentSchema,
        //                Object propertyNames,
        //                Object ifKeyword,
        //                Object then,
        //                Object elseKeyword,
        //                List<Object> allOf,
        //                List<Object> oneOf,
        //                List<Object> anyOf,
        //                Object not,
        //                String contentEncoding,
        //                String contentMediaType,
        //                Object content,
        //                String $id,
        //                String $schema,
        //                String $ref,
        //                String $anchor,
        //                String $dynamicRef,
        //                String $dynamicAnchor,
        //                String $vocabulary,
        //                String $comment,
        //                Map<String, Object> $defs,
        //                String format,
        //                String title,
        //                String description,
        //                Object defaultKeyword,
        //                Boolean deprecated,
        //                Boolean readOnly,
        //                Boolean writeOnly,
        //                List<Object> examples,
        //                Object unevaluatedItems,
        //                Object unevaluatedProperties,
        //                ArrayList<String> type,
        //                Object constKeyword,
        //                ArrayList<Object> enumKeyword,
        //                Double multipleOf,
        //                Double maximum,
        //                Double exclusiveMaximum,
        //                Double minimum,
        //                Double exclusiveMinimum,
        //                Long maxLength,
        //                Long minLength,
        //                String pattern,
        //                Long maxItems,
        //                Long minItems,
        //                Boolean uniqueItems,
        //                Long maxContains,
        //                Long minContains,
        //                Long maxProperties,
        //                Long minProperties,
        //                List<String> required,
        //                Map<String, List<String>> dependentRequired

        return newSchema;
    }

    ;


    public static Schema factorizeASchema(Schema schema){
        //TODO: This will factorize a schema. Only the common parts (not the combination of properties)
        return new Schema();
    }

    public static Schema factorize(List<Object> schemas){
        return new Schema();
    }
}
