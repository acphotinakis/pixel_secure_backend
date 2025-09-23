// backend/src/main/java/com/videogamedb/app/config/MongoEncryptionConfig.java
package com.videogamedb.app.config;

import com.videogamedb.app.annotation.EncryptedField;
import com.videogamedb.app.util.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;

import java.lang.reflect.Field;

@Configuration
public class MongoEncryptionConfig {

    @Autowired
    private EncryptionUtil encryptionUtil;

    @Bean
    public EncryptionEventListener encryptionEventListener() {
        return new EncryptionEventListener();
    }

    public class EncryptionEventListener extends AbstractMongoEventListener<Object> {

        @Override
        public void onBeforeConvert(BeforeConvertEvent<Object> event) {
            Object source = event.getSource();
            encryptFields(source);
        }

        @Override
        public void onAfterConvert(AfterConvertEvent<Object> event) {
            Object source = event.getSource();
            decryptFields(source);
        }

        private void encryptFields(Object object) {
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(EncryptedField.class)) {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(object);
                        if (value instanceof String) {
                            String encrypted = encryptionUtil.encryptField((String) value);
                            field.set(object, encrypted);
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Failed to encrypt field: " + field.getName(), e);
                    }
                }
            }
        }

        private void decryptFields(Object object) {
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(EncryptedField.class)) {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(object);
                        if (value instanceof String) {
                            String decrypted = encryptionUtil.decryptField((String) value);
                            field.set(object, decrypted);
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Failed to decrypt field: " + field.getName(), e);
                    }
                }
            }
        }
    }
}