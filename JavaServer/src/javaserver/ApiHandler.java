package javaserver;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.*;
import java.util.Properties;
/**
 * This class handles all api input
 * @author Brad Minogue
 */
public class ApiHandler implements HttpHandler{
    @Override
    public void handle(HttpExchange he) throws IOException {
        Headers header = he.getRequestHeaders();
        JSONObject headerValues = new JSONObject();
        Set<Map.Entry<String, List<String>>> params = header.entrySet();
        
        for(Map.Entry<String, List<String>> part : params)
        {
            headerValues.put(part.getKey(), part.getValue());
        }
        if(headerValues.containsKey("Client-Verify"))
        {
            System.out.println((String)headerValues.get("Client-Verify"));
            
        }
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
                obj.put("reason", Definitions.NO_API_INPUT);
                response += obj.toString();
                System.out.println(Definitions.NO_API_INPUT);
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
    /**
     * This function switches to the aproriate function based on action
     * @param obj
     * @return 
     */
    public JSONObject switchAction(JSONObject obj)
    {
        JSONObject retVal = new JSONObject();
        if(!obj.containsKey("action"))
        {
            retVal.put("success", false);
            retVal.put("reason", "Bad Input");;
            System.out.println(Definitions.BAD_OR_NO_ACTION_INPUT);
            return retVal;
        }
        switch((String)obj.get("action"))
        {
            case "register":
                retVal = runRegister(obj);
                break;
            default:
                retVal.put("successs", false);
                retVal.put("reason", Definitions.BAD_OR_NO_ACTION_INPUT);
                break;
        }
        return retVal;
    }
    /**
     * Run the register action
     * @param obj containing the json data 
     * @return success condition and necisary data in json format
     */
    public JSONObject runRegister(JSONObject obj)
    {
        JSONObject retVal = new JSONObject();
        retVal.put("success", false);
        boolean flag = obj.containsKey("CN") && obj.containsKey("csr");
        if(!flag)
        {
            retVal.put("reason", "Bad Input");
            System.out.println(Definitions.BAD_CSR_CN_API_INPUT);
            return retVal;
        }
        String userName = (String)obj.get("CN");
        convertToAlpha(userName);
        try
        {
            String[] commandList = {"openssl", "ca", "-keyfile", 
                "/etc/ssl/ca/ca.key", "-cert", "/etc/ssl/ca/ca.crt",
            "-extensions", "usr_cert", "-notext", "-md", "sha256", "-in",
            "/dev/stdin", "-subj", 
            "/countryName=US/stateOrProvinceName=Washington/localityName="
                    +"Bothell/organizationName=JustChat/JustChat "
                    +"Enterprises/commonName="+ userName};
            Process command = Runtime.getRuntime().exec(commandList);
            PrintWriter pw = new PrintWriter(command.getOutputStream());
            pw.print((String)obj.get("csr"));
            //command.waitFor();
            int extValue = 0;
            /*
            try{
                extValue = command.exitValue();
            }
            catch(Exception e)
            {
                System.out.println("error grabing exit code");
            }*/
            if(extValue == 0)
            {
                retVal.remove("success");
                retVal.put("success", true);
                String cert = outPutProccessOutput(command);
                retVal.put("cert", cert);
                retVal.put("CN", userName);
                System.out.println("Signed cert for: " + userName);
            }
            else
            {
                System.out.println("Failed to gen cert for: " + userName);
                System.out.println("exit code: " + command.exitValue());
                System.out.println(outPutProccessOutput(command));
                retVal.put("reason", "Sorry, that name is already in use");
                retVal.put("CN", userName);
            }
        }
        catch(IOException e)
        {
            System.out.println("unkown error" + e.toString());
            retVal.put("reason", "Internal Failure, Try Again Later");
            retVal.put("CN", userName);
        }
        return retVal;
    }
    private String convertToAlpha(String test)
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
    private boolean isCharAlphaNum(char test)
    {
        return Character.isLetter(test) || Character.isDigit(test);
    }
    /**
     * Prints out response from executing command
     * @param inputSource response from executing command
     * @throws IOException
     */
    private String outPutProccessOutput(Process inputSource)
    {
            String retVal = "";
        try {
            retVal = "";
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
        } catch (IOException ex) {
            retVal = ex.toString();
        }
        return retVal;
    }
}
