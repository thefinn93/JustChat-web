/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaserver;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
/**
 *
 * @author minogb
 */
public class User {
    public final String USER_NAME;
    JSONArray actions = new JSONArray();
    User(String name)
    {
        USER_NAME = name;
    }
    public String addActionToTake(JSONObject newAct)
    {
        actions.add(newAct);
        return null;
    }
    public JSONArray getActionsToTake()
    {
        JSONArray retVal = new JSONArray();
        System.out.println("Action size: " + actions.size());
        for(int i = 0; i < actions.size(); i++)
        {
            retVal.add(i, actions.get(i).toString());
        }
        emptyActions();
        return retVal;
    }
    private void emptyActions()
    {
        try
        {
            for(int i = 0; i < actions.size(); i++)
                actions.remove(i);
        }
        catch(Exception e)
        {
        }
    }
}
