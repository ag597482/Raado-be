package org.raado.utils;

import lombok.NoArgsConstructor;
import org.bson.Document;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

import static org.reflections.Reflections.log;

@NoArgsConstructor
public class RaadoUtils<T> {

    public Document convertToDocument(T parentClass) {
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
