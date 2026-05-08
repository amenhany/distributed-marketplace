package org.team13.marketplace.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Override
    protected String getDatabaseName() {
        return "marketplace"; // Hard-coded to prevent "test" leak
    }

    @Override
    public MongoClient mongoClient() {
        // Connect specifically to your OrbStack router
        return MongoClients.create("mongodb://localhost:27017/marketplace");
    }

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }
}
