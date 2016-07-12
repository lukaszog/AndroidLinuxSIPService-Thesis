package lab.linuxservice.com.linuxservice.model;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import lab.linuxservice.com.linuxservice.R;
import lab.linuxservice.com.linuxservice.interfaces.AsyncResponse;

/**
 * Created by Łukasz on 2015-07-18.
 */
public class RetrieveFeedTask extends AsyncTask<String, String, Map<String, String>> {

    public ArrayList<String> str = new ArrayList<String>();
    public Map<String, String> statusMap = new HashMap<>();
    public ArrayList<HashMap<String, String>> usersList = new ArrayList<>();

    private String job;
    public AsyncResponse delegate;
    public Activity activity;


    public ArrayList<HashMap<String, String>> getUsersList() {
        return usersList;
    }

    public void setUsersList(ArrayList<HashMap<String, String>> usersList) {
        this.usersList = usersList;
    }

    public void params(String job){
        this.job = job;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setAsyncResponse(AsyncResponse delegate){
        this.delegate = delegate;
    }

    public AsyncResponse getAsyncResponse() {
        return delegate;
    }



    @Override
    protected Map<String, String> doInBackground(String... params){

        if(job.equals("checkConn")) {

            boolean reachable = true;
            boolean wwwServ = true;
            try {
                InetAddress address = InetAddress.getByName(params[0]);
                reachable = address.isReachable(10000);
                Log.d("lab", "IP: " + reachable);
            }
            catch (Exception e){
                //ip is un reachable
                Log.e("lab", e.getMessage());

            }
            try {
                (new Socket(params[0], 8080)).close();
            }
            catch (UnknownHostException e)
            {
                wwwServ = false;
            }
            catch (IOException e)
            {
                //wwwServIsUnavailable
                Log.e("lab", e.getMessage());
                wwwServ = false;
            }


            Log.d("lab", "WWW: " + wwwServ);
            Log.d("lab", "Rozmiar listy: " + Integer.toString(str.size()));

            //add values to map
            statusMap.put("IP", Boolean.toString(reachable));
            statusMap.put("WWW", Boolean.toString(wwwServ));
        }
        if(job.equals("getURL")){

            try {

                URL myUrl = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) myUrl.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();

                Log.d("lab", "The response is: " + response);

                statusMap.put("addUser", Integer.toString(response));

                Log.d("lab", "URL: " + params[0]);

            }catch (Exception e){
                Log.d("lab", "Error2: " + Log.getStackTraceString(e));
            }
        }
        if(job.equals("startScript")){

            ProcessScript processScript = new ProcessScript();

            Log.d("lab", "Jestem w FeedTask startScript");

            processScript.setActivity(activity);
            processScript.runScript("start");

          //  this.publishProgress(processScript.getLine());

        }
        if(job.equals("stopScript")){

            Log.d("lab", "Jestem w FeedTask stopScript");
            ProcessScript processScript = new ProcessScript();

            processScript.setActivity(activity);
            processScript.runScript("stop");

            //  this.publishProgress(processScript.getLine());

        }


        return statusMap;
    }

    /*
    onPostExecuthe, method call when doInBackground done task
     */
    @Override
    protected void onPostExecute(Map<String, String> result) {
        // Result is here now, may be 6 different List type.
        if(result != null || delegate != null) {
            delegate.updateView(result); //Update view
            Log.d("lab", "Skonczylem zadanie");
        }
    }

}
