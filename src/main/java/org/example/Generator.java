package org.example;

import com.google.gson.internal.LinkedTreeMap;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;

import java.util.*;

import static org.example.GeneratorUtil.createType;

public class Generator {
    public static final String TYPE = "type";
    public static final String INTEGER = "int";
    public static final String NUMBER = "int|float|decimal";
    public static final String BOOLEAN = "boolean";
    public static final String ARRAY = "array";

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

    public static void convert(Schema schema, String name, Map<String, ModuleMemberDeclarationNode> nodes) {
        ArrayList<Object> schemaType = getCommonType(schema.enumKeyword(), schema.constKeyword(), schema.type());
        // This can be a type or a value.
        if (schemaType.isEmpty()) {
            System.out.println("No valid instances. type Schema never");
            // TODO: Return a never type.
        } else if (schemaType.contains(Class.class)) {
            //! This is definitely a type
            schemaType.remove(Class.class);
            if (schemaType.size() == 1) {
                // TODO: Create only a single type.
                String tempName = createType(nodes, name, schema, schemaType.getFirst());
                System.out.println(tempName);
            } else {
                for(Object element : schemaType) {
                    //TODO: Create types for each of these.
                }
                //TODO: Then create a type combining all of them.
            }
        } else {
            System.out.println("This is an enum");
            // Create an enum.
            //TODO: Constraints validate to enums too, need to cross check them and return the value
        }


        System.out.println("Hello");
    }
}
