package Homework2;

import java.util.Hashtable;

public class HTTPResponse {

    private String generalHeader;
    private Hashtable<String,String> responseHeaders;
    private byte[] responseBody;

    public HTTPResponse() {
        responseHeaders = new Hashtable<>();
    }

    public void AddHeader(String key,String value) {
        responseHeaders.put(key,value);
    }

    private void Body(byte[] body) {
        responseBody = body;
    }

    private void GeneralHeader(String header) {
        generalHeader = header;
    }

    @Override
    public String toString() {
        String response = "";
        response += generalHeader + "\r\n";
        for (String key: responseHeaders.keySet()) {
            response += key + ": " + responseHeaders.get(key) + "\r\n";
        }
        response += "\r\n";
        response += responseBody;

        return response;
    }
}
