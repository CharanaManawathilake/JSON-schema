package org.example;

import com.google.gson.internal.LinkedTreeMap;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.NodeParser;

import java.util.*;
import java.util.stream.Collectors;

import static org.example.GeneratorUtil.*;
import static org.example.Merger.combineSubSchemas;
import static org.example.SchemaDatabase.idToTypeMap;
import static org.example.SchemaDatabase.schemaList;

public class Generator {
    public static ArrayList<Object> getCommonType(ArrayList<Object> enumKeyword, Object constKeyword, ArrayList<String> type) {
        Set<Object> typeList = new HashSet<>();
        Set<Object> finalList = new HashSet<>();

        if (type == null || type.isEmpty()) {
            typeList.add(Long.class);
            typeList.add(Double.class);
            typeList.add(Boolean.class);
            typeList.add(String.class);
            typeList.add(ArrayList.class);
            typeList.add(LinkedTreeMap.class);
            typeList.add(null);
        } else {
            for (String element : type) {
                switch (element) {
                    case "integer":
                        typeList.add(Long.class);
                        break;
                    case "number":
                        typeList.add(Double.class);
                        break;
                    case "boolean":
                        typeList.add(Boolean.class);
                        break;
                    case "string":
                        typeList.add(String.class);
                        break;
                    case "array":
                        typeList.add(ArrayList.class);
                        break;
                    case "object":
                        typeList.add(LinkedTreeMap.class);
                        break;
                    case "null":
                        typeList.add(null);
                        break;
                    default:
                        break;
                }
            }
        }

        if (!typeList.isEmpty()) {
            typeList.add(Class.class);
        }

        // Absence of enum keyword.
        if (enumKeyword == null) {
            if (constKeyword == null) {
                return new ArrayList<>(typeList);
            }
            if (typeList.contains(constKeyword.getClass())) {
                return new ArrayList<>(List.of(constKeyword));
            }
            return new ArrayList<>();
        }

        // Presence of enum keyword.
        for (Object element : enumKeyword) {
            if (typeList.contains(element.getClass())) {
                finalList.add(element);
            }
        }

        if (constKeyword == null) {
            return new ArrayList<>(finalList);
        }

        if (finalList.contains(constKeyword)) { // Checked for reference cross check-ins
            return new ArrayList<>(List.of(constKeyword));
        }
        return new ArrayList<>();
    }

    public static String convert(Object schemaObject, String name, Map<String, ModuleMemberDeclarationNode> nodes) {
        if (schemaObject instanceof Boolean){
            boolean boolValue = (Boolean) schemaObject;
            return boolValue ? JSON : NEVER;
        }
        Schema schema = (Schema) schemaObject;

        //! Ref is added here, because after this point a single id is branched to several types.
        // TODO: Additional attributes need to be appended to the ref schema.
        if (schema.get$ref() != null) {
            if (idToTypeMap.containsKey(schema.get$id())) {
                return idToTypeMap.get(schema.get$id());
            }
            // else
            return convert(schemaList.get(schema.get$ref()), name, nodes);
        }

        // AnyOf, OneOf, AllOf combining keywords
        int keywordCount = 0;
        List<Object> combinedSchema = new ArrayList<>(List.of());

        if (schema.getAnyOf() != null) {
            keywordCount++;
            Schema AnyOfSchema = new Schema();
            AnyOfSchema.setAnyOf(schema.getAnyOf());
            combinedSchema.add(AnyOfSchema);
        }
        if (schema.getOneOf() != null) {
            keywordCount++;
            Schema OneOfSchema = new Schema();
            OneOfSchema.setOneOf(schema.getOneOf());
            combinedSchema.add(OneOfSchema);
        }
        if (schema.getAllOf() != null) {
            keywordCount++;
            Schema AllOfSchema = new Schema();
            AllOfSchema.setAllOf(schema.getAllOf());
            combinedSchema.add(AllOfSchema);
        }

        // Branching for the keywords.
        if (keywordCount > 1) {
            schema.setAllOf(combinedSchema);
            schema.setAnyOf(null);
            schema.setOneOf(null);
        }

        // There are only one combining keyword present in the schema beyond this point.
        if (schema.getAnyOf() != null) {
            List<Object> anyOf = schema.getAnyOf();
            List<String> typeList = new ArrayList<>();
            schema.setAnyOf(null);

            name = resolveNameConflicts(name, nodes);
            nodes.put(name, NodeParser.parseModuleMemberDeclaration(""));

            for (int i = 0; i < anyOf.size(); i++) {
                Object element = anyOf.get(i);
                Object anyOfSchema = combineSubSchemas(schema, element);
                String type = convert(anyOfSchema, resolveNameConflicts(name + "AnyOf" + i, nodes), nodes);
                typeList.add(type);
            }

            String typeDefinition = TYPE + WHITESPACE + name + WHITESPACE + String.join("|", typeList) + SEMI_COLON;
            // TODO: Set the id to this.

            ModuleMemberDeclarationNode moduleNode = NodeParser.parseModuleMemberDeclaration(typeDefinition);
            nodes.put(name, moduleNode);
            return name;
        }

        if (schema.getOneOf() != null) {
            List<Object> oneOf = schema.getOneOf();
            List<String> typeList = new ArrayList<>();
            schema.setOneOf(null);

            name = resolveNameConflicts(name, nodes);
            nodes.put(name, NodeParser.parseModuleMemberDeclaration(""));

            for (int i = 0; i < oneOf.size(); i++) {
                Object element = oneOf.get(i);
                Object oneOfSchema = combineSubSchemas(schema, element);
                String type = convert(oneOfSchema, resolveNameConflicts(name + "OneOf" + i, nodes), nodes);
                typeList.add(type);
            }

            String typeDefinition = ONE_OF_ANNOTATION + NEW_LINE + TYPE + WHITESPACE + name + WHITESPACE + String.join("|", typeList) + SEMI_COLON;
            // TODO: Set the id to this.

            ModuleMemberDeclarationNode moduleNode = NodeParser.parseModuleMemberDeclaration(typeDefinition);
            nodes.put(name, moduleNode);
            return name;
        }

        if (schema.getAllOf() != null) {
            List<Object> allOf = schema.getAllOf();
            List<String> typeList = new ArrayList<>();
            schema.setAllOf(null);

            name = resolveNameConflicts(name, nodes);
            nodes.put(name, NodeParser.parseModuleMemberDeclaration(""));

            for (int i = 0; i < allOf.size(); i++) {
                Object element = allOf.get(i);
                Object allOfSchema = combineSubSchemas(schema, element);
                String type = convert(allOfSchema, resolveNameConflicts(name + "AllOf" + i, nodes), nodes);
                typeList.add(type);
            }

            String typeDefinition = ONE_OF_ANNOTATION + NEW_LINE + TYPE + WHITESPACE + name + WHITESPACE + String.join("|", typeList) + SEMI_COLON;
            // TODO: Set the id to this.

            ModuleMemberDeclarationNode moduleNode = NodeParser.parseModuleMemberDeclaration(typeDefinition);
            nodes.put(name, moduleNode);
            return name;
        }

        ArrayList<Object> schemaType = getCommonType(schema.getEnumKeyword(), schema.getConstKeyword(), schema.getType());
        // This can be a type or a value.
        if (schemaType.isEmpty()) {
            idToTypeMap.put(schema.get$id(), NEVER);
            return NEVER;
        } else if (schemaType.contains(Class.class)) {
            //! This is definitely a type
            schemaType.remove(Class.class);
            if (schemaType.size() == 1) {
                // Only a single type.
                String typeName = createType(nodes, name, schema, schemaType.getFirst());
                idToTypeMap.put(schema.get$id(), typeName);
                return typeName;
            } else {
                Set<String> unionTypes = new HashSet<>();
                for(Object element : schemaType) {
                    String subtypeName = name + getBallerinaType(element);
                    unionTypes.add(createType(nodes, subtypeName, schema, element));
                }
                if (unionTypes.containsAll(Set.of(INTEGER, NUMBER, BOOLEAN, STRING, UNIVERSAL_ARRAY, UNIVERSAL_OBJECT, NULL))) {
                    idToTypeMap.put(schema.get$id(), JSON);
                    return JSON;
                }
                if (unionTypes.contains(NUMBER)) {
                    unionTypes.remove(NUMBER);
                    unionTypes.add(INTEGER);
                    unionTypes.add(FLOAT);
                    unionTypes.add(DECIMAL);
                }
                String typeName = String.join(PIPE, unionTypes);
                idToTypeMap.put(schema.get$id(), typeName);
                return typeName;
            }
        } else {
            // Create an enum.
            //TODO: Constraints validate to enums too, need to cross check them and return the value
            String typeName = schemaType.stream().map(String::valueOf).collect(Collectors.joining(PIPE));
            idToTypeMap.put(schema.get$id(), typeName);
            return typeName;
        }
    }
}
