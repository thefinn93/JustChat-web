package javaserver;


import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Brad Minogue
 */
public class Channel {
    ArrayList<User> users;
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
    private User getUsr(String usr)
    {
        for(User current : users)
        {
            if(current.USER_NAME.equalsIgnoreCase(usr))
                return current;
        }
        return null;
    }
    public String joinChannel(User user)
    {
        //potential whitelist implimentation support
        if(whitelist != null && !whitelist.contains(user.USER_NAME))
            return "Not on whitelist";
        users.add(user);
        JSONObject actionUpdate = new JSONObject();
        actionUpdate.put("action", "join");
        actionUpdate.put("username", user.USER_NAME);
        actionUpdate.put("channel", this.CHANNEL_NAME);
        for(User current : users)
        {
            current.addActionToTake(actionUpdate);
        }
        return null;
    }
    public String leaveChanneL(String userName)
    {
        User user = getUsr(userName);
        if(user == null)
            return "User not in channel";
        if(users.contains(user))
            users.remove(user);

        JSONObject actionUpdate = new JSONObject();
        actionUpdate.put("action", "leave");
        actionUpdate.put("username", userName);
        actionUpdate.put("channel", this.CHANNEL_NAME);
        for(User current : users)
        {
            current.addActionToTake(actionUpdate);
        }
        return null;
    }
    public String addMessage(String userName, String message, Date time)
    {
        User user = getUsr(userName);
        if(user == null)
            return "User not in channel";
        if(whitelist != null && !whitelist.contains(user.USER_NAME))
            return "Not whitelisted";

        if(!users.contains(user))
            return "User not in channel";
        log.add(new Message(user,message,time));
        JSONObject actionUpdate = new JSONObject();
        actionUpdate.put("action", "sendmsg");
        actionUpdate.put("username", userName);
        actionUpdate.put("channel", userName);
        actionUpdate.put("message", message);
        for(User current : users)
        {
            current.addActionToTake(actionUpdate);
        }
        System.out.println("[" + this.CHANNEL_NAME + "] <" + user.USER_NAME + "> " + message);
        return null;
    }
}
