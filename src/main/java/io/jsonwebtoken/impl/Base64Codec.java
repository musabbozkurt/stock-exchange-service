package io.jsonwebtoken.impl;

import jakarta.xml.bind.DatatypeConverter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Base64Codec extends AbstractTextCodec {

    public String encode(byte[] data) {
        return DatatypeConverter.printBase64Binary(data);
    }

    public byte[] decode(String encoded) {
        return DatatypeConverter.parseBase64Binary(encoded);
    }
}