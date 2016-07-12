package lab.linuxservice.com.linuxservice.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Map;

import lab.linuxservice.com.linuxservice.interfaces.AsyncResponse;
import lab.linuxservice.com.linuxservice.R;
import lab.linuxservice.com.linuxservice.model.RetrieveFeedTask;
import lab.linuxservice.com.linuxservice.model.Preferences;


public class HomeFragment extends Fragment implements AsyncResponse, View.OnClickListener{

    Button checkConn;
    TextView statusIP,statusWWW;
    SharedPreferences savedSettings;
    SharedPreferences.Editor editor;
    private String serverIpAddress;

    private Preferences pref;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {


        savedSettings = getActivity().getSharedPreferences("lab.linuxservice.com.linuxservice", Context.MODE_PRIVATE); //load shared preferences
        editor = savedSettings.edit();

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        statusIP = (TextView)rootView.findViewById(R.id.ipStatusText);
        statusWWW = (TextView)rootView.findViewById(R.id.sipServerStatusText);

        checkConn = (Button) rootView.findViewById(R.id.checkConn);
        checkConn.setOnClickListener(this);

        pref = new Preferences("lab.linuxservice.com.linuxservice");
        pref.loadPref(getActivity());
        Log.d("lab", pref.getServerStatus());

        return rootView;
    }

    public void onClick(View v) {

        Log.d("lab", "Jestem w onClick");

        switch(v.getId()) {

            //check connection to IP and server status
            case R.id.checkConn:

                RetrieveFeedTask task = new RetrieveFeedTask(); // new instance
                task.setAsyncResponse(this); // set itself
                task.params("checkConn"); // set job to do

                serverIpAddress = savedSettings.getString("IP", "");
                task.execute(serverIpAddress); //check connection on click
                break;
        }
    }

    public void updateView(Map<String,String> map){

        String ipStatus = map.get("IP");
        String wwwStatus = map.get("WWW");

        Log.d("lab", "ipStatus: " + ipStatus);

        if(ipStatus.equals("false")){
            statusIP.setText("Unavailable");
            editor.putString("IPAva", "no");
            //set to memory
        }
        else{
            statusIP.setText("Available");
            editor.putString("IPAva", "yes");
            //set to memory
        }

        if(wwwStatus.equals("false")){
            statusWWW.setText("Unavailable");
            editor.putString("WWWAva", "no");
            //set to memory
        }
        else{
            statusWWW.setText("Available");
            editor.putString("WWWAva", "yes");
            //set to memory
        }
        editor.apply();
        Toast.makeText(getActivity(), "Checking done ", Toast.LENGTH_SHORT).show();
    }
}

