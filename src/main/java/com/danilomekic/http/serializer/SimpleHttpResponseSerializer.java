package com.danilomekic.http.serializer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.danilomekic.http.model.HttpResponse;

public class SimpleHttpResponseSerializer implements HttpResponseSerializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHttpResponseSerializer.class);

    private ByteArrayOutputStream byteArrayOutputStream;
    private DataOutputStream dataOutputStream;

    public SimpleHttpResponseSerializer() {
        this.byteArrayOutputStream = new ByteArrayOutputStream();
        this.dataOutputStream = new DataOutputStream(byteArrayOutputStream);
    }

    @Override
    public byte[] serialize(HttpResponse httpResponse) throws IOException {
        LOGGER.info("Serializing HTTP response message");

        dataOutputStream.writeInt(httpResponse.statusCode());
        dataOutputStream.write(httpResponse.reasonPhrase().getBytes());
        dataOutputStream.write(httpResponse.reasonPhrase().getBytes());
        dataOutputStream.write(httpResponse.responseBody());

        return byteArrayOutputStream.toByteArray();
    }
}
