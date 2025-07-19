package ru.otus.dataprocessor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import ru.otus.model.Measurement;

public class ResourcesFileLoader implements Loader {
    private final String fileName;

    public ResourcesFileLoader(String fileName) {
        this.fileName = Objects.requireNonNull(fileName, "File name cannot be null");
    }

    @Override
    public List<Measurement> load() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new FileProcessException("File not found: " + fileName);
            }
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(inputStream, new TypeReference<>() {});
        } catch (Exception e) {
            throw new FileProcessException("Failed to load or parse file: " + fileName);
        }
    }
}
