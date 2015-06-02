package javaserver;


import java.util.ArrayList;
import java.util.Date;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author Brad Minogue
 */
public class Channel {
    ArrayList<String> users;
    //whitelist can be null
    ArrayList<String> whitelist;
    ArrayList<Message> log;
    final String CHANNEL_NAME;
    Channel(String name)
    {
        CHANNEL_NAME = name;
        log = new ArrayList();
        users = new ArrayList();
    }
    public boolean joinChannel(String user)
    {
        //potential whitelist implimentation support
        if(whitelist != null && !whitelist.contains(user))
            return false;
        users.add(user);
        return true;
    }
    public boolean leaveChanneL(String user)
    {
        if(users.contains(user))
            users.remove(user);
        return true;
    }
    public JSONObject getMessages(String user, Date time)
    {
        JSONObject retVal = new JSONObject();
        retVal.put("channel", CHANNEL_NAME);
        if(whitelist != null && whitelist.contains(user))
        {
            JSONArray messages = new JSONArray();
            for(Message current : log)
            {
                if(current.date.before(time))
                    continue;
                JSONObject crnt = new JSONObject();
                crnt.put("user", current.user);
                crnt.put("message", current.message);
                crnt.put("timestamp", current.date);
            }
            if(messages.isEmpty())
                return null;
            retVal.put("messages", messages);
        }
        return retVal;
    }
    public boolean addMessage(String user, String message, Date time)
    {
        if(whitelist != null && !whitelist.contains(user))
            return false;
        if(!users.contains(user))
            return false;
        log.add(new Message(user,message,time));
        return true;
    }
}
