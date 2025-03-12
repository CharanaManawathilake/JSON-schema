package org.example;

import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.NodeParser;

import java.util.Map;

public class GeneratorUtil {
    public static final String PUBLIC = "public";
    public static final String TYPE = "type";
    public static final String WHITESPACE = " ";
    public static final String AT = "@";
    public static final String OPEN_BRACES = "{";
    public static final String CLOSE_BRACES = "}";
    public static final String COLON = ":";
    public static final String INTEGER = "int";
    public static final String SEMI_COLON = ";";
    public static final String COMMA = ",";
    public static final String NEW_LINE = "\n";

    public static final String ANNOTATION_MODULE = "jsondata";
    public static final String NUMBER_ANNOTATION = AT + ANNOTATION_MODULE + COLON + "NumberValidation";
    public static final String MINIMUM = "minimum";
    public static final String EXCLUSIVE_MINIMUM = "exclusiveMinimum";
    public static final String MAXIMUM = "maximum";
    public static final String EXCLUSIVE_MAXIMUM = "exclusiveMaximum";
    public static final String MULTIPLE_OF = "multipleOf";

    public static final String STRING_ANNOTATION = AT + ANNOTATION_MODULE + COLON + "StringValidation";
    public static final String ARRAY_VALIDATION = AT + ANNOTATION_MODULE + COLON + "ArrayValidation";
    public static final String OBJECT_ANNOTATION = AT + ANNOTATION_MODULE + COLON + "ObjectValidation";

    public static String createType(Map<String, ModuleMemberDeclarationNode> nodes, String name, Schema schema, Object type){
        if (type == Long.class) {
            return createInteger(nodes, name, schema.minimum(),schema.exclusiveMinimum(), schema.maximum(), schema.exclusiveMaximum(), schema.multipleOf());
        } else if (type == String.class) {
            System.out.println("This is a string");
        }
        return "HELLO";
    }

    public static String createInteger(Map<String, ModuleMemberDeclarationNode> nodes, String name, Double minimum, Double exclusiveMinimum, Double maximum, Double exclusiveMaximum, Double multipleOf){
        String finalName = resolveNameConflicts(convertToCamelCase(name), nodes);

        if (minimum==null && maximum==null && exclusiveMaximum==null && exclusiveMinimum==null && multipleOf==null){
            return TYPE + WHITESPACE + finalName + WHITESPACE + INTEGER + SEMI_COLON;
        }

        StringBuilder annotation = new StringBuilder();
        annotation.append(NUMBER_ANNOTATION).append(OPEN_BRACES);

        if (minimum!=null) {
            annotation.append(MINIMUM).append(COLON).append(minimum.toString()).append(COMMA);
        }
        if (exclusiveMinimum!=null){
            annotation.append(EXCLUSIVE_MINIMUM).append(COLON).append(exclusiveMinimum.toString()).append(COMMA);
        }
        if (maximum!=null) {
            annotation.append(MAXIMUM).append(COLON).append(maximum.toString()).append(COMMA);
        }
        if (exclusiveMaximum!=null) {
            annotation.append(EXCLUSIVE_MAXIMUM).append(COLON).append(exclusiveMaximum.toString()).append(COMMA);
        }
        if (multipleOf!=null){
            annotation.append(MULTIPLE_OF).append(COLON).append(multipleOf.toString()).append(COMMA);
        }

        annotation.deleteCharAt(annotation.length() - 1).append(CLOSE_BRACES).append(NEW_LINE);
        annotation.append(TYPE).append(WHITESPACE).append(finalName).append(WHITESPACE).append(INTEGER).append(SEMI_COLON);

        ModuleMemberDeclarationNode moduleNode = NodeParser.parseModuleMemberDeclaration(annotation.toString());
        nodes.put(finalName, moduleNode);

        return finalName;
    }

    public static String resolveNameConflicts(String name, Map<?, ?> nodes) {
        String baseName = name;
        int counter = 1;
        while (nodes.containsKey(name)) {
            name = baseName + counter;
            counter++;
        }
        return name;
    }

    public static String convertToCamelCase(String name) {
        if (name == null || name.isEmpty()){
            return name;
        }
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }
}
