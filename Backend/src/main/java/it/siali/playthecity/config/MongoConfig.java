package it.siali.playthecity.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

    @Bean
    public MongoClient mongoClient() {
        // INCOLLA QUI L'URI DI ATLAS (quello corretto, con un solo '?')

        String uri = "mongodb://leonida2390_db_user:9nI6jJ94AG0ecqUj@ac-biyecma-shard-00-00.lxv7ug2.mongodb.net:27017,ac-biyecma-shard-00-01.lxv7ug2.mongodb.net:27017,ac-biyecma-shard-00-02.lxv7ug2.mongodb.net:27017/?ssl=true&replicaSet=atlas-xp8wz0-shard-0&authSource=admin&appName=Cluster0&tlsAllowInvalidCertificates=true";
        ConnectionString connectionString = new ConnectionString(uri);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();

        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), "PlayTheCity");
    }
}