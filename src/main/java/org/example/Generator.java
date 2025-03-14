package org.example;

import com.google.gson.internal.LinkedTreeMap;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;

import java.util.*;
import java.util.stream.Collectors;

import static org.example.GeneratorUtil.*;

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

    public static String convert(Schema schema, String name, Map<String, ModuleMemberDeclarationNode> nodes) {
        ArrayList<Object> schemaType = getCommonType(schema.enumKeyword(), schema.constKeyword(), schema.type());
        // This can be a type or a value.
        if (schemaType.isEmpty()) {
            return NEVER;
        } else if (schemaType.contains(Class.class)) {
            //! This is definitely a type
            schemaType.remove(Class.class);
            if (schemaType.size() == 1) {
                // Only a single type.
                return createType(nodes, name, schema, schemaType.getFirst());
            } else {
                Set<String> unionTypes = new HashSet<>();
                for(Object element : schemaType) {
                    String subtypeName = name + getBallerinaType(element);
                    unionTypes.add(createType(nodes, subtypeName, schema, element));
                }
                if (unionTypes.contains(NUMBER)){
                    unionTypes.remove(NUMBER);
                    unionTypes.add(INTEGER);
                    unionTypes.add(FLOAT);
                    unionTypes.add(DECIMAL);
                }
                return String.join("|", unionTypes);
            }
        } else {
            // Create an enum.
            //TODO: Constraints validate to enums too, need to cross check them and return the value
            return schemaType.stream().map(String::valueOf).collect(Collectors.joining("|"));
        }
    }
}
