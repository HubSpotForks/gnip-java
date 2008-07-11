package com.gnipcentral.client.util;

import com.gnipcentral.client.Config;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;

public class HTTPConnection {
    private final Config config;

    public HTTPConnection(Config config) {
        this.config = config;
    }

    public InputStream doGet(String urlString) throws IOException {
        HttpURLConnection urlConnection = getConnection(urlString, Method.GET);
        return getData(urlConnection);
    }

    public InputStream doPost(String urlString, byte[] data) throws IOException {
        HttpURLConnection urlConnection = getConnection(urlString, Method.POST);
        return transferData(data, urlConnection);
    }

    public InputStream doPut(String urlString, byte[] data) throws IOException {
        HttpURLConnection urlConnection = getConnection(urlString, Method.PUT);
        return transferData(data, urlConnection);
    }

    public InputStream doDelete(String urlString) throws IOException {
        HttpURLConnection urlConnection = getConnection(urlString, Method.DELETE);
        return getData(urlConnection);
    }

    private InputStream transferData(byte[] data, HttpURLConnection urlConnection) throws IOException {
        urlConnection.setDoOutput(true);
        urlConnection.setFixedLengthStreamingMode(data.length);
        urlConnection.connect();
        OutputStream out = urlConnection.getOutputStream();
        IOUtils.copy(new ByteArrayInputStream(data), out);
        out.flush();
        int responseCode = urlConnection.getResponseCode();
        String responseMessage = urlConnection.getResponseMessage();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Error with request code: " + responseCode + " message: " + responseMessage);
        }
        InputStream resultStream;
        InputStream stream;
        String contentEncoding = urlConnection.getHeaderField("Content-Encoding");
        if ("gzip".equalsIgnoreCase(contentEncoding)) {
            stream = new GZIPInputStream(urlConnection.getInputStream());
        } else {
            stream = urlConnection.getInputStream();
        }
        ByteArrayOutputStream resultData = new ByteArrayOutputStream();
        IOUtils.copy(stream, resultData);
        resultStream = new ByteArrayInputStream(resultData.toByteArray());
        urlConnection.disconnect();
        return resultStream;
    }

    private InputStream getData(HttpURLConnection urlConnection) throws IOException {
        urlConnection.connect();
        int responseCode = urlConnection.getResponseCode();
        String responseMessage = urlConnection.getResponseMessage();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Error with request code:" + responseCode + " message: " + responseMessage);
        }
        InputStream resultStream;
        InputStream stream;
        String contentEncoding = urlConnection.getHeaderField("Content-Encoding");
        if ("gzip".equalsIgnoreCase(contentEncoding)) {
            stream = new GZIPInputStream(urlConnection.getInputStream());
        } else {
            stream = urlConnection.getInputStream();
        }
        ByteArrayOutputStream resultData = new ByteArrayOutputStream();
        IOUtils.copy(stream, resultData);
        resultStream = new ByteArrayInputStream(resultData.toByteArray());

        urlConnection.disconnect();
        return resultStream;
    }

    private HttpURLConnection getConnection(String urlString, Method method) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod(method.name());
        urlConnection.addRequestProperty("Content-Type", "application/xml");
        urlConnection.addRequestProperty("Authorization", "Basic " + new String(Base64.encodeBase64(getGnipCredentials()), Charset.forName("UTF-8")));
        urlConnection.setConnectTimeout(2000);
        urlConnection.setReadTimeout(5000);
        if (config.useGzip()) {
            urlConnection.addRequestProperty("Accept-Encoding", "gzip");
            urlConnection.addRequestProperty("Content-Encoding", "gzip");
        }
        return urlConnection;
    }

    private byte[] getGnipCredentials() {
        String credentials = config.getUsername() + ":" + config.getPassword();
        return credentials.getBytes(Charset.forName("UTF-8"));
    }
}
