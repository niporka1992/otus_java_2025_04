package ru.otus.jdbc.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.annotation.Id;
import ru.otus.exeption.EntityClassMetaDataException;
import ru.otus.jdbc.EntityClassMetaData;

@SuppressWarnings({"java:S3011"})
public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {
    private static final Logger log = LoggerFactory.getLogger(EntityClassMetaDataImpl.class);

    private final Class<T> clazz;
    private final Constructor<T> constructor;
    private final Field idField;
    private final List<Field> allFields;
    private final List<Field> fieldsWithoutId;

    public EntityClassMetaDataImpl(Class<T> clazz) {
        this.clazz = Objects.requireNonNull(clazz, "Entity class must not be null");
        this.constructor = findDefaultConstructor();
        this.allFields = extractAllFields();
        this.idField = extractIdField();
        this.fieldsWithoutId =
                allFields.stream().filter(field -> !field.equals(idField)).toList();

        log.info("Initialized EntityClassMetaData for class: {}", clazz.getName());
        log.info("Fields found: {}", allFields.stream().map(Field::getName).toList());
        log.info("ID field: {}", idField.getName());
        log.info(
                "Fields without ID: {}",
                fieldsWithoutId.stream().map(Field::getName).toList());
    }

    @Override
    public String getName() {
        return clazz.getSimpleName();
    }

    @Override
    public Constructor<T> getConstructor() {
        return constructor;
    }

    @Override
    public Field getIdField() {
        return idField;
    }

    @Override
    public List<Field> getAllFields() {
        return allFields;
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return fieldsWithoutId;
    }

    private Constructor<T> findDefaultConstructor() {
        try {
            var ctor = clazz.getDeclaredConstructor();
            ctor.setAccessible(true);
            return ctor;
        } catch (NoSuchMethodException e) {
            var message = "No default constructor in class: " + clazz.getName();
            throw new EntityClassMetaDataException(message, e);
        }
    }

    private List<Field> extractAllFields() {
        var fields = clazz.getDeclaredFields();
        for (var field : fields) {
            field.setAccessible(true);
        }
        return List.of(fields);
    }

    private Field extractIdField() {
        return allFields.stream()
                .filter(f -> f.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> {
                    var message = "No field annotated with @Id in class: " + clazz.getName();
                    var ex = new EntityClassMetaDataException(message);
                    log.error(message, ex);
                    return ex;
                });
    }
}
