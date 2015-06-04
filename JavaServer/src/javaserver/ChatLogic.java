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
    ArrayList<User> glbUser;
    ChatLogic()
    {
        channels = new ArrayList();
        glbUser = new ArrayList();
    }
    public String joinChannel(String user, String channel)
    {
        if(channel == null)
            return "channel is null";
        if(user == null)
            return "user is null";
        User currentUser = null;
        for(User current : glbUser)
        {
            if(current.USER_NAME.equalsIgnoreCase(user))
            {
                currentUser = current;
                break;
            }
        }
        if(currentUser == null)
        {
            currentUser = new User(user);
            glbUser.add(currentUser);
        }
        for(Channel current : channels)
        {
            if(current.CHANNEL_NAME.equalsIgnoreCase(channel))
            {
                return current.joinChannel(currentUser);
            }
        }
        Channel newChan = new Channel(channel);
        channels.add(newChan);
        return newChan.joinChannel(currentUser);
    }
    public String leaveChannel(String user, String channel)
    {
        if(channel == null)
            return "Channel is null";
        if(user == null)
            return "user is null";
        for(Channel current : channels)
        {
            if(current.CHANNEL_NAME.equalsIgnoreCase(channel))
                return current.leaveChanneL(user);
        }
        return "cannot find channel";
    }
    public String sendMessage(String channel, String user, String message)
    {
        Date time = new Date();
        if(channel == null)
            return "Channel is null";
        if(user == null)
            return "User name is null";
        if(message == null)
            return "Message is null";
        for(Channel current : channels)
        {
            if(current.CHANNEL_NAME.equalsIgnoreCase(channel))
            {
                System.out.println(user + " sent to " + channel);
                return current.addMessage(user, message, time);
            }
        }
        return "No channel exists under that name";
    }

    public JSONArray getUpdate(String user)
    {
        JSONArray retVal = null;
        JSONArray messages = new JSONArray();
        for(User current : glbUser)
        {
            if(current.USER_NAME.equalsIgnoreCase(user))
            {
                retVal = current.getActionsToTake();
            }
        }
        return retVal;
    }
}
