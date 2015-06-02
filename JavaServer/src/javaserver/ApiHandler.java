package javaserver;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.simple.JSONObject;
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

        if(headerValues.containsKey("Client-verify"))
        {
            System.out.println(headerValues.get("Client-verify").toString());

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
        try
        {
            System.out.println("CN: " + (String)obj.get("Client-s-dn"));
            System.out.println(obj.toString());
        }
        catch(Exception e)
        {
            System.out.println("No or error in gettiing cn");
        }
        if(!obj.containsKey("action"))
        {
            retVal.put("success", false);
            retVal.put("reason", "Bad Input");;
            System.out.println(Definitions.BAD_OR_NO_ACTION_INPUT);
            return retVal;
        }
        if(!obj.containsKey(Definitions.SSL_CLIENT_VERIFY) ||
                ((String)obj.get(Definitions.SSL_CLIENT_VERIFY)).equalsIgnoreCase("success"))
        {
            retVal.put("success", false);
            retVal.put("reason","Invalid User");
            return retVal;
        }
        switch((String)obj.get("action"))
        {
            case "getMessage":
                retVal = getMessages(obj);
                break;
            case "sendMessage":
                retVal = sendMessage(obj);
                break;
            case "join":
                retVal = joinChannel(obj);
                break;
            case "leave":
                retVal = leaveChannel(obj);
                break;
            default:
                retVal.put("successs", false);
                retVal.put("reason", Definitions.BAD_OR_NO_ACTION_INPUT);
                break;
        }
        return retVal;
    }
    private JSONObject sendMessage(JSONObject req)
    {
        JSONObject retVal = new JSONObject();
                retVal.put("action","join");
        try
        {
            if(JavaServer.chat.sendMessage((String)req.get("Channel"), (String)req.get("Cn"), (String)req.get("Message"), (Date)req.get("Date")))
            {
                retVal.put("success",true);
            }
        }
        catch(Exception e)
        {
                retVal.put("success",false);
                retVal.put("reason", "Cannot send message");
        }
        return retVal;
    }
    private JSONObject leaveChannel(JSONObject req)
    {
        JSONObject retVal = new JSONObject();
                retVal.put("action","join");
        try
        {
            if(JavaServer.chat.leaveChannel((String)req.get("Cn"), (String)req.get("Channel")))
            {
                retVal.put("success",true);
            }
        }
        catch(Exception e)
        {
                retVal.put("success",false);
                retVal.put("reason", "Cannot leave channel");
        }
        return retVal;
    }
    private JSONObject joinChannel(JSONObject req)
    {
        JSONObject retVal = new JSONObject();
                retVal.put("action","join");
        try
        {
            if(JavaServer.chat.joinChannel((String)req.get("Cn"), (String)req.get("Channel")))
            {
                retVal.put("success",true);
            }
        }
        catch(Exception e)
        {
                retVal.put("success",false);
                retVal.put("reason", "Cannot join channel");
        }
        return retVal;
    }
    private JSONObject getMessages(JSONObject req)
    {
        JSONObject retVal = new JSONObject();
        try
        {
            retVal = JavaServer.chat.getMessages((String)req.get("Cn"), (Date)req.get("Date"));
            retVal.put("action", "getMessages");
        }
        catch(Exception e)
        {
            retVal.put("action", "getMessages");
            retVal.put("success", false);
            retVal.put("reason", "Invalid Input on Client App");
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
