package javaserver;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.simple.*;
/**
 * @author Brad Minogue
 */
public class ApiHandler implements HttpHandler{
    @Override
    public void handle(HttpExchange he) throws IOException {
        Headers header = he.getRequestHeaders();
        String response = "";
        try{
            Set<Map.Entry<String, List<String>>> params = he.getRequestHeaders().entrySet();
            JSONObject obj = new JSONObject();
            for(Map.Entry<String, List<String>> part : params)
            {
                obj.put(part.getKey(), part.getValue());
            }
            response += obj.toString();
        }
        catch(Exception e)
        {
            response = e.toString();
        }
        he.sendResponseHeaders(200, response.length());
        OutputStream oout = he.getResponseBody();
        oout.write(response.getBytes());
        oout.close();
    }
}
