package com.devapp.devmain.json;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class JacksonJSONConvert {
    public static String jsonObject;

    public JacksonJSONConvert(Object obj) {

        ObjectMapper mapper = new ObjectMapper();

        try {

            System.out.println(mapper.writeValueAsString(obj));
            jsonObject = mapper.writeValueAsString(obj);


        } catch (JsonGenerationException ex) {
            ex.printStackTrace();
        } catch (JsonMappingException ex) {
            ex.printStackTrace();

        } catch (IOException ex) {

            ex.printStackTrace();
        }

    }

}
