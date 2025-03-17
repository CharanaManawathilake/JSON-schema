package org.example;

import com.google.gson.internal.LinkedTreeMap;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.NodeParser;

import java.util.ArrayList;
import java.util.List;
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
    public static final String PIPE = "|";
    public static final String REST = "...";
    public static final String OPEN_SQUARE_BRACKET = "[";
    public static final String CLOSE_SQUARE_BRACKET = "]";
    public static final String ZERO = "0";

    public static final String INTEGER = "int";
    public static final String STRING = "string";
    public static final String FLOAT = "float";
    public static final String DECIMAL = "decimal";
    public static final String NUMBER = "int|float|decimal";
    public static final String BOOLEAN = "boolean";
    public static final String NEVER = "never";
    public static final String NULL = "()";
    public static final String JSON = "json";
    public static final String UNIVERSAL_ARRAY = "json[]";
    public static final String UNIVERSAL_OBJECT = "record{}";

    public static final String ANNOTATION_MODULE = "jsondata";
    public static final String NUMBER_ANNOTATION = AT + ANNOTATION_MODULE + COLON + "NumberValidation";
    public static final String STRING_ANNOTATION = AT + ANNOTATION_MODULE + COLON + "StringValidation";
    public static final String ARRAY_ANNOTATION = AT + ANNOTATION_MODULE + COLON + "ArrayValidation";
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

    public static final String MIN_ITEMS = "minItems";
    public static final String MAX_ITEMS = "maxItems";
    public static final String UNIQUE_ITEMS = "uniqueItems";
    public static final String CONTAINS = "contains";
    public static final String MIN_CONTAINS = "minContains";
    public static final String MAX_CONTAINS = "maxContains";
    public static final String UNEVALUATED_ITEMS = "unevaluatedItems";

    private static final String INVALID_CHARS_PATTERN = ".*[!@$%^&*()_\\-|/\\\\\\s\\d].*";
    private static final String DIGIT_PATTERN = ".*\\d.*";
    private static final String STARTS_WITH_DIGIT_PATTERN = "^\\d.*";


    public static String createType(Map<String, ModuleMemberDeclarationNode> nodes, String name, Schema schema, Object type){
        if (type == Long.class) {
            return createInteger(nodes, name, schema.getMinimum(),schema.getExclusiveMinimum(), schema.getMaximum(), schema.getExclusiveMaximum(), schema.getMultipleOf());
        } else if (type == Double.class) {
            return createNumber(nodes, name, schema.getMinimum(), schema.getExclusiveMinimum(), schema.getMaximum(), schema.getExclusiveMaximum(), schema.getMultipleOf());
        } else if (type == String.class) {
            return createString(nodes, name, schema.getFormat(), schema.getMinLength(), schema.getMaxLength(), schema.getPattern());
        } else if (type == Boolean.class) {
            return BOOLEAN;
        } else if (type == null) {
            return NULL;
        } else if (type == ArrayList.class) {
            return createArray(nodes, name, schema.getPrefixItems(), schema.getItems(), schema.getContains(), schema.getMinItems(), schema.getMaxItems(), schema.getUniqueItems(), schema.getMaxContains(), schema.getMinContains(), schema.getUnevaluatedItems());
        } else {
            return createObject(nodes, name, schema.getAdditionalProperties(), schema.getProperties(), schema.getPatternProperties(), schema.getDependentSchema(), schema.getPropertyNames(), schema.getUnevaluatedProperties(), schema.getMaxProperties(), schema.getMinProperties(), schema.getDependentRequired());
        }
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
            annotation.append(MIN_LENGTH).append(COLON).append(minLength).append(COMMA);
        }
        if (maxLength!=null) {
            annotation.append(MAX_LENGTH).append(COLON).append(maxLength).append(COMMA);
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

    public static String createArray(Map<String, ModuleMemberDeclarationNode> nodes, String name, List<Object> prefixItems, Object items, Object contains, Long minItems, Long maxItems, Boolean uniqueItems, Long maxContains, Long minContains, Object unevaluatedItems) {
        ArrayList<String> arrayItems = new ArrayList<>();

        name = resolveNameConflicts(convertToPascalCase(name), nodes);
        nodes.put(name, NodeParser.parseModuleMemberDeclaration("")); // To avoid conflicts with the sub-schema name allocation.

        if (prefixItems != null) {
            for (int i = 0; i < prefixItems.size(); i++) {
                Object item = prefixItems.get(i);
                arrayItems.add(Generator.convert(item, name + "Item" + i, nodes)); // Append index to the item name
            }
        }

        if (items != null ) {
            String itemsType = Generator.convert(items, name + "RestItem", nodes); //
            if (itemsType.contains("|")){
                itemsType = OPEN_BRACES + itemsType + CLOSE_BRACES + REST;
                arrayItems.add(itemsType);
            } else {
                arrayItems.add(itemsType + REST);
            }
        } else {
            arrayItems.add(JSON + REST);
        }

        Long min = (minItems == null) ? 0L : minItems;
        Long max = (maxItems == null) ? Long.MAX_VALUE : maxItems;

        ArrayList<String> tupleList = new ArrayList<>();
        for (int i = 1; i<arrayItems.size()+1; i++) {
            if (i >= min && i <= max) {
                tupleList.add(OPEN_SQUARE_BRACKET + String.join(COMMA, arrayItems.subList(0, i)) + CLOSE_SQUARE_BRACKET);
            }
        }

        if ((minItems == null) && (maxItems == null) && (uniqueItems == null) && (contains == null)) {
            return String.join(PIPE, tupleList);
        }

        StringBuilder annotation = new StringBuilder();
        annotation.append(ARRAY_ANNOTATION).append(OPEN_BRACES);

        if (minItems != null) {
            annotation.append(MIN_ITEMS).append(COLON).append(minItems.toString()).append(COMMA);
        }
        if (maxItems != null) {
            annotation.append(MAX_ITEMS).append(COLON).append(maxItems.toString()).append(COMMA);
        }
        if (uniqueItems != null) {
            annotation.append(UNIQUE_ITEMS).append(COLON).append(uniqueItems.toString()).append(COMMA);
        }
        if (contains != null) {
            String containsRecordName = resolveNameConflicts(name + convertToPascalCase(CONTAINS), nodes);
            String newType = Generator.convert(contains, containsRecordName, nodes);

            if(newType.contains(PIPE)) {
                String typeDef = TYPE + WHITESPACE + containsRecordName + WHITESPACE + newType + SEMI_COLON;
                newType = containsRecordName;
                ModuleMemberDeclarationNode moduleNode = NodeParser.parseModuleMemberDeclaration(typeDef);
                nodes.put(containsRecordName, moduleNode);
            }

            annotation.append(CONTAINS).append(COLON).append(OPEN_BRACES);
            annotation.append(CONTAINS).append(COLON).append(newType).append(COMMA);

            if (minContains == null) {
                annotation.append(MIN_CONTAINS).append(COLON).append(ZERO).append(COMMA);
            } else {
                annotation.append(MIN_CONTAINS).append(COLON).append(minContains).append(COMMA);
            }

            if (maxContains!= null) {
                annotation.append(MAX_CONTAINS).append(COLON).append(maxContains).append(COMMA);
            }

            annotation.deleteCharAt(annotation.length() - 1).append(CLOSE_BRACES).append(COMMA);
        }

        if (unevaluatedItems!= null) { //TODO: Test this
            String UnevaluatedRecord;
            if (unevaluatedItems instanceof Boolean) {
                UnevaluatedRecord = (Boolean) unevaluatedItems ? JSON : NEVER;
            } else {
                String unevaluatedItemsRecordName = resolveNameConflicts(name + "UnevaluatedRecord", nodes);
                nodes.put(unevaluatedItemsRecordName, NodeParser.parseModuleMemberDeclaration("")); // To avoid future name allocation

                UnevaluatedRecord = Generator.convert(unevaluatedItems, unevaluatedItemsRecordName, nodes);

                if(UnevaluatedRecord.contains(PIPE)) {
                    String typeDef = TYPE + WHITESPACE + unevaluatedItemsRecordName + WHITESPACE + UnevaluatedRecord + SEMI_COLON;
                    UnevaluatedRecord = unevaluatedItemsRecordName;
                    ModuleMemberDeclarationNode moduleNode = NodeParser.parseModuleMemberDeclaration(typeDef);
                    nodes.put(unevaluatedItemsRecordName, moduleNode);
                }
            }
            annotation.append(UNEVALUATED_ITEMS).append(COLON).append(UnevaluatedRecord).append(COMMA);
        }

        annotation.deleteCharAt(annotation.length()-1).append(CLOSE_BRACES).append(NEW_LINE);
        annotation.append(TYPE).append(WHITESPACE).append(name).append(WHITESPACE).append(String.join(PIPE, tupleList)).append(SEMI_COLON);

        ModuleMemberDeclarationNode moduleNode = NodeParser.parseModuleMemberDeclaration(annotation.toString());
        nodes.put(name, moduleNode);

        return name;
    }

    public static String createObject(Map<String, ModuleMemberDeclarationNode> nodes, String name, Object additionalProperties, Map<String, Object> properties, Map<String, Object> patternProperties, Map<String, Object> dependentSchema,Object propertyNames,Object unevaluatedProperties,Long maxProperties,Long minProperties,Map<String, Object> dependentRequired){
        name = resolveNameConflicts(convertToPascalCase(name), nodes);
        nodes.put(name, NodeParser.parseModuleMemberDeclaration(""));

        if (patternProperties == null) {

        }
        return "HELLO";
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

    public static String getBallerinaType(Object type) {
        if (type == Long.class) {
            return "Integer";
        }
        if (type == Double.class) {
            return "Number";
        }
        if (type == String.class) {
            return "String";
        }
        if (type == Boolean.class) {
            return "Boolean"; // Actually not needed here as there are no keywords for type Boolean
        }
        if (type == ArrayList.class) {
            return "Array";
        }
        if (type == LinkedTreeMap.class) {
            return "Object";
        }
        return "Null";
    }

}
