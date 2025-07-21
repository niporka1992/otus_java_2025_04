package ru.otus.dataprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class FileSerializer implements Serializer {
    private final String fileName;
    private final ObjectMapper mapper;

    // Включить красивый вывод .enable(SerializationFeature.INDENT_OUTPUT)
    public FileSerializer(String fileName) {
        this.fileName = Objects.requireNonNull(fileName, "File name cannot be null");
        this.mapper = new ObjectMapper().configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    }

    @Override
    public void serialize(Map<String, Double> data) {
        Objects.requireNonNull(data, "Data map cannot be null");

        try {
            File file = new File(fileName);
            Files.createParentDirs(file);

            mapper.writeValue(file, data);
        } catch (IOException e) {
            throw new FileProcessException("Error while writing to file: " + fileName, e);
        }
    }
}
