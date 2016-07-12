package lab.linuxservice.com.linuxservice.model;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lab.linuxservice.com.linuxservice.R;


/**
 * Created by Łukasz on 11.08.2015.
 */
public class ProcessScript {

    public String imagePathStatus;
    public String miniSipServerStatus;
    public String sshServerStatus;
    public static String logLine;

    public String lineLog;

    private Preferences preferences;
    String imagePath, scriptPath, sshStatus, miniSipStatus, vncStatus, commandRun;

    private Activity activity;
    private final static String sExecute = "su";

    public ProcessScript(){

    }

    public String getLine() {
        return lineLog;
    }

    public void setLine(String lineLog) {
        this.lineLog = lineLog;
    }

    public void setActivity(Activity activity){
        this.activity = activity; //set activity
    }

    public void runScript(String option){

        final TextView display = (TextView) activity.findViewById(R.id.LogView);
        final ScrollView logScroll = (ScrollView) activity.findViewById(R.id.LogScrollView);

        preferences = new Preferences("lab.linuxservice.com.linuxservice"); //load preferences
        preferences.loadPref(activity); //load preferences from activity

        imagePath = preferences.getImagePath();
        scriptPath = preferences.getScriptPath();
        sshStatus = preferences.getSshServer();
        miniSipStatus = preferences.getMiniSipServer();
        vncStatus = preferences.getVncStatus();

        InputStream is =  activity.getResources().openRawResource(activity.getResources().getIdentifier("script", "raw", activity.getPackageName()));
        boolean copysuccess = false;


        File file = new File(activity.getFilesDir(), "script.sh");
        String scriptPath = file.getAbsolutePath();
        if(!file.exists()) {
            try {
                OutputStream output = new FileOutputStream(file);
                byte[] buffer = new byte[4*1024];
                int read;
                while((read = is.read(buffer))!=-1){
                    output.write(buffer,0, read);
                    Thread.sleep(10);
                }
                copysuccess = true;
                Log.d("lab", "FileOutputStream: " + output.toString());

                output.flush();
                output.close();
                is.close();
            } catch(Exception e) {
                copysuccess = false;
                // TODO perform cleanup
            }

            // perform chmod now
            if(copysuccess) {
                try {
                    Process proc = Runtime.getRuntime()
                            .exec(new String[] {"su", "-c", "chmod 755 "+ scriptPath});



                    proc.waitFor();
                } catch (Exception e) {
                    Log.d("lab", e.getMessage());
                }
            }
        }
        Log.d("lab", "ProcessScript.java");
        // Execute the script now
        try {
            //Process p=Runtime.getRuntime().exec("su");
            //  p.waitFor();


            String extension = "";

            int i = imagePath.lastIndexOf('.');
            int p = Math.max(imagePath.lastIndexOf('/'), imagePath.lastIndexOf('\\'));

            if (i > p) {
                extension = imagePath.substring(i+1);
                Log.d("lab", "Ext: " + extension);
            }

            if(extension.equals("img") || extension.equals("iso")) {


                Process proc = Runtime.getRuntime()
                        .exec(new String[]{"su", "-c", "sh " + scriptPath + " " + option + " " + imagePath + " " + miniSipStatus + " " + sshStatus + " " + vncStatus + " &"});

                //

                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(proc.getInputStream()));
                //proc.waitFor();

                Log.d("lab", "Wykonuje sykrpt...");
                Log.d("lab", "Cale polecenie..." + scriptPath + " " + option + " " + imagePath + " " + miniSipStatus + " " + sshStatus + " " + vncStatus);
                StringBuilder log = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {


                    log.append(line + "\n");
                    //Log.d("lab", line);

                    logLine = line;

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                            String currentDateandTime = sdf.format(new Date());

                            display.append(currentDateandTime + ": " + logLine + "\n");

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

                preferences.savePref(activity, "AppLog", log.toString());
                //Log.d("lab", "elo " + log);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        display.append("IP: " + ": " + Utils.getIPAddress(true) + "\n");

                        logScroll.post(new Runnable() {
                            @Override
                            public void run() {
                                logScroll.fullScroll(View.FOCUS_DOWN);
                                logScroll.clearFocus();
                            }
                        });
                    }
                });

                preferences.savePref(activity, "systemRun", "yes");
                preferences.savePref(activity,"IP", Utils.getIPAddress(true));


                proc.waitFor();
            }
            else{
                //do not this
                Toast.makeText(activity, "This is not image for mount, please check it", Toast.LENGTH_SHORT).show();

            }


        } catch (Exception e) {
            Log.d("lab", e.getMessage());
        }


    }


}
