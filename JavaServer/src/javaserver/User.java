/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaserver;

import org.json.JSONArray;
import org.json.JSONObject;

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
        actions.put(newAct);
        return null;
    }
    public JSONArray getActionsToTake()
    {
        JSONArray retVal = new JSONArray();
        for(int i = 0; i < actions.length(); i++)
        {
            retVal.put(i, actions.get(i));
        }
        emptyActions();
        return retVal;
    }
    private void emptyActions()
    {
        actions = new JSONArray();
    }
}
