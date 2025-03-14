package org.example;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TypeDeserializer implements JsonDeserializer<ArrayList<String>> {
        @Override
        public ArrayList<String> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                if (jsonElement.isJsonArray()) {
                        return jsonDeserializationContext.deserialize(jsonElement, ArrayList.class);
                } else if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString()) {
                        return new ArrayList<>(List.of(jsonElement.getAsString()));
                } else {
                        throw new JsonParseException("Expected a string or an array of strings");
                }
        }
}

class SchemaDeserializer implements JsonDeserializer<Object> {
        @Override
        public Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isBoolean()) {
                        return jsonElement.getAsBoolean();
                }
                if (jsonElement.isJsonObject()) {
                        return jsonDeserializationContext.deserialize(jsonElement, Schema.class);
                } else {
                        throw new JsonParseException("Expected a boolean or an object");
                }
        }
}

class ListSchemaDeserializer implements JsonDeserializer<List<Object>> {
        @Override
        public List<Object> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                if (!jsonElement.isJsonArray()) {
                    throw new JsonParseException("Expected an array");
                }
                List<Object> list = new ArrayList<>();
                for (JsonElement element : jsonElement.getAsJsonArray()) {
                    if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) {
                            list.add(element.getAsBoolean());
                    }
                    else {
                            list.add(jsonDeserializationContext.deserialize(element, Schema.class));
                    }
                }
                return list;
        }
}

class MapStringSchemaDeserializer implements JsonDeserializer<Map<String, Object>> {
        @Override
        public Map<String, Object> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                Map<String, Object> resultMap = new HashMap<>();
                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()){
                        if (entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isBoolean()) {
                                resultMap.put(entry.getKey(), entry.getValue().getAsBoolean());
                        }
                        else {
                                Schema schema = jsonDeserializationContext.deserialize(entry.getValue(), Schema.class);
                                resultMap.put(entry.getKey(), schema);
                        }
                }
                return resultMap;
        }
}

final record Schema (
        // Applicator
        @JsonAdapter(ListSchemaDeserializer.class)
        List<Object> prefixItems,
        @JsonAdapter(SchemaDeserializer.class)
        Object items,
        @JsonAdapter(SchemaDeserializer.class)
        Object contains,
        @JsonAdapter(SchemaDeserializer.class)
        Object additionalProperties,
        @JsonAdapter(MapStringSchemaDeserializer.class)
        Map<String, Object> properties,
        @JsonAdapter(MapStringSchemaDeserializer.class)
        Map<String, Object> patternProperties,
        @JsonAdapter(MapStringSchemaDeserializer.class)
        Map<String, Object> dependentSchema,
        @JsonAdapter(SchemaDeserializer.class)
        Object propertyNames,
        @JsonAdapter(SchemaDeserializer.class)
        @SerializedName("if")
        Object ifKeyword,
        @JsonAdapter(SchemaDeserializer.class)
        Object then,
        @JsonAdapter(SchemaDeserializer.class)
        @SerializedName("else")
        Object elseKeyword,
        @JsonAdapter(ListSchemaDeserializer.class)
        List<Object> allOf,
        @JsonAdapter(ListSchemaDeserializer.class)
        List<Object> oneOf,
        @JsonAdapter(ListSchemaDeserializer.class)
        List<Object> anyOf,
        @JsonAdapter(SchemaDeserializer.class)
        Object not,

        // Content
        String contentEncoding,
        String contentMediaType,
        @JsonAdapter(SchemaDeserializer.class)
        Object content,

        // Core
        String $id,
        String $schema,
        String $ref,
        String $anchor,
        String $dynamicRef,
        String $dynamicAnchor,
        String $vocabulary,
        String $comment,
        @JsonAdapter(MapStringSchemaDeserializer.class)
        Map<String, Object> $defs,

        // Format-annotation/ Format-assertion
        String format,

        // Meta-data
        String title,
        String description,
        @SerializedName("default")
        @JsonAdapter(SchemaDeserializer.class)
        Object defaultKeyword,
        Boolean deprecated,
        Boolean readOnly,
        Boolean writeOnly,
        List<Object> examples,

        // Unevaluated
        @JsonAdapter(SchemaDeserializer.class)
        Object unevaluatedItems,
        @JsonAdapter(SchemaDeserializer.class)
        Object unevaluatedProperties,

        // Validation
        @JsonAdapter(TypeDeserializer.class)
        ArrayList<String> type,
        @SerializedName("const")
        Object constKeyword,
        @SerializedName("enum")
        ArrayList<Object> enumKeyword,
        Double multipleOf,
        Double maximum,
        Double exclusiveMaximum,
        Double minimum,
        Double exclusiveMinimum,
        Long maxLength,
        Long minLength,
        String pattern,
        Long maxItems,
        Long minItems,
        Boolean uniqueItems,
        Long maxContains,
        Long minContains,
        Long maxProperties,
        Long minProperties,
        List<String> required,
        @JsonAdapter(MapStringSchemaDeserializer.class)
        Map<String, Object> dependentRequired
){}
