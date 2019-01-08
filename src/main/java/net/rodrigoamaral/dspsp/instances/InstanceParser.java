package net.rodrigoamaral.dspsp.instances;


import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class InstanceParser {

    public <T> T parse(String filename, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        T obj = null;

        try {
            obj = mapper.readValue(new File(filename), clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return obj;
    }

}
