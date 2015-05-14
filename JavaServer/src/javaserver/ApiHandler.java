package javaserver;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
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
        //values.toArray(array);
        try{
            Set<Map.Entry<String, List<String>>> params = he.getRequestHeaders().entrySet();
            response += "\n" + params.toString();
            //access via new for-loop
            for(Map.Entry<String, List<String>> keyValSet : params) {
                List values = keyValSet.getValue();
                //print keys
                response += "\n" + (String)keyValSet.getKey();
                //print all values
                for(Object value : values)
                {
                    response += ":" + (String)value;
                }
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
