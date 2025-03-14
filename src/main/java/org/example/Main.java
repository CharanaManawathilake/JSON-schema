package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.NodeParser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static org.example.Generator.convert;
import static org.example.GeneratorUtil.*;

public class Main {
    public static File getFileFromResources(String fileName) {
        ClassLoader classLoader = Main.class.getClassLoader();
        URL resource = Objects.requireNonNull(classLoader.getResource(fileName));
        return new File(resource.getFile());
    }

    public static void main(String[] args) {
        File jsonSchema = getFileFromResources("schema.json");

        Gson gson = new GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create();

        try {
            String jsonContent = new String(Files.readAllBytes(jsonSchema.toPath()));

            Object schema;
            //! Handle this \n issue
            if ("true\n".equals(jsonContent)) {
                schema = true;
            } else if ("false\n".equals(jsonContent)) {
                schema = false;
            } else {
                schema = gson.fromJson(jsonContent, Schema.class);
            }

            Map<String, ModuleMemberDeclarationNode> nodes = new LinkedHashMap<>();

            String typeName = convert(schema, "Schema", nodes);
            // This is just the name, it is either "int" or "Schema"
            // convert() returns the type created (if created), so the usage should be handled here.
            // Here it should be implemented if the type is "int"
            // It should not be implemented if the type is "Schema"
            if ( typeName != "Schema"){
                String schemaDefinition = TYPE + WHITESPACE+ "Schema" + WHITESPACE + typeName + SEMI_COLON;
                ModuleMemberDeclarationNode schemaNode = NodeParser.parseModuleMemberDeclaration(schemaDefinition);
                nodes.put("Schema", schemaNode);
            }
            System.out.println("Hello, World!");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}