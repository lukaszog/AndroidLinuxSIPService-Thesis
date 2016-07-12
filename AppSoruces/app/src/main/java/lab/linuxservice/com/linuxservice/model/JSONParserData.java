package lab.linuxservice.com.linuxservice.model;

import android.app.ListActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import lab.linuxservice.com.linuxservice.interfaces.AsyncResponse;

/**
 * Created by Łukasz on 19.09.2015.
 */
public class JSONParserData {

    public String url;

    private static final String TAG_CONTACTS = "users";
    private static final String TAG_USERNAME = "usrname";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_DISPLAYNAME = "dispname";
    private static final String TAG_ADDR = "addr";
    private static final String TAG_STATE = "state";

    public AsyncResponse asyncResponse;

    public ArrayList<HashMap<String, String>> usersList = new ArrayList<HashMap<String, String>>();

    public JSONParserData(){

    }

    public JSONParserData(String url){
        this.url = url;
    }

    JSONArray users = null;

    public void setAsyc(AsyncResponse asyncResponse){
            this.asyncResponse = asyncResponse;
    }

    public ArrayList<HashMap<String, String>> getUsersList() {
        return usersList;
    }

    public void setUsersList(ArrayList<HashMap<String, String>> usersList) {
        this.usersList = usersList;
    }

    public ArrayList<HashMap<String, String>> executeJSON(String urlName) {

        JSONParser jParser = new JSONParser();

        JSONArray json = jParser.getJSONFromUrl(urlName);

        try {


            for (int i = 0; i < json.length(); i++) {

                JSONObject c = json.getJSONObject(i);

                String username = c.getString(TAG_USERNAME);
                String description = c.getString(TAG_DESCRIPTION);
                String displayname = c.getString(TAG_DISPLAYNAME);
                String addr = c.getString(TAG_ADDR);
                String state = c.getString(TAG_STATE);

                HashMap<String, String> map = new HashMap<>();


                map.put(TAG_USERNAME,username);
               // map.put(TAG_DESCRIPTION, description);
             //   map.put(TAG_DISPLAYNAME, displayname);
              ////  map.put(TAG_ADDR, addr);
             //   map.put(TAG_STATE, state);

                usersList.add(map);

                Log.d("lab", "Username: " + username);

            }
        }catch (JSONException e){
            Log.d("lab", "JSON EX: " + e.toString());
        }

        setUsersList(usersList);

        return usersList;

    }
}
