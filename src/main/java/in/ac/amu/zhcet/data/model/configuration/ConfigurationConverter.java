package in.ac.amu.zhcet.data.model.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;

@Slf4j
@Converter
public class ConfigurationConverter implements AttributeConverter<ConfigurationModel, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(ConfigurationModel attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error converting model into JSON");
        }
    }

    @Override
    public ConfigurationModel convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, ConfigurationModel.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error converting JSON to model");
        }
    }
}
