package ru.otus.jdbc.impl;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import ru.otus.jdbc.EntityClassMetaData;
import ru.otus.jdbc.EntitySQLMetaData;

public class EntitySQLMetaDataImpl<T> implements EntitySQLMetaData {
    private final String tableName;
    private final String idFieldName;
    private final List<String> allFieldNames;
    private final List<String> fieldsWithoutId;

    public EntitySQLMetaDataImpl(EntityClassMetaData<T> entityClassMetaData) {
        this.tableName = entityClassMetaData.getName(); // можно тут сделать toLowerCase(), если надо
        this.idFieldName = entityClassMetaData.getIdField().getName();
        this.allFieldNames =
                entityClassMetaData.getAllFields().stream().map(Field::getName).toList();
        this.fieldsWithoutId = entityClassMetaData.getFieldsWithoutId().stream()
                .map(Field::getName)
                .toList();
    }

    @Override
    public String getSelectAllSql() {
        var fields = String.join(", ", allFieldNames);
        return String.format("SELECT %s FROM %s", fields, tableName);
    }

    @Override
    public String getSelectByIdSql() {
        var fields = String.join(", ", allFieldNames);
        return String.format("SELECT %s FROM %s WHERE %s = ?", fields, tableName, idFieldName);
    }

    @Override
    public String getInsertSql() {
        var fields = String.join(", ", fieldsWithoutId);
        var placeholders = fieldsWithoutId.stream().map(f -> "?").collect(Collectors.joining(", "));
        return String.format("INSERT INTO %s(%s) VALUES (%s)", tableName, fields, placeholders);
    }

    @Override
    public String getUpdateSql() {
        var setClause = fieldsWithoutId.stream().map(f -> f + " = ?").collect(Collectors.joining(", "));
        return String.format("UPDATE %s SET %s WHERE %s = ?", tableName, setClause, idFieldName);
    }
}
