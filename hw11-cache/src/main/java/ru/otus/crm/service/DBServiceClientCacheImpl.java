package ru.otus.crm.service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cache.CacheKey;
import ru.otus.cache.HwCache;
import ru.otus.cache.HwListener;
import ru.otus.crm.model.Client;

public class DBServiceClientCacheImpl implements DBServiceClient {

    private static final Logger log = LoggerFactory.getLogger(DBServiceClientCacheImpl.class);

    private final DBServiceClient dbServiceClient;
    private final HwCache<CacheKey, Client> cache;

    @SuppressWarnings("java:S1604")
    public DBServiceClientCacheImpl(DBServiceClient dbServiceClient, HwCache<CacheKey, Client> cache) {
        this.dbServiceClient = dbServiceClient;
        this.cache = cache;

        cache.addListener(new HwListener<CacheKey, Client>() {
            @Override
            public void notify(CacheKey key, Client value, String action) {
                log.info("Cache event: {} key={}, value={}", action, key, value);
            }
        });
    }

    @Override
    public Client saveClient(Client client) {
        Client savedClient = dbServiceClient.saveClient(client);
        cache.put(createCacheKey(savedClient.getId()), savedClient);
        return savedClient;
    }

    @Override
    public Optional<Client> getClient(long id) {
        Client cachedClient = cache.get(createCacheKey(id));
        if (cachedClient != null) {
            return Optional.of(cachedClient);
        }
        Optional<Client> clientFromDb = dbServiceClient.getClient(id);
        clientFromDb.ifPresent(client -> cache.put(createCacheKey(id), client));
        return clientFromDb;
    }

    @Override
    public List<Client> findAll() {
        return dbServiceClient.findAll();
    }

    private CacheKey createCacheKey(long id) {
        return new CacheKey(id);
    }
}
