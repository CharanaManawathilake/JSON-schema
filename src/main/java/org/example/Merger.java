package org.example;

import java.util.*;
import java.util.stream.Stream;

public class Merger {
    public static Schema combineAllOfSchemas(Schema schema1, Schema schema2) {
        //TODO: This will combine 2 schemas. Will perform the function of AllOf combination for non conflicting items.

        // List<Object> prefixItems, : AllOf this together with the items
        // Object items,
        // Object contains,
        // Object additionalProperties,
        // Map<String, Object> properties,
        // Map<String, Object> patternProperties,
        // Map<String, Object> dependentSchema,
        // Object propertyNames,
        // Object ifKeyword,
        // Object then,
        // Object elseKeyword,
        // List<Object> allOf,
        // List<Object> oneOf,
        // List<Object> anyOf,
        // Object not,
        // String contentEncoding,
        // String contentMediaType,
        // Object content,
        // String $id,
        // String $schema,
        // String $ref,
        // String $anchor,
        // String $dynamicRef,
        // String $dynamicAnchor,
        // String $vocabulary,
        // String $comment,
        // Map<String, Object> $defs,
        // String format,
        // String title,
        // String description,
        // Object defaultKeyword,
        // Boolean deprecated,
        // Boolean readOnly,
        // Boolean writeOnly,
        // List<Object> examples,
        // Object unevaluatedItems,
        // Object unevaluatedProperties,
        // ArrayList<String> type,
        // Object constKeyword,
        // ArrayList<Object> enumKeyword,
        // Double multipleOf,
        // Double maximum,
        // Double exclusiveMaximum,
        // Double minimum,
        // Double exclusiveMinimum,
        // Long maxLength,
        // Long minLength,
        // String pattern,
        // Long maxItems,
        // Long minItems,
        // Boolean uniqueItems,
        // Long maxContains,
        // Long minContains,
        // Long maxProperties,
        // Long minProperties,
        // List<String> required,
        // Map<String, List<String>> dependentRequired
        Schema schema = new Schema();
        return schema1;
    }

    public static Object[] combineSubSchemas(Object mainSchemaObject, Object subSchemaObject) {
        // Handling the case where mainSchemaObject or subSchemaObject is a boolean
        if (mainSchemaObject instanceof Boolean && subSchemaObject instanceof Schema) {
            return new Object[]{false, subSchemaObject};
        }
        if (mainSchemaObject instanceof Schema && subSchemaObject instanceof Boolean) {
            return new Object[]{false, mainSchemaObject};
        }
        if (mainSchemaObject instanceof Boolean && subSchemaObject instanceof Boolean) {
            return new Object[]{false, (Boolean) mainSchemaObject && (Boolean) subSchemaObject};
        }

        // Both the mainSchemaObject and the subSchemaObject are Schemas
        Schema mainSchema;
        Schema subSchema;
        if (mainSchemaObject instanceof Schema && subSchemaObject instanceof Schema) {
            mainSchema = (Schema) mainSchemaObject;
            subSchema = (Schema) subSchemaObject;
        } else {
            throw new IllegalArgumentException("mainSchemaObject and subSchemaObject are not of the same type");
        }

        // Combining the sub Schema ( An element of a keyword like OneOf ) to the main Schema
        boolean annotationRequired = false;
        Schema AllOfSchema1 = new Schema();
        Schema AllOfSchema2 = new Schema();
        Schema newSchema = new Schema();

        //! Keywords

        // Object items,
        // List<Object> PrefixItems & Object items
        // TODO: complete this part using the sizes of hte prefixItems list and then merging the intersection between that and the items part.
        if (mainSchema.getPrefixItems() == null || mainSchema.getPrefixItems().isEmpty()) {
            newSchema.setPrefixItems(subSchema.getPrefixItems());
        } else {
            if (subSchema.getPrefixItems() == null || subSchema.getPrefixItems().isEmpty()) {
                newSchema.setPrefixItems(mainSchema.getPrefixItems());
            } else {
                annotationRequired = true;
                AllOfSchema1.setPrefixItems(mainSchema.getPrefixItems());
                AllOfSchema2.setPrefixItems(subSchema.getPrefixItems());
            }
        }

        // Object contains,
        // Long maxContains,
        // Long minContains,
        if (mainSchema.getContains() == null) {
            newSchema.setContains(subSchema.getContains());
            newSchema.setMinContains(subSchema.getMinContains());
            newSchema.setMaxContains(subSchema.getMaxContains());
        } else if (subSchema.getContains() == null) {
            newSchema.setContains(mainSchema.getContains());
            newSchema.setMinContains(mainSchema.getMinContains());
            newSchema.setMaxContains(mainSchema.getMaxContains());
        } else {
            annotationRequired = true;
            AllOfSchema1.setContains(mainSchema.getContains());
            AllOfSchema1.setMinContains(mainSchema.getMinContains());
            AllOfSchema1.setMaxContains(mainSchema.getMaxContains());
            AllOfSchema2.setContains(subSchema.getContains());
            AllOfSchema2.setMinContains(subSchema.getMinContains());
            AllOfSchema2.setMaxContains(subSchema.getMaxContains());
        }

        // Object additionalProperties,
        // Object unevaluatedProperties,
        // Map<String, Object> properties,
        if (mainSchema.getAdditionalProperties() == null && mainSchema.getUnevaluatedProperties() == null
                && subSchema.getAdditionalProperties() == null && subSchema.getUnevaluatedProperties() == null) {
            if (mainSchema.getProperties() == null) {
                newSchema.setProperties(subSchema.getProperties());
            } else if (subSchema.getProperties() == null) {
                newSchema.setProperties(mainSchema.getProperties());
            } else {
                // Combine the two schemas
                Map<String, Object> properties = new HashMap<>(mainSchema.getProperties());

                for (Map.Entry<String, Object> entry : subSchema.getProperties().entrySet()) {
                    String key = entry.getKey();
                    Object subSchemaValue = entry.getValue();

                    if (properties.containsKey(key)) {
                        properties.compute(key, (k, mainSchemaValue) -> combineSubSchemas(mainSchemaValue, subSchemaValue));
                    } else {
                        properties.put(key, subSchemaValue);
                    }
                }
            }
        } else {
            annotationRequired = true;
            AllOfSchema1.setAdditionalProperties(mainSchema.getAdditionalProperties());
            AllOfSchema1.setUnevaluatedProperties(mainSchema.getUnevaluatedProperties());
            AllOfSchema1.setProperties(mainSchema.getProperties());
            AllOfSchema2.setAdditionalProperties(subSchema.getAdditionalProperties());
            AllOfSchema2.setUnevaluatedProperties(subSchema.getUnevaluatedProperties());
            AllOfSchema2.setProperties(subSchema.getProperties());
        }

        // Map<String, Object> patternProperties,
        // Map<String, Object> dependentSchema,

        // Object propertyNames,
        //?? Does this need to be here, or within the additionalProperties/ unEvaluatedProperties keywords.
        if (mainSchema.getPropertyNames() == null) {
            newSchema.setPropertyNames(subSchema.getPropertyNames());
        } else if (subSchema.getPropertyNames() == null) {
            newSchema.setPropertyNames(mainSchema.getPropertyNames());
        } else {
            newSchema.setPropertyNames(combineSubSchemas(mainSchema.getPropertyNames(), subSchema.getPropertyNames()));
        }

        // Object ifKeyword,
        // Object then,
        // Object elseKeyword,
        if (mainSchema.getIfKeyword() == null) {
            newSchema.setIfKeyword(subSchema.getIfKeyword());
            newSchema.setThen(subSchema.getThen());
            newSchema.setElseKeyword(subSchema.getElseKeyword());
        } else if (subSchema.getIfKeyword() == null) {
            newSchema.setIfKeyword(mainSchema.getIfKeyword());
            newSchema.setThen(mainSchema.getThen());
            newSchema.setElseKeyword(mainSchema.getElseKeyword());
        } else {
            annotationRequired = true;
            AllOfSchema1.setIfKeyword(mainSchema.getIfKeyword());
            AllOfSchema1.setThen(mainSchema.getThen());
            AllOfSchema1.setElseKeyword(mainSchema.getElseKeyword());
            AllOfSchema2.setIfKeyword(subSchema.getIfKeyword());
            AllOfSchema2.setThen(subSchema.getThen());
            AllOfSchema2.setElseKeyword(subSchema.getElseKeyword());
        }

        // List<Object> allOf,
        if (mainSchema.getAllOf() == null || mainSchema.getAllOf().isEmpty()) {
            newSchema.setAllOf(subSchema.getAllOf());
        } else if (subSchema.getAllOf() == null || subSchema.getAllOf().isEmpty()) {
            newSchema.setAllOf(mainSchema.getAllOf());
        } else {
            List<Object> combinedAllOf = new ArrayList<>(mainSchema.getAllOf());
            combinedAllOf.addAll(subSchema.getAllOf());
            newSchema.setAllOf(combinedAllOf);
        }

        // List<Object> oneOf,
        if (mainSchema.getOneOf() == null || mainSchema.getOneOf().isEmpty()) {
            newSchema.setOneOf(subSchema.getOneOf());
        } else if (subSchema.getOneOf() == null || subSchema.getOneOf().isEmpty()) {
            newSchema.setOneOf(mainSchema.getOneOf());
        } else {
            annotationRequired = true;
            AllOfSchema1.setOneOf(mainSchema.getOneOf());
            AllOfSchema2.setOneOf(subSchema.getOneOf());
        }

        // List<Object> anyOf,
        if (mainSchema.getAnyOf() == null || mainSchema.getAnyOf().isEmpty()) {
            newSchema.setAnyOf(subSchema.getAnyOf());
        } else if (subSchema.getAnyOf() == null || subSchema.getAnyOf().isEmpty()) {
            newSchema.setAnyOf(mainSchema.getAnyOf());
        } else {
            annotationRequired = true;
            AllOfSchema1.setAnyOf(mainSchema.getAnyOf());
            AllOfSchema2.setAnyOf(subSchema.getAnyOf());
        }

        // Object not,
        if (mainSchema.getNot() == null) {
            newSchema.setNot(subSchema.getNot());
        } else if (subSchema.getNot() == null) {
            newSchema.setNot(mainSchema.getNot());
        } else {
            annotationRequired = true;
            AllOfSchema1.setNot(mainSchema.getNot());
            AllOfSchema2.setNot(subSchema.getNot());
        }

        // String contentEncoding,
        // String contentMediaType,
        // Object content,

        // String $id,

        // String $schema,
        //?? Doesn't matter, as the $schema should be 2020-12 specifically

        // String $ref,

        // String $anchor,
        // String $dynamicRef,
        // String $dynamicAnchor,

        // String $comment,
        if (mainSchema.get$comment() == null) {
            newSchema.set$comment(subSchema.get$comment());
        } else if (subSchema.get$comment() == null) {
            newSchema.set$comment(mainSchema.get$comment());
        } else {
            newSchema.set$comment(mainSchema.get$comment() + ". " + subSchema.get$comment()); // TODO: WHAT???
        }

        // Map<String, Object> $defs,

        // String format,
        if (mainSchema.getFormat() == null) {
            newSchema.setFormat(subSchema.getFormat());
        } else if (subSchema.getFormat() == null) {
            newSchema.setFormat(mainSchema.getFormat());
        } else {
            // TODO: Handle using the AllOf keyword.
        }

        // String title,
        // String description,
        // Object defaultKeyword,
        // Boolean deprecated,
        // Boolean readOnly,
        // Boolean writeOnly,
        // List<Object> examples,
        // Object unevaluatedItems,

        // ArrayList<String> type,
        //TODO: Handle integer, number issue here.
        if (mainSchema.getType() == null) {
            newSchema.setType(subSchema.getType());
        } else {
            if (subSchema.getType() == null) {
                newSchema.setType(mainSchema.getType());
            } else {
                newSchema.setType(new ArrayList<>(
                        Stream.concat(mainSchema.getType().stream(), subSchema.getType().stream()).toList()
                ));
            }
        }

        // Object constKeyword
        if (mainSchema.getConstKeyword() == null) {
            newSchema.setConstKeyword(subSchema.getConstKeyword());
        } else if (subSchema.getConstKeyword() == null) {
            newSchema.setConstKeyword(mainSchema.getConstKeyword());
        } else if (mainSchema.getConstKeyword().equals(subSchema.getConstKeyword())) {
            newSchema.setConstKeyword(mainSchema.getConstKeyword());
        } else {
            return new Object[]{false ,false};
        }

        // ArrayList<Object> enumKeyword,
        if (mainSchema.getEnumKeyword() == null) {
            newSchema.setEnumKeyword(subSchema.getEnumKeyword());
        } else {
            if (subSchema.getEnumKeyword() == null) {
                newSchema.setEnumKeyword(mainSchema.getEnumKeyword());
            } else {
                // Combine the two lists into a Set to get the union (no duplicates)
                Set<Object> combinedSet = new HashSet<>();
                combinedSet.addAll(mainSchema.getEnumKeyword());  // Add elements from mainSchema
                combinedSet.addAll(subSchema.getEnumKeyword());   // Add elements from subSchema

                // Convert the Set back to an ArrayList (or your desired list type)
                ArrayList<Object> combinedList = new ArrayList<>(combinedSet);
                newSchema.setEnumKeyword(combinedList);
            }
        }

        // Double multipleOf,
        if (mainSchema.getMultipleOf() == null) {
            newSchema.setMultipleOf(subSchema.getMultipleOf());
        } else {
            if (subSchema.getEnumKeyword() == null) {
                newSchema.setMultipleOf(mainSchema.getMultipleOf());
            } else {
                //TODO: Add an AllOf to this.
            }
        }

        // Double maximum,
        if (mainSchema.getMaximum() == null) {
            newSchema.setMaximum(subSchema.getMaximum());
        } else if (subSchema.getMaximum() == null) {
            newSchema.setMaximum(mainSchema.getMaximum());
        } else {
            newSchema.setMaximum(Math.min(mainSchema.getMaximum(), subSchema.getMaximum()));
        }

        // Double exclusiveMaximum,
        if (mainSchema.getExclusiveMaximum() == null) {
            newSchema.setExclusiveMaximum(subSchema.getExclusiveMaximum());
        } else if (subSchema.getExclusiveMaximum() == null) {
            newSchema.setExclusiveMaximum(mainSchema.getExclusiveMaximum());
        } else {
            newSchema.setExclusiveMaximum(Math.min(mainSchema.getExclusiveMaximum(), subSchema.getExclusiveMaximum()));
        }

        // Double minimum,
        if (mainSchema.getMinimum() == null) {
            newSchema.setMinimum(subSchema.getMinimum());
        } else if (subSchema.getMinimum() == null) {
            newSchema.setMinimum(mainSchema.getMinimum());
        } else {
            newSchema.setMinimum(Math.max(mainSchema.getMinimum(), subSchema.getMinimum()));
        }

        // Double exclusiveMinimum,
        if (mainSchema.getExclusiveMinimum() == null) {
            newSchema.setExclusiveMinimum(subSchema.getExclusiveMinimum());
        } else if (subSchema.getExclusiveMinimum() == null) {
            newSchema.setExclusiveMinimum(mainSchema.getExclusiveMinimum());
        } else {
            newSchema.setExclusiveMinimum(Math.max(mainSchema.getExclusiveMinimum(), subSchema.getExclusiveMinimum()));
        }

        // Long maxLength,
        if (mainSchema.getMaxLength() == null) {
            newSchema.setMaxLength(subSchema.getMaxLength());
        } else if (subSchema.getMaxLength() == null) {
            newSchema.setMaxLength(mainSchema.getMaxLength());
        } else {
            newSchema.setMaxLength(Math.min(mainSchema.getMaxLength(), subSchema.getMaxLength()));
        }

        // Long minLength,
        if (mainSchema.getMinLength() == null) {
            newSchema.setMinLength(subSchema.getMinLength());
        } else if (subSchema.getMinLength() == null) {
            newSchema.setMinLength(mainSchema.getMinLength());
        } else {
            newSchema.setMinLength(Math.max(mainSchema.getMinLength(), subSchema.getMinLength()));
        }

        // String pattern,
        if (mainSchema.getPattern() == null) {
            newSchema.setPattern(subSchema.getPattern());
        } else if (subSchema.getPattern() == null) {
            newSchema.setPattern(mainSchema.getPattern());
        } else {
            //TODO: Add an AllOf to this
        }

        // Long maxItems,
        if (mainSchema.getMaxItems() == null) {
            newSchema.setMaxItems(subSchema.getMaxItems());
        } else if (subSchema.getMaxItems() == null) {
            newSchema.setMaxItems(mainSchema.getMaxItems());
        } else {
            newSchema.setMaxItems(Math.min(mainSchema.getMaxItems(), subSchema.getMaxItems()));
        }

        // Long minItems,
        if (mainSchema.getMinItems() == null) {
            newSchema.setMinItems(subSchema.getMinItems());
        } else if (subSchema.getMinItems() == null) {
            newSchema.setMinItems(mainSchema.getMinItems());
        } else {
            newSchema.setMinItems(Math.max(mainSchema.getMinItems(), subSchema.getMinItems()));
        }

        // Boolean uniqueItems,
        if (mainSchema.getUniqueItems() == null) {
            newSchema.setUniqueItems(subSchema.getUniqueItems());
        } else if (subSchema.getUniqueItems() == null) {
            newSchema.setUniqueItems(mainSchema.getUniqueItems());
        } else {
            newSchema.setUniqueItems(mainSchema.getUniqueItems() || subSchema.getUniqueItems());
        }

        // Long maxProperties,
        if (mainSchema.getMaxProperties() == null) {
            newSchema.setMaxProperties(subSchema.getMaxProperties());
        } else if (subSchema.getMaxProperties() == null) {
            newSchema.setMaxProperties(mainSchema.getMaxProperties());
        } else {
            newSchema.setMaxProperties(Math.min(mainSchema.getMaxProperties(), subSchema.getMaxProperties()));
        }

        // Long minProperties,
        if (mainSchema.getMinProperties() == null) {
            newSchema.setMinProperties(subSchema.getMinProperties());
        } else if (subSchema.getMinProperties() == null) {
            newSchema.setMinProperties(mainSchema.getMinProperties());
        } else {
            newSchema.setMinProperties(Math.max(mainSchema.getMinProperties(), subSchema.getMinProperties()));
        }

        // List<String> required,
        if (mainSchema.getRequired() == null) {
            newSchema.setRequired(subSchema.getRequired());
        } else if (subSchema.getRequired() == null) {
            newSchema.setRequired(mainSchema.getRequired());
        } else {
            // Combine both lists into a set to remove duplicates
            Set<String> combinedSet = new HashSet<>();
            combinedSet.addAll(mainSchema.getRequired());
            combinedSet.addAll(subSchema.getRequired());

            // Convert the set back to a list (if needed)
            List<String> combinedList = new ArrayList<>(combinedSet);
            newSchema.setRequired(combinedList);
        }

        // Map<String, List<String>> dependentRequired
        if (mainSchema.getDependentRequired() == null) {
            newSchema.setDependentRequired(subSchema.getDependentRequired());
        } else if (subSchema.getDependentRequired() == null) {
            newSchema.setDependentRequired(mainSchema.getDependentRequired());
        } else {
            Map<String, List<String>> newDependentRequired = new HashMap<>();

            // Copy existing entries from mainSchema
            for (Map.Entry<String, List<String>> entry : mainSchema.getDependentRequired().entrySet()) {
                newDependentRequired.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }

            // Merge with subSchema's dependentRequired
            for (Map.Entry<String, List<String>> entry : subSchema.getDependentRequired().entrySet()) {
                newDependentRequired.merge(entry.getKey(), new ArrayList<>(entry.getValue()), (list1, list2) -> {
                    list1.addAll(list2);
                    return list1;
                });
            }

            newSchema.setDependentRequired(newDependentRequired);
        }

        if (annotationRequired) {
            newSchema.getAllOf().add(AllOfSchema1);
            newSchema.getAllOf().add(AllOfSchema2);
        }
        return new Object[]{annotationRequired, newSchema};
    }


    public static Schema factorizeASchema(Schema schema){
        //TODO: This will factorize a schema. Only the common parts (not the combination of properties)
        return new Schema();
    }

    public static Schema factorize(List<Object> schemas){
        return new Schema();
    }
}
