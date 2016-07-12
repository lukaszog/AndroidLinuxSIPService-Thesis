package lab.linuxservice.com.linuxservice.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.util.Date;

import lab.linuxservice.com.linuxservice.R;
import lab.linuxservice.com.linuxservice.model.Preferences;
import lab.linuxservice.com.linuxservice.model.Utils;

public class SettingsFragment extends PreferenceFragment implements View.OnClickListener {

    Button saveData,setDefaultIP;
    //EditText ipAddress;
    SharedPreferences savedSettings;
    SharedPreferences.Editor editor;
    String ipAddr;
    public EditText ipAddress;
    public Preferences preferences;

	public SettingsFragment(){}
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        ipAddress = (EditText) rootView.findViewById(R.id.ipAddr);
        saveData = (Button) rootView.findViewById(R.id.saveBtn);
        setDefaultIP = (Button) rootView.findViewById(R.id.setDefaultIPAddress);

        setDefaultIP.setOnClickListener(this);

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

        return rootView;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.setDefaultIPAddress:

                ipAddress.setText(Utils.getIPAddress(true));

                ipAddr = ipAddress.getText().toString(); //prepare string
                editor.putString("IP", Utils.getIPAddress(true)); //put data to shared preferences
                editor.apply(); //save

                Toast.makeText(getActivity(),"Set and saved",Toast.LENGTH_SHORT).show();

                break;
        }

    }
}
