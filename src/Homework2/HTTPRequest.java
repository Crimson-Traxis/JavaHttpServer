package Homework2;

import javax.xml.ws.http.HTTPException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Hashtable;

public class HTTPRequest {

    private String generalHeader;
    private Hashtable<String,String> requestHeaders;
    private StringBuffer requestBody;

    public HTTPRequest(String requestText) throws IOException,Exception{
        requestHeaders = new Hashtable<>();
        requestBody = new StringBuffer();

        BufferedReader reader = new BufferedReader( new StringReader(requestText));

        generalHeader = reader.readLine();

        String header = reader.readLine();

        while (header.length() > 0 ) {
            int index = header.indexOf(":");
            if (index == -1) {
                throw new Exception("Header parameter is invalid: " + header);
            }
            requestHeaders.put(header.substring(0, index), header.substring(index + 1, header.length()));
            header = reader.readLine();
        }


        String bodyLine = reader.readLine();
        while (bodyLine != null) {
            requestBody.append(bodyLine).append("\r\n");
            bodyLine = reader.readLine();
        }
    }

    public String Path() {
        return generalHeader.split(" ")[1];
    }

    public String GeneralHeader() {
        return generalHeader;
    }

    public String HeaderParameter(String header) {
        return requestHeaders.get(header);
    }

    public byte[] Body() {
        return requestBody.toString().getBytes();
    }
}
