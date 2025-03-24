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
import java.util.*;

import static org.example.Generator.convert;
import static org.example.GeneratorUtil.*;

public class Main {
    public static final List<String> SUPPORTED_DRAFTS = List.of(
            "https://json-schema.org/draft/2020-12/schema"
    );

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
            jsonContent = jsonContent.trim();  // Removes leading/trailing spaces & newlines

            if ("true".equals(jsonContent)) {
                schema = true;
            } else if ("false".equals(jsonContent)) {
                schema = false;
            } else {
                schema = gson.fromJson(jsonContent, Schema.class);
                // A new HashMap is used to store the anchors.
                SchemaIdCollector.schemaIdCollector((Schema) schema, ((Schema) schema).get$id(), ((Schema) schema).get$id(), new HashMap<>(), new HashMap<>());

                if (((Schema) schema).get$schema() != null && !SUPPORTED_DRAFTS.contains(((Schema) schema).get$schema())){
                    throw new RuntimeException("Schema draft not supported");
                }
            }


            Map<String, ModuleMemberDeclarationNode> nodes = new LinkedHashMap<>();

            String typeName = convert(schema, DEFAULT_SCHEMA_NAME, nodes);
            // This is just the name, it is either "int" or "Schema"
            // convert() returns the type created (if created), so the usage should be handled here.
            // Here it should be implemented if the type is "int"
            // It should not be implemented if the type is "Schema"
            if ( !typeName.equals(DEFAULT_SCHEMA_NAME)){
                String schemaDefinition = TYPE + WHITESPACE+ DEFAULT_SCHEMA_NAME + WHITESPACE + typeName + SEMI_COLON;
                ModuleMemberDeclarationNode schemaNode = NodeParser.parseModuleMemberDeclaration(schemaDefinition);
                nodes.put(DEFAULT_SCHEMA_NAME, schemaNode);
            }
            System.out.println("Hello, World!");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}