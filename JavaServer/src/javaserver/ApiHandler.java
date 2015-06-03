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
public class ApiHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange he) throws IOException {
        Headers header = he.getRequestHeaders();
        Set<Map.Entry<String, List<String>>> params = header.entrySet();
        String userName = null;
        String response = "";
        try
        {
            if(header.containsKey(Definitions.SSL_CLIENT_VERIFY)
                    && (header.getFirst(Definitions.SSL_CLIENT_VERIFY)).equalsIgnoreCase("success"))
            {
                // This is such a terribly way to get the username, but we're on a deadline and
                // security is no longer a priority. If additional time is available, look at
                // https://stackoverflow.com/questions/2914521/how-to-extract-cn-from-x509certificate-in-java
                userName = header.getFirst("Client-S-DN").split("CN=")[1];
                System.out.println("Username verified as " + userName);
            }
            else
            {
                JSONObject ob = new JSONObject();
                ob.put("success", false);
                ob.put("reason", "bad verify");
                response = ob.toString();
            }
        }
        catch(Exception e)
        {
                JSONObject ob = new JSONObject();
                ob.put("success", false);
                ob.put("reason", "bad ssl");
                response = ob.toString();
        }

        if(response == null)
            try{
                JSONObject obj = JSONHelper.convertToJson(he.getRequestBody());
                System.out.println(he.getRequestBody());
                if(obj != null)
                {
                    response += switchAction(obj, userName).toString();
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
        System.out.println(response);
        he.sendResponseHeaders(200, response.length());
        OutputStream oout = he.getResponseBody();
        oout.write(response.getBytes());
        oout.close();
    }
    /**
     * This function switches to the aproriate function based on action
     * @param obj
     * @param user
     * @return
     */
    public JSONObject switchAction(JSONObject obj, String user)
    {
        JSONObject retVal = new JSONObject();
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
            case "getmsg":
                retVal = getMessages(obj, user);
                break;
            case "sendmsg":
                retVal = sendMessage(obj, user);
                break;
            case "join":
                retVal = joinChannel(obj, user);
                break;
            case "leave":
                retVal = leaveChannel(obj, user);
                break;
            default:
                retVal.put("successs", false);
                retVal.put("reason", Definitions.BAD_OR_NO_ACTION_INPUT);
                break;
        }
        return retVal;
    }
    private JSONObject sendMessage(JSONObject req, String user)
    {
        JSONObject retVal = new JSONObject();
                retVal.put("action","sendmsg");
        try
        {
            if(JavaServer.chat.sendMessage((String)req.get("channel"), user, (String)req.get("message"), (Date)req.get("date")))
            {
                retVal.put("success",true);
            }
            else
            {
                retVal.put("success",false);
                retVal.put("reason", "Bad api input");
            }
        }
        catch(Exception e)
        {
                retVal.put("success",false);
                retVal.put("reason", "Cannot send message");
        }
        return retVal;
    }
    private JSONObject leaveChannel(JSONObject req, String user)
    {
        JSONObject retVal = new JSONObject();
                retVal.put("action","leave");
        try
        {
            if(JavaServer.chat.leaveChannel(user, (String)req.get("Channel")))
            {
                retVal.put("success",true);
            }
            else
            {
                retVal.put("success",false);
                retVal.put("reason", "Bad api input");
            }
        }
        catch(Exception e)
        {   
                retVal.put("success",false);
                retVal.put("reason", "Cannot leave channel");
        }
        return retVal;
    }
    private JSONObject joinChannel(JSONObject req, String user)
    {
        JSONObject retVal = new JSONObject();
                retVal.put("action","join");
        try
        {
            if(JavaServer.chat.joinChannel(user, (String)req.get("Channel")))
            {
                retVal.put("success",true);
            }
            else
            {
                retVal.put("success",false);
                retVal.put("reason", "Bad api input");
            }
        }
        catch(Exception e)
        {
                retVal.put("success",false);
                retVal.put("reason", "Cannot join channel");
        }
        return retVal;
    }
    private JSONObject getMessages(JSONObject req, String user)
    {
        JSONObject retVal = new JSONObject();
        try
        {
            retVal = JavaServer.chat.getMessages(user, (Date)req.get("Date"));
            retVal.put("action", "getmsg");
        }
        catch(Exception e)
        {
            retVal.put("action", "getmsg");
            retVal.put("success", false);
            retVal.put("reason", "Invalid Input on Client App");
        }
        return retVal;
    }
    private String convertToAlpha(String test, String user)
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
