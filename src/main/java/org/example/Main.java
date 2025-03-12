package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

import static org.example.Generator.convert;

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
            Schema schema = gson.fromJson(jsonContent, Schema.class);

            Map<String, ModuleMemberDeclarationNode> nodes = new LinkedHashMap<>();

            convert(schema, "schema", nodes);
            System.out.println("Hello, World!");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}