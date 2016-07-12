package lab.linuxservice.com.linuxservice.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;
import java.util.logging.Logger;

import lab.linuxservice.com.linuxservice.R;
import lab.linuxservice.com.linuxservice.interfaces.AsyncResponse;
import lab.linuxservice.com.linuxservice.model.Preferences;
import lab.linuxservice.com.linuxservice.model.RetrieveFeedTask;
import lab.linuxservice.com.linuxservice.model.Utils;

public class StartSystemFragment extends Fragment implements View.OnClickListener, AsyncResponse {
	
	public StartSystemFragment(){}

    Button startSystem, stopSystem;
    private Preferences preferences;
    private AsyncResponse asyncResponse;
    private  static TextView logView;
    private  static ScrollView logScroll;


    String imagePath, scriptPath, sshStatus, miniSipStatus, vncStatus, commandRun, systemRun;

    public static void showLog(){
        logView.post(new Runnable() {
            @Override
            public void run() {
                logView.setText(Logger.getGlobal().toString());

                logScroll.post(new Runnable() {
                    @Override
                    public void run() {
                        logScroll.fullScroll(View.FOCUS_DOWN);
                        logScroll.clearFocus();
                    }
                });
            }
        });
    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_start_system, container, false);


        preferences = new Preferences("lab.linuxservice.com.linuxservice"); //load preferences
        preferences.loadPref(getActivity()); //load preferences from activity

        imagePath = preferences.getImagePath();
        scriptPath = preferences.getScriptPath();
        sshStatus = preferences.getSshServer();
        miniSipStatus = preferences.getMiniSipServer();
        systemRun = preferences.getSystemRun();


        startSystem = (Button) rootView.findViewById(R.id.startSystem); //init buttons
        stopSystem = (Button) rootView.findViewById(R.id.stopSystem); //init buttons

        logView = (TextView) rootView.findViewById(R.id.LogView);
        logScroll = (ScrollView) rootView.findViewById(R.id.LogScrollView);

        startSystem.setOnClickListener(this);
        stopSystem.setOnClickListener(this);

        if(systemRun.equals("yes")){
            startSystem.setEnabled(false);
            Utils utils = new Utils();
            logView.setText("System is running");
            logView.append("IP: "+ utils.getIPAddress(true));
        }


        StringBuilder sb = new StringBuilder();

        sb.append("sh " + scriptPath);
        sb.append(" " + imagePath);
        sb.append(" " + sshStatus);
        sb.append(" " + miniSipStatus +"'");

        commandRun = sb.toString();

        String applicationLogFromPref = preferences.getAppLog();
        logView.setText(applicationLogFromPref);

        Log.d("lab", "to wykonuje: " + commandRun);

        //showLog();

        return rootView;
    }

    @Override
    public void onClick(View v){

        switch (v.getId()){

            case R.id.stopSystem:

                Log.d("lab", "Stop system");
                RetrieveFeedTask task = new RetrieveFeedTask(); // new instance
                task.setAsyncResponse(this);
                task.setActivity(getActivity());
                task.params("stopScript"); // set job to do
                task.execute();
                startSystem.setEnabled(true);
                preferences.savePref(getActivity(),"systemRun","no");


                break;

            case R.id.startSystem:

                Log.d("lab", "Start system");
                RetrieveFeedTask taskR = new RetrieveFeedTask(); // new instance
                taskR.setAsyncResponse(this);
                taskR.setActivity(getActivity());
                taskR.params("startScript"); // set job to do
                taskR.execute();

                startSystem.setEnabled(false);

                break;

            default:

                break;

        }
    }

    @Override
    public void updateView(Map<String, String> output) {

    }

}
