package org.raado.utils;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

@Slf4j
@NoArgsConstructor
public class RaadoUtils {

    public static <T> Document convertToDocument(T parentClass) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(parentClass);
            return Document.parse(jsonString);
        } catch (IOException e) {
            log.error(" Error parsing json to java Object", e);
        }
        return null;
    }
}
