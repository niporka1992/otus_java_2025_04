package ru.otus;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class CustomerService {

    private final TreeMap<Customer, String> map =
            new TreeMap<>(Comparator.comparingLong(Customer::getScores).thenComparingLong(Customer::getId));

    public Map.Entry<Customer, String> getSmallest() {
        return copyEntry(map.firstEntry());
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        Customer probe = new Customer(customer.getId(), customer.getName(), customer.getScores());
        Map.Entry<Customer, String> entry = map.higherEntry(probe);
        return copyEntry(entry);
    }

    public void add(Customer customer, String data) {
        Customer copy = new Customer(customer.getId(), customer.getName(), customer.getScores());
        map.put(copy, data);
    }

    private Map.Entry<Customer, String> copyEntry(Map.Entry<Customer, String> entry) {
        if (entry == null) return null;
        Customer original = entry.getKey();
        Customer copy = new Customer(original.getId(), original.getName(), original.getScores());
        return Map.entry(copy, entry.getValue());
    }
}
