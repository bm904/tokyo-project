package com.asia.tokyo.controller.repository;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ExtendWith(SpringExtension.class)
class MongoDbSpringIntegrationTest {
    @DisplayName("given object to save"
            + " when save object using MongoDB template"
            + " then object is saved")
    @Test
    public void test(@Autowired MongoTemplate mongoTemplate) {
        // GIVEN
        DBObject objectToSave = BasicDBObjectBuilder.start().add("key", "value").get();

        // WHEN
        mongoTemplate.save(objectToSave, "collection");

        // THEN
        assertThat(mongoTemplate.findAll(DBObject.class, "collection"))
                .extracting("key")
                .containsOnly("value");
    }
}
