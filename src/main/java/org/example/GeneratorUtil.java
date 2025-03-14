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
    public static final String SEMI_COLON = ";";
    public static final String COMMA = ",";
    public static final String NEW_LINE = "\n";
    public static final String BACK_TICK = "`";
    public static final String REGEX_PREFIX = "re";

    public static final String INTEGER = "int";
    public static final String STRING = "string";
    public static final String NUMBER = "int|float|decimal";
    public static final String BOOLEAN = "boolean";
    public static final String NEVER = "never";
    public static final String JSON = "json";

    public static final String ANNOTATION_MODULE = "jsondata";
    public static final String NUMBER_ANNOTATION = AT + ANNOTATION_MODULE + COLON + "NumberValidation";
    public static final String STRING_ANNOTATION = AT + ANNOTATION_MODULE + COLON + "StringValidation";
    public static final String ARRAY_VALIDATION = AT + ANNOTATION_MODULE + COLON + "ArrayValidation";
    public static final String OBJECT_ANNOTATION = AT + ANNOTATION_MODULE + COLON + "ObjectValidation";

    public static final String MINIMUM = "minimum";
    public static final String EXCLUSIVE_MINIMUM = "exclusiveMinimum";
    public static final String MAXIMUM = "maximum";
    public static final String EXCLUSIVE_MAXIMUM = "exclusiveMaximum";
    public static final String MULTIPLE_OF = "multipleOf";

    public static final String FORMAT = "format";
    public static final String MIN_LENGTH = "minLength";
    public static final String MAX_LENGTH = "maxLength";
    public static final String PATTERN = "pattern";

    private static final String INVALID_CHARS_PATTERN = ".*[!@$%^&*()_\\-|/\\\\\\s\\d].*";
    private static final String DIGIT_PATTERN = ".*\\d.*";
    private static final String STARTS_WITH_DIGIT_PATTERN = "^\\d.*";


    public static String createType(Map<String, ModuleMemberDeclarationNode> nodes, String name, Schema schema, Object type){
        if (type == Long.class) {
            return createInteger(nodes, name, schema.minimum(),schema.exclusiveMinimum(), schema.maximum(), schema.exclusiveMaximum(), schema.multipleOf());
        } else if (type == Double.class) {
            return createNumber(nodes, name, schema.minimum(), schema.exclusiveMinimum(), schema.maximum(), schema.exclusiveMaximum(), schema.multipleOf());
        } else if (type == String.class) {
            return createString(nodes, name, schema.format(), schema.minLength(), schema.maxLength(), schema.pattern());
        }
        return "HELLO";
    }

    public static String createInteger(Map<String, ModuleMemberDeclarationNode> nodes, String name, Double minimum, Double exclusiveMinimum, Double maximum, Double exclusiveMaximum, Double multipleOf){
        if (minimum==null && maximum==null && exclusiveMaximum==null && exclusiveMinimum==null && multipleOf==null){
            return INTEGER;
        }

        String finalName = resolveNameConflicts(convertToPascalCase(name), nodes);

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

    public static String createNumber(Map<String, ModuleMemberDeclarationNode> nodes, String name, Double minimum, Double exclusiveMinimum, Double maximum, Double exclusiveMaximum, Double multipleOf){
        if (minimum==null && maximum==null && exclusiveMaximum==null && exclusiveMinimum==null && multipleOf==null){
            return NUMBER;
        }

        String finalName = resolveNameConflicts(convertToPascalCase(name), nodes);

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
        annotation.append(TYPE).append(WHITESPACE).append(finalName).append(WHITESPACE).append(NUMBER).append(SEMI_COLON);

        ModuleMemberDeclarationNode moduleNode = NodeParser.parseModuleMemberDeclaration(annotation.toString());
        nodes.put(finalName, moduleNode);

        return finalName;
    }

    public static String createString(Map<String, ModuleMemberDeclarationNode> nodes, String name, String format, Long minLength, Long maxLength, String pattern){
        if (format == null && minLength == null && maxLength == null){
            return STRING;
        }

        String finalName = resolveNameConflicts(convertToPascalCase(name), nodes);

        StringBuilder annotation = new StringBuilder();
        annotation.append(STRING_ANNOTATION).append(OPEN_BRACES);

        if (format!=null) {
            if (!isValidFormat(format)){
                throw new IllegalArgumentException("Invalid format: " + format);
            }
            annotation.append(FORMAT).append(COLON).append(format).append(COMMA);
        }
        if (minLength!=null) {
            annotation.append(MIN_LENGTH).append(COLON).append(minLength.toString()).append(COMMA);
        }
        if (maxLength!=null) {
            annotation.append(MAX_LENGTH).append(COLON).append(maxLength.toString()).append(COMMA);
        }
        if (pattern!=null) {
            annotation.append(PATTERN).append(COLON).append(REGEX_PREFIX).append(BACK_TICK).append(pattern).append(BACK_TICK).append(COMMA);
        }

        annotation.deleteCharAt(annotation.length() - 1).append(CLOSE_BRACES).append(NEW_LINE);
        annotation.append(TYPE).append(WHITESPACE).append(finalName).append(WHITESPACE).append(STRING).append(SEMI_COLON);

        ModuleMemberDeclarationNode moduleNode = NodeParser.parseModuleMemberDeclaration(annotation.toString());
        nodes.put(finalName, moduleNode);

        return finalName;
    }

    public static boolean isValidFormat(String format) {
        //TODO: Return true if the format is a valid JSON format.
        return true;
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

    public static String convertToPascalCase(String name) {
        if (name == null || name.isEmpty()){
            return name;
        }
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
