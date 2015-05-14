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
        String response = "Unsupported Api";
        Set values = header.keySet();
        String[] array;
        //values.toArray(array);
        try{
            Set<Map.Entry<String, List<String>>> params = he.getRequestHeaders().entrySet();
            response += "\n" + params.toString();
            
        }
        catch(Exception e)
        {
            response += "\n" + e.toString();
        }
        he.sendResponseHeaders(200, response.length());
        OutputStream oout = he.getResponseBody();
        oout.write(response.getBytes());
        oout.close();
        
    }
}
