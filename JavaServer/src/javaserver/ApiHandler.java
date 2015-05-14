package javaserver;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
        Map<String, String> params = queryToMap(he.getRequestURI().getQuery()); 
        response += "param A=" + params.get("A");
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
    public Map<String, String> queryToMap(String query){
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                result.put(pair[0], pair[1]);
            }else{
                result.put(pair[0], "");
            }
        }
        return result;
    }
}
