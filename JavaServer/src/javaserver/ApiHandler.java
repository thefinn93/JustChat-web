package javaserver;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
            JSONObject obj = JSONHelper.convertToJson(he.getRequestBody());
            if(obj != null)
            {
                response += switchAction(obj).toString();
            }
            else
            {
                obj = new JSONObject();
                obj.put("success", false);
                obj.put("reason", "Internal");
                response += obj.toString();
            }
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
    public JSONObject switchAction(JSONObject obj)
    {
        JSONObject retVal = new JSONObject();
        if(!obj.containsKey("action"))
        {
            retVal.put("success", false);
            retVal.put("reason", "Bad Input");;
            System.out.println("No action");
            return retVal;
        }
        switch((String)obj.get("action"))
        {
            case "register":
                retVal = runRegister(obj);
                break;
            default:
                break;
        }
        return retVal;
    }
    public JSONObject runRegister(JSONObject obj)
    {
        JSONObject retVal = new JSONObject();
        boolean flag = obj.containsKey("CN") && obj.containsKey("csr");
        if(flag)
        {
            retVal.put("success", false);
            retVal.put("reason", "Bad Input");
            System.out.println("No CN/csr");
            return retVal;
        }
        String userName = (String)obj.get("CN");
        convertToAlpha(userName);
        try
        {
            Process responceFromCommand = Runtime.getRuntime().exec(
                    "openssl ca -keyfile /etc/ssl/ca/ca.key "
                            +"-cert /etc/ssl/ca/ca.crt -extensions usr_cert "
                            +"-notext -md sha256 -in /tmp/1432063090.pem -subj "
                            +"'/countryName=US/stateOrProvinceName=Washington/"
                            +"localityName=Bothell/organizationName=JustChat "
                            + "Enterprises/commonName="+ userName+"'");
            
            retVal.put("success", true);
            String cert = outPutProccessOutput(responceFromCommand);
            retVal.put("cert", cert);
            retVal.put("CN", userName);
        }
        catch(Exception e)
        {
            retVal.put("success", false);
            retVal.put("reason", "Sorry, that name is already in use");
            retVal.put("CN", userName);
        }
        return retVal;
    }
    public String convertToAlpha(String test)
    {
        String retVal = "";
        for(int i = 0; i < test.length(); i++)
        {
            if(isCharAlphaNum(test.charAt(i)))
            {
                retVal+=test.charAt(i);
            }
        }
        return retVal;
    }
    public boolean isCharAlphaNum(char test)
    {
        return Character.isLetter(test) || Character.isDigit(test);
    }
    /**
     * Prints out response from executing command
     * @param inputSource response from executing command
     * @throws IOException
     */
    private String outPutProccessOutput(Process inputSource) throws IOException
    {
        String retVal = "";
        BufferedReader stdInput = new BufferedReader(
                new InputStreamReader(inputSource.getInputStream()));
        BufferedReader stdError = new BufferedReader(
                new InputStreamReader(inputSource.getErrorStream()));
        String tempVal = "";
        while((tempVal = stdInput.readLine()) != null)
        {
            retVal +=tempVal;
        }
        tempVal = "";
        while((tempVal = stdError.readLine()) != null)
        {
            retVal +=tempVal;
        }
        return retVal;
    }
}
