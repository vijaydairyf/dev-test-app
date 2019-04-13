package com.devapp.devmain.ConsolidationPost;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by u_pendra on 22/1/18.
 */

public class CustomSerializable extends SerializerBase<Date> {

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public CustomSerializable() {
        this(null);
    }

    public CustomSerializable(Class t) {
        super(t);
    }

    @Override
    public void serialize(Date date, JsonGenerator generator, SerializerProvider provider)
            throws IOException, JsonProcessingException {

        generator.writeString(formatter.format(date));
    }
}