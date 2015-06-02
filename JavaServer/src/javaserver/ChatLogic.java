package javaserver;

import java.util.ArrayList;
import java.util.Date;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author Brad Minogue
 */
public class ChatLogic {
    ArrayList<Channel> channels;
    ChatLogic()
    {
        channels = new ArrayList();
    }
    public boolean joinChannel(String user, String channel)
    {
        for(Channel current : channels)
        {
            if(current.CHANNEL_NAME.equalsIgnoreCase(channel))
            {
                return current.joinChannel(user);
            }
        }
        Channel newChan = new Channel(channel);
        if(!newChan.joinChannel(user))
            return false;
        return channels.add(newChan);
    }
    public boolean leaveChannel(String user, String channel)
    {
        for(Channel current : channels)
        {
            if(current.CHANNEL_NAME.equalsIgnoreCase(channel))
                return current.leaveChanneL(user);
        }
        return false;
    }
    public boolean sendMessage( String channel,String user, String message, Date time)
    {
        for(Channel current : channels)
        {
            if(current.CHANNEL_NAME.equalsIgnoreCase(channel))
            {
                return current.addMessage(user, message, time);
            }
        }
        return false;
    }
    public JSONObject getMessages(String user, Date time)
    {
        JSONObject retVal = new JSONObject();
        retVal.put("action", "getMessages");
        JSONArray messages = new JSONArray();
        for(Channel current : channels)
        {
            JSONObject val;
            val = current.getMessages(user, time);
            if(val != null)
                messages.add(val);
        }
        if(messages.isEmpty())
        {
            retVal.put("success", false);
            retVal.put("reason", "no messages");
        }
        else
        {
            retVal.put("success", true);
            retVal.put("messages", messages);
        }
        return retVal;
    }
}
