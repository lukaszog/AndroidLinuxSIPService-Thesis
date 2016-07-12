package lab.linuxservice.com.linuxservice.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.util.logging.Logger;

import lab.linuxservice.com.linuxservice.R;
import lab.linuxservice.com.linuxservice.model.FileDialog;
import lab.linuxservice.com.linuxservice.model.Preferences;
import lab.linuxservice.com.linuxservice.model.ProcessScript;

public class LinuxSettingsFragment extends Fragment implements View.OnClickListener{
	
	public LinuxSettingsFragment(){}

    Button fileChooser,scriptChooser, logView;
    TextView fileChooserText,scriptChooserText;
    ScrollView logScroll;
    CheckBox sshServer,miniSipServer,vlc;
    private Preferences preferences;

    private String imagePathStatus;
    private String miniSipServerStatus;
    private String sshServerStatus;
    private String vlcStatus;

    private Activity activity;


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_linux_settings, container, false);

        preferences = new Preferences("lab.linuxservice.com.linuxservice");

        fileChooser = (Button) rootView.findViewById(R.id.chooseFile);
        //scriptChooser = (Button) rootView.findViewById(R.id.chooseScript);
        fileChooserText = (TextView) rootView.findViewById(R.id.filePathEditText);
        //scriptChooserText = (TextView) rootView.findViewById(R.id.scriptPathEditText);
        sshServer = (CheckBox) rootView.findViewById(R.id.sshServerCheckBox);
        miniSipServer = (CheckBox) rootView.findViewById(R.id.miniSipServerCheckBox);
        vlc = (CheckBox) rootView.findViewById(R.id.vlc);

        preferences.loadPref(getActivity());

        imagePathStatus = preferences.getImagePath();
        miniSipServerStatus = preferences.getMiniSipServer();
        sshServerStatus = preferences.getSshServer();
        vlcStatus = preferences.getVncStatus();

        fileChooserText.setText(imagePathStatus);

        if(sshServerStatus.equals("yes")){
            sshServer.setChecked(true);
        }
        if(miniSipServerStatus.equals("yes")){
            miniSipServer.setChecked(true);
        }
        if(vlcStatus.equals("yes")){
            vlc.setChecked(true);
        }

        fileChooser.setOnClickListener(this);

        sshServer.setOnClickListener(this);
        miniSipServer.setOnClickListener(this);
        vlc.setOnClickListener(this);


        return rootView;
    }


    @Override
    public void onClick(View v) {

        preferences = new Preferences("lab.linuxservice.com.linuxservice");

        switch (v.getId()){


            case R.id.chooseFile:

                File sdCard = Environment.getExternalStorageDirectory();
                File mPath = new File (sdCard.getAbsolutePath());

                FileDialog fileDialog = new FileDialog(getActivity(), mPath);

                fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
                    public void fileSelected(File file) {
                        Log.d("lab", "selected file " + file.toString());
                        fileChooserText.setText(file.toString());

                        String extension = "";

                        int i = file.toString().lastIndexOf('.');
                        int p = Math.max(file.toString().lastIndexOf('/'), file.toString().lastIndexOf('\\'));

                        if (i > p) {
                            extension = file.toString().substring(i+1);
                            Log.d("lab", "Ext: " + extension);
                        }

                        if(extension.equals("img") || extension.equals("iso")){
                            preferences.savePref(getActivity(), "imagePath", file.toString());
                            Toast.makeText(getActivity(),"File has been saved",Toast.LENGTH_SHORT).show();

                        }else{
                            //do not this
                            Toast.makeText(getActivity(), "This is not image for mount", Toast.LENGTH_SHORT).show();

                        }


                    }
                });

                fileDialog.showDialog();

                break;

            case R.id.sshServerCheckBox:
                Log.d("lab", "kliknales w ssh checkbox");

                if(sshServer.isChecked()){
                    Log.d("lab","checked");
                    preferences.savePref(getActivity(),"sshServer","yes");

                }
                else{
                    Log.d("lab", "uncecked");
                    preferences.savePref(getActivity(),"sshServer","no");
                }

                break;

            case R.id.miniSipServerCheckBox:
                Log.d("lab", "kliknales w miniSipServerCheckbox");

                if(miniSipServer.isChecked()){
                    Log.d("lab", "checked");
                    preferences.savePref(getActivity(),"miniSipServer","yes");

                }
                else{
                    Log.d("lab", "uncecked");
                    preferences.savePref(getActivity(),"miniSipServer", "no");
                }

                break;
            case R.id.vlc:
                Log.d("lab", "kliknales w VNC checkbox");

                if(vlc.isChecked()){
                    Log.d("lab", "checked");
                    preferences.savePref(getActivity(),"VNC","yes");

                }
                else{
                    Log.d("lab", "uncecked");
                    preferences.savePref(getActivity(),"VNC", "no");
                }

                break;

            default:

            break;


        }

    }
}
