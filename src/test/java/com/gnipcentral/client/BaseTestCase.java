package com.gnipcentral.client;

import junit.framework.TestCase;

import java.io.*;
import java.util.Collection;
import java.util.zip.GZIPOutputStream;
import java.util.zip.GZIPInputStream;
import java.nio.charset.Charset;

public abstract class BaseTestCase extends TestCase {


    protected byte[] compress(String data) {
        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            GZIPOutputStream compressedStream = new GZIPOutputStream(result);
            new OutputStreamWriter(compressedStream, Charset.forName("UTF-8")).append(data).flush();
            compressedStream.finish();
            return result.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Something is really wrong can't gzip", e);
        }
    }

    protected String readAll(Reader stream) throws IOException {
        BufferedReader reader = new BufferedReader(stream);
        String fromReader;
        StringBuilder result = new StringBuilder();
        while ((fromReader = reader.readLine()) != null) {
            result.append(fromReader);
            result.append("\n");
        }
        result.replace(result.length() - 1, result.length(), "");
        return result.toString();
    }

    protected void assertEquals(ByteArrayInputStream expected, ByteArrayInputStream actual) {
        int fromActual;
        while ((fromActual = actual.read()) != -1) {
            assertEquals(expected.read(), fromActual);
        }
        assertEquals(expected.read(), -1);
    }

    protected String uncompress(byte[] compressedData) throws Exception {
        return readAll(new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(compressedData))));
    }

    protected void assertNotEquals(Object object1, Object object2) {
        String message = "object " + object1 + " is equal to " + object2;
        if (object1 == object2) fail(message);
        if (object1 != null && object1.equals(object2)) fail(message);
    }

    protected void assertHashNotEqual(Object object1, Object object2) {
        if (object1 == null || object2 == null) return;
        assertNotEquals(object1.hashCode(), object2.hashCode());
    }

    protected void assertHashEqual(Object object1, Object object2) {
        if (object1 == null || object2 == null) fail("hash code not equal");
        assertEquals(object1.hashCode(), object2.hashCode());
    }

    @SuppressWarnings("unchecked")
    protected void assertContains(Object expected, Collection collection) {
        String message = "Object: " + expected + " is not in " + collection;
        if (expected != null && collection == null) fail(message);
        if (expected == null && collection != null) fail(message);

        assert collection != null;
        for (Object o : collection) {
            if (expected.equals(o)) return;
        }
        fail(message);
    }

    @SuppressWarnings("unchecked")
    protected void assertDoesNotContain(Object object, Collection collection) {
        if (collection == null) return;
        assertFalse("Object " + object + " is in collection " + collection, collection.contains(object));
    }

    protected void assertIsA(Class<?> expectedClazz, Object instance) {
        assertEquals(expectedClazz, instance.getClass());
    }

    protected void assertContains(String expected, String text) {
        assertNotNull(text);
        assertTrue("expected string [" + expected + "] not in [" + text + "]", text.contains(expected));
    }

    protected void assertDoesNotContain(String expected, String string) {
        String message = "The string [" + expected + "] is in [" + string + "]";
        if (string == null) fail(message);
        assertFalse(message, string.contains(expected));
    }
}

