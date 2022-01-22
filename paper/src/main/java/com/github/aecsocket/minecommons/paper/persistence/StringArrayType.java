package com.github.aecsocket.minecommons.paper.persistence;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * A PersistentDataType for storing {@link String} arrays.
 */
public class StringArrayType implements PersistentDataType<byte[], String[]> {
    /** A singleton instance of this data type. */
    public static final StringArrayType INSTANCE = new StringArrayType();

    private final Charset charset;

    /**
     * Creates an instance with a charset.
     * @param charset The charset.
     */
    public StringArrayType(Charset charset) {
        this.charset = charset;
    }

    /**
     * Creates an instance with the {@link StandardCharsets#UTF_8} charset.
     */
    public StringArrayType() {
        this(StandardCharsets.UTF_8);
    }

    /** Gets the Charset used in conversions.
     * @return The Charset.
     */
    public Charset getCharset() { return charset; }

    @Override public @NotNull Class<byte[]> getPrimitiveType() { return byte[].class; }
    @Override public @NotNull Class<String[]> getComplexType() { return String[].class; }

    @Override
    public byte @NotNull [] toPrimitive(String[] strings, PersistentDataAdapterContext context) {
        byte[][] bytes = new byte[strings.length][];
        int size = 0;
        for (int i = 0; i < bytes.length; i++) {
            byte[] stringBytes = strings[i].getBytes();
            bytes[i] = stringBytes;
            size += stringBytes.length;
        }

        ByteBuffer buffer = ByteBuffer.allocate(size + bytes.length * 4);
        for (byte[] stringBytes : bytes) {
            buffer.putInt(stringBytes.length);
            buffer.put(stringBytes);
        }

        return buffer.array();
    }

    @Override
    public String @NotNull [] fromPrimitive(byte[] bytes, PersistentDataAdapterContext context) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        List<String> strings = new ArrayList<>();

        while (buffer.remaining() > 0) {
            if (buffer.remaining() < 4)
                break;

            int length = buffer.getInt();
            if (buffer.remaining() < length)
                break;

            byte[] stringBytes = new byte[length];
            buffer.get(stringBytes);

            strings.add(new String(stringBytes, charset));
        }

        return strings.toArray(new String[0]);
    }
}
