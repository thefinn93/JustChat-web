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
            if(obj.containsKey("Client-Verify"))
            {
                List values = (List) obj.get("Client-Verify");
                if(!values.contains("SUCCESS"))
                {
                    response = 
                        "Please install the JustChap app to use this service."
                            +" To play with client-SSL certificates, start"
                            + "by <a href=\"/keygen\">generating a key";
                }
                else if (values.contains("SUCCESS"))
                {
                    response = "Contains ssl";
                }
                else
                {
                    response = "???";
                }
            }
            else
            {
                response += obj.toString();
            }
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
