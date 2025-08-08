package ru.otus.performance;

import java.util.ArrayList;
import java.util.List;
import ru.otus.crm.model.Client;
import ru.otus.crm.service.DBServiceClient;

public final class PerformanceTester {

    private static final int CLIENT_COUNT = 1_000;

    private PerformanceTester() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static List<Long> testInsert(DBServiceClient service) {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < CLIENT_COUNT; i++) {
            Client saved = service.saveClient(new Client("client_" + i));
            ids.add(saved.getId());
        }
        return ids;
    }

    public static void testSelect(DBServiceClient service, List<Long> ids) {
        for (long id : ids) {
            service.getClient(id);
        }
    }
}
