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
import java.util.Scanner;
import java.util.Set;
import org.json.JSONObject;
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
        JSONObject response = new JSONObject();
        response.put("success", false);
        response.put("reason", "An unknown error occured");
        try
        {
            if(header.containsKey(Definitions.SSL_CLIENT_VERIFY)
                    && (header.getFirst(Definitions.SSL_CLIENT_VERIFY)).equalsIgnoreCase("success"))
            {
                // This is such a terribly way to get the username, but we're on a deadline and
                // security is no longer a priority. If additional time is available, look at
                // https://stackoverflow.com/questions/2914521/how-to-extract-cn-from-x509certificate-in-java
                userName = header.getFirst("Client-S-DN").split("CN=")[1];
            }
            else
            {
                response.put("reason", "bad verify");
            }
        }
        catch(Exception e)
        {
                response.put("reason", "Bad SSL: " + e.toString());
        }

        // We were checking if something up there ^ failed in a really poor way. we should
        // instead call this code from the if statement when we have a username.
        try {
            JSONObject request = JSONHelper.convertToJson(he.getRequestBody());
            System.out.println(userName + " > " + request.toString());
            if(request != null)
            {
                response = switchAction(request, userName);
                if(response == null)
                {
                    response.put("reason", "Failed to switch action");
                }
            }
            else
            {
                response.put("reason", Definitions.NO_API_INPUT);
            }
        }
        catch(Exception e)
        {
            response.put("reason", "Failed to switch action: " + e.toString());
        }

        if(userName == null) {
          System.out.println("username is null");
        } else {
          response.put("actions", JavaServer.chat.getUpdate(userName));
        }

        String responseString = response.toString();
        System.out.println(userName + " < " + responseString);
        he.sendResponseHeaders(200, responseString.length());
        OutputStream oout = he.getResponseBody();
        oout.write(responseString.getBytes());
        oout.close();
    }
    /**
     * This function switches to the aproriate function based on action
     * @param obj
     * @param user
     * @return
     */
    public JSONObject switchAction(JSONObject request, String username)
    {
        JSONObject response = new JSONObject();
        response.put("success", false);
        response.put("reason", "Unknown failure with switchAction()");
        if(!request.has("action"))
        {
            response.put("reason", "No action key");
            return response;
        }
        switch(request.getString("action"))
        {
            case "refresh":
                response.put("success", true);
                response.put("reason", "refreshed");
                break;
            case "sendmsg":
                response = sendMessage(request, username);
                break;
            case "join":
                response = joinChannel(request, username);
                break;
            case "leave":
                response = leaveChannel(request, username);
                break;
            default:
                response.put("reason", Definitions.BAD_OR_NO_ACTION_INPUT);
                break;
        }
        return response;
    }

    private JSONObject sendMessage(JSONObject req, String username)
    {
        JSONObject response = new JSONObject();
        response.put("success", false);
        response.put("reason", "Something bad happened in the sendMessage() function.");
        try
        {
            String reason = JavaServer.chat.sendMessage(req.getString("channel"), username, req.getString("message"));
            if( reason == null)
            {
                response.put("success", true);
                response.put("reason", "Message successfully sent");
            }
            else
            {
                response.put("reason", reason);
            }
        }
        catch(Exception e)
        {
            response.put("reason", "Cannot send message: " + e.toString());
        }
        return response;
    }

    private JSONObject leaveChannel(JSONObject req, String user)
    {
        JSONObject response = new JSONObject();
        response.put("action", "leave");
        response.put("success", false);
        response.put("reason", "Unknown error in leaveChannel");
        try
        {
            String reason = JavaServer.chat.leaveChannel(user, req.getString("Channel"));
            if(reason != null)
            {
                response.put("success", true);
                response.put("reason", "left channel");
            }
            else
            {
                response.put("reason", "Failed to join channel. We don't know why. Debugging is for n00bs.");
            }
        }
        catch(Exception e)
        {
            response.put("reason", "Failed to leave channel: " + e.toString());
        }
        return response;
    }
    private JSONObject joinChannel(JSONObject req, String user)
    {
        JSONObject retVal = new JSONObject();
                retVal.put("action","join");
        try
        {
            String reason = JavaServer.chat.joinChannel(user, req.getString("channel"));
            if(reason == null)
            {
                retVal.put("success",true);
                retVal.put("reason", "joined channel");
            }
            else
            {
                retVal.put("success",false);
                retVal.put("reason", "Failed to join channel, no good reason given");
            }
        }
        catch(Exception e)
        {
                retVal.put("success",false);
                retVal.put("reason", "Cannot join channel");
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
