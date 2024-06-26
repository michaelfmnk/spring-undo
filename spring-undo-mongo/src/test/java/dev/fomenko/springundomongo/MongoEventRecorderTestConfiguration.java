package dev.fomenko.springundomongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.testcontainers.containers.GenericContainer;

@SpringBootApplication
public class MongoEventRecorderTestConfiguration extends AbstractMongoClientConfiguration {
    private static final int MONGO_PORT = 27017;

    private final static GenericContainer<?> mongo = new GenericContainer<>("mongo:5.0.8")
            .withExposedPorts(MONGO_PORT);

    static {
        mongo.start();
    }

    @Override
    protected String getDatabaseName() {
        return "test";
    }


    @Override
    public MongoClient mongoClient() {
        String url = String.format("mongodb://%s:%s", mongo.getHost(), mongo.getMappedPort(MONGO_PORT));
        return MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(url))
                .build());
    }
}

