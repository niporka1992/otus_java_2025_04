package ru.otus.jdbc.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.executor.DbExecutor;
import ru.otus.exeption.DataTemplateException;
import ru.otus.jdbc.EntityClassMetaData;
import ru.otus.jdbc.EntitySQLMetaData;

/**
 * Saves an object to the database, reads an object from the database
 */
@SuppressWarnings({"java:S1068", "java:S112"})
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityClassMetaData<T> entityClassMetaData;

    public DataTemplateJdbc(
            DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData, EntityClassMetaData<T> entityClassMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(), List.of(id), rs -> {
            try {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
                return null;
            } catch (Exception ex) {
                throw new DataTemplateException("Error mapping ResultSet in findById", ex);
            }
        });
    }

    @Override
    public List<T> findAll(Connection connection) {
        return dbExecutor
                .executeSelect(connection, entitySQLMetaData.getSelectAllSql(), List.of(), rs -> {
                    List<T> resultList = new ArrayList<>();
                    try {
                        while (rs.next()) {
                            resultList.add(mapResultSetToEntity(rs));
                        }
                        return resultList;
                    } catch (Exception ex) {
                        throw new DataTemplateException("Error mapping ResultSet in findAll", ex);
                    }
                })
                .orElseGet(ArrayList::new);
    }

    @Override
    public long insert(Connection connection, T entity) {
        List<Object> params = extractValues(entity, entityClassMetaData.getFieldsWithoutId());
        return dbExecutor.executeStatement(connection, entitySQLMetaData.getInsertSql(), params);
    }

    @Override
    public void update(Connection connection, T entity) {
        try {
            List<Object> params = extractValues(entity, entityClassMetaData.getFieldsWithoutId());
            Field idField = entityClassMetaData.getIdField();
            Object idValue = getValueByGetter(entity, idField);
            params.add(idValue);

            dbExecutor.executeStatement(connection, entitySQLMetaData.getUpdateSql(), params);
        } catch (Exception ex) {
            throw new DataTemplateException("Error updating entity in update", ex);
        }
    }

    private T mapResultSetToEntity(ResultSet rs) {
        try {
            T entity = entityClassMetaData.getConstructor().newInstance();
            for (Field field : entityClassMetaData.getAllFields()) {
                String columnName = toColumnName(field.getName());
                Object value = rs.getObject(columnName);
                if (value != null) {
                    Method setter = entity.getClass().getMethod(getSetterName(field.getName()), field.getType());
                    setter.invoke(entity, value);
                }
            }
            return entity;
        } catch (Exception ex) {
            throw new DataTemplateException("Error mapping ResultSet to entity object", ex);
        }
    }

    private List<Object> extractValues(T entity, List<Field> fields) {
        try {
            List<Object> values = new ArrayList<>();
            for (Field field : fields) {
                values.add(getValueByGetter(entity, field));
            }
            return values;
        } catch (Exception ex) {
            throw new DataTemplateException("Error extracting values from entity", ex);
        }
    }

    private Object getValueByGetter(T entity, Field field) throws Exception {
        Method getter = entity.getClass().getMethod(getGetterName(field.getName()));
        return getter.invoke(entity);
    }

    private String getSetterName(String fieldName) {
        return "set" + capitalize(fieldName);
    }

    private String getGetterName(String fieldName) {
        return "get" + capitalize(fieldName);
    }

    private String toColumnName(String fieldName) {
        return fieldName.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
