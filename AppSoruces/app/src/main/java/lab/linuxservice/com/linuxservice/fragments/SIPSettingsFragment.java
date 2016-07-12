package lab.linuxservice.com.linuxservice.fragments;



import android.app.Fragment;


import android.app.ListActivity;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lab.linuxservice.com.linuxservice.MainActivity;
import lab.linuxservice.com.linuxservice.interfaces.AsyncResponse;
import lab.linuxservice.com.linuxservice.R;
import lab.linuxservice.com.linuxservice.model.JSONParser;
import lab.linuxservice.com.linuxservice.model.JSONParserData;
import lab.linuxservice.com.linuxservice.model.RetrieveFeedTask;
import lab.linuxservice.com.linuxservice.model.Preferences;
import lab.linuxservice.com.linuxservice.model.Utils;

public class SIPSettingsFragment extends ListFragment implements View.OnClickListener, AsyncResponse {

    public SIPSettingsFragment() {
    }

    TextView username, password;
    Button adduser,showUserForm, showUserListForm, deleteButton,openWeb,ipSettings,saveData;
    public String serverIpAddress = "no";
    public String reachable = "no";
    public String UserName;
    public String UserPassword;
    public String CreateUrl;
    private Preferences pref;
    public SharedPreferences savedSettings;
    public LinearLayout linearLayout, linearLayoutUserList, linearLayoutIPSettings;
    public int showUserFormClick = 0;
    public int showUserListFormClick = 0;
    public int showIPSettingsClick = 0;
    private ListView listView;
    private ListAdapter adapter;
    private static final String TAG_USERNAME = "usrname";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_DISPLAYNAME = "dispname";
    private static final String TAG_ADDR = "addr";
    private static final String TAG_STATE = "state";
    public ArrayList<HashMap<String, String>> usersList;
    private ProgressDialog pDialog;
    private Preferences preferences;
    private AsyncResponse asyncResponse;
    SharedPreferences.Editor editor;
    EditText ipAddress;
    String ipAddr;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_sipsettings, container, false);
        View sipuserListView = inflater.inflate(R.layout.sipuser_list_item, container, false);

        username = (TextView) rootView.findViewById(R.id.username);
        password = (TextView) rootView.findViewById(R.id.password);

        adduser = (Button) rootView.findViewById(R.id.adduser);
        openWeb = (Button) rootView.findViewById(R.id.openWeb);
        ipSettings = (Button) rootView.findViewById(R.id.ipSettings);
        saveData = (Button) rootView.findViewById(R.id.saveBtn);

        ipAddress = (EditText) rootView.findViewById(R.id.ipAddr);

        //  deleteButton = (Button) rootView.findViewById(R.id.deleteSIPUser);

        showUserForm = (Button) rootView.findViewById(R.id.showAddUser);
        showUserListForm = (Button) rootView.findViewById(R.id.userListButton);


        linearLayoutUserList = (LinearLayout) rootView.findViewById(R.id.userListLayout);
        linearLayout = (LinearLayout) rootView.findViewById(R.id.addUserLayout);
        linearLayoutIPSettings = (LinearLayout) rootView.findViewById(R.id.ipSettingsLayout);

        if(showUserFormClick == 0 || showUserListFormClick == 0 || showIPSettingsClick == 0) {
            linearLayout.setVisibility(LinearLayout.GONE);
            linearLayoutUserList.setVisibility(LinearLayout.GONE);
            linearLayoutIPSettings.setVisibility(LinearLayout.GONE);

        }else{
            linearLayout.setVisibility(LinearLayout.VISIBLE);
            linearLayoutUserList.setVisibility(LinearLayout.VISIBLE);
            linearLayoutIPSettings.setVisibility(LinearLayout.VISIBLE);
        }

        preferences = new Preferences("lab.linuxservice.com.linuxservice"); //load preferences
        preferences.loadPref(getActivity()); //load preferences from activity

        String systemRun = preferences.getSystemRun();

        if(systemRun.equals("yes")){
            adduser.setEnabled(true);
            openWeb.setEnabled(true);
            ipSettings.setEnabled(true);
            showUserForm.setEnabled(true);
            showUserListForm.setEnabled(true);

        }else{
            adduser.setEnabled(false);
            openWeb.setEnabled(false);
            ipSettings.setEnabled(false);
            showUserForm.setEnabled(false);
            showUserListForm.setEnabled(false);
        }


        savedSettings = this.getActivity().getSharedPreferences("lab.linuxservice.com.linuxservice", Context.MODE_PRIVATE); //load shared preferences

        //get ip address
        serverIpAddress = savedSettings.getString("IP", "");
        //is server reachable
        reachable = savedSettings.getString("IPAva", "");



        /* onlick listeners*/
        adduser.setOnClickListener(this);
        showUserListForm.setOnClickListener(this);
        showUserForm.setOnClickListener(this);
        openWeb.setOnClickListener(this);
        ipSettings.setOnClickListener(this);

        savedSettings = this.getActivity().getSharedPreferences("lab.linuxservice.com.linuxservice", Context.MODE_PRIVATE); //load shared preferences
        editor = savedSettings.edit();

        ipAddress.setText(savedSettings.getString("IP", "")); //put ip address from shared preferences to text


        saveData.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                /* check IP address validate */



                /* check IP address validate end */

                ipAddr = ipAddress.getText().toString(); //prepare string
                editor.putString("IP", ipAddr); //put data to shared preferences
                editor.apply(); //save

                Toast.makeText(getActivity(),"Save",Toast.LENGTH_SHORT).show();
                // do something
            }
        });

//        deleteButton.setOnClickListener(this);
        /* onlick listeners */


        /* add user form  */
        pref = new Preferences("lab.linuxservice.com.linuxservice");
        pref.loadPref(getActivity());
        Log.d("lab", pref.getServerStatus());

        if (pref.getServerStatus().equals("no")) {
            disableEditText(username);
            disableEditText(password);
            adduser.setFocusable(false);
            adduser.setEnabled(false);
        }
        /* add user form end */



        return rootView;
    }




    //disable text function
    private void disableEditText(TextView editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }


    //onClick button
    @Override
    public void onClick(View v) {

        Log.d("lab", "Jestm w OnClick AddUser");
        switch (v.getId()) {

            case R.id.setDefaultIPAddress:

                ipAddress.setText(Utils.getIPAddress(true));

                ipAddr = ipAddress.getText().toString(); //prepare string
                editor.putString("IP", Utils.getIPAddress(true)); //put data to shared preferences
                editor.apply(); //save

                Toast.makeText(getActivity(),"Set and saved",Toast.LENGTH_SHORT).show();

                break;

            case R.id.adduser:

                UserName = username.getText().toString();
                UserPassword = password.getText().toString();

                if (UserName.matches("") || UserPassword.matches("")) {
                    Toast.makeText(getActivity(), "Complete all fields", Toast.LENGTH_SHORT).show();
                } else {
                    //add user to database sip server
                    UserName = "\"" + username.getText().toString() + "\"";
                    UserPassword = "\"" + password.getText().toString() + "\"";
                    InputStream is = null;
                    /*
                    http://192.168.1.122:8080/
                    openapi/localuser/set?
                    {%22syskey%22:%221234%22,%22usrname%22:%2223423432%22,%22usrpwd%22:%22555%22}
                    */
                    CreateUrl = "http://" + serverIpAddress + ":8080/openapi/localuser/set?{\"syskey\":\"1234\",\"usrname\":" + UserName + ",\"usrpwd\":" + UserPassword + "}";

                    try {
                        RetrieveFeedTask task = new RetrieveFeedTask(); // new instance
                        task.setAsyncResponse(this); // set itself
                        task.params("getURL"); // set job to do

                        Log.d("lab", "Adres: " + CreateUrl);

                        task.execute(CreateUrl);
                    } catch (Exception e) {

                        Log.d("lab", "Error: " + e.getMessage());
                    }


                }
                break;
            case R.id.showAddUser:

                if(showUserFormClick == 0) {

                    showUserFormClick = 1;
                    linearLayout.setVisibility(LinearLayout.VISIBLE);
                }
                else{
                    linearLayout.setVisibility(LinearLayout.GONE);
                    showUserFormClick=0;
                }

                break;

            case R.id.userListButton:

                if(showUserListFormClick == 0){
                    DownloadJSON downloadJSON = new DownloadJSON();
                    downloadJSON.setAsyncResponse(this);
                    downloadJSON.params("downloadJSON");
                    downloadJSON.execute();



                    showUserListFormClick = 1;
                    linearLayoutUserList.setVisibility(LinearLayout.VISIBLE);
                }
                else {
                    linearLayoutUserList.setVisibility(LinearLayout.GONE);
                    showUserListFormClick=0;
                }

                break;

            case R.id.ipSettings:

                if(showIPSettingsClick == 0){

                    showIPSettingsClick = 1;
                    linearLayoutIPSettings.setVisibility(LinearLayout.VISIBLE);
                }
                else {
                    linearLayoutIPSettings.setVisibility(LinearLayout.GONE);
                    showIPSettingsClick = 0;
                }

                break;

            case R.id.deleteSIPUser:

                final int position = listView.getPositionForView((View) v.getParent());
                Log.d("lab", "Wybrales: " + position);

                break;

            case R.id.openWeb:

                Log.d("lab", "Jeste w openWeb:");

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + serverIpAddress + ":8080/"));
                startActivity(browserIntent);

                break;

        }
    }



    public class DownloadJSON  extends AsyncTask<Void, Void, Void>  {

        public String job;
        public String usernameDelete;
        public AsyncResponse asyncResponse;

        public AsyncResponse getAsyncResponse() {
            return asyncResponse;
        }

        public void setAsyncResponse(AsyncResponse asyncResponse) {
            this.asyncResponse = asyncResponse;
        }

        public void params(String job){
            this.job = job;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        protected Void doInBackground(Void... params) {

            if(job.equals("deleteUser")){

                Log.d("lab", "delete user: " + usernameDelete);
            }

            if(job.equals("downloadJSON")) {
                usersList = new ArrayList<>();

                String urlName = "http://" + serverIpAddress + ":8080/openapi/localuser/list?{\"syskey\":\"1234\"}";

                Log.d("lab", "Adres jsona: " + urlName);
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

                        if (state.equals("0")) {
                            state = "Inactive";
                        } else {
                            state = "Active";
                        }
                        if (addr.isEmpty()) {
                            addr = "None";
                        }

                        map.put("id", Integer.toString(i));
                        map.put(TAG_USERNAME, username);
                        map.put(TAG_DESCRIPTION, description);
                        map.put(TAG_DISPLAYNAME, displayname);
                        map.put(TAG_ADDR, addr);
                        map.put(TAG_STATE, state);

                        usersList.add(map);

                        Log.d("lab", "Username: " + username);

                    }
                } catch (JSONException e) {
                    Log.d("lab", "JSON EX: " + e.toString());
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);

            if (pDialog.isShowing())
                pDialog.dismiss();

            listView = (ListView) getActivity().findViewById(android.R.id.list);

            adapter = new SimpleAdapter(
                    getActivity(),
                    usersList,
                    R.layout.sipuser_list_item,
                    new String[] { TAG_USERNAME, TAG_ADDR, TAG_STATE },
                    new int[] { R.id.username, R.id.addr, R.id.state}
            )
            {
                @Override
                public View getView (final int position, View convertView, ViewGroup parent)
                {
                    View view = super.getView(position, convertView, parent);

                    Button btn=(Button)view.findViewById(R.id.deleteSIPUser);
                    btn.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            // TODO Auto-generated method stub


                            usernameDelete = usersList.get(position).get(TAG_USERNAME);
                            Toast.makeText(getActivity(),"User: " + usernameDelete + " has been deleted",Toast.LENGTH_SHORT).show();



                            usersList.remove(position);
                            ((SimpleAdapter)adapter).notifyDataSetChanged();


                            CreateUrl = "http://" + serverIpAddress + ":8080/openapi/localuser/delete?{\"syskey\":\"1234\",\"usrname\":" + usernameDelete + "}";

                            try {
                                RetrieveFeedTask task = new RetrieveFeedTask(); // new instance
                                task.setAsyncResponse(asyncResponse);
                                task.params("getURL"); // set job to do*/

                                Log.d("lab", "Adres: " + CreateUrl);

                                task.execute(CreateUrl);
                            } catch (Exception e) {

                                Log.d("lab", "Error: " + e.getMessage());
                            }

                        }
                    });
                    return view;
                }

            };

            ((SimpleAdapter) adapter).notifyDataSetChanged();
            setListAdapter(adapter);

            // listView.setAdapter(adapter);
            Log.d("lab", "Skonczylem zadanie");
        }

    }

    @Override
    public void updateView(Map<String, String> output) {

        String response = output.get("addUser");

        //if(response.equals("200")){
        Toast.makeText(getActivity(), "Done", Toast.LENGTH_SHORT).show();
        //}
    }
}
