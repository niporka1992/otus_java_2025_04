package ru.otus;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

@SuppressWarnings("java:S106")
public class HelloOtus {

    public static void main(String[] args) {

        Table<String, String, String> table = HashBasedTable.create();
        table.put("юзер", "имя", "Антон");
        System.out.println("Таблица: " + table);

        Multimap<String, String> tags = ArrayListMultimap.create();
        tags.put("java", "spring");
        tags.put("db", "postgres");
        System.out.println("Мультимапа: " + tags);
    }
}
