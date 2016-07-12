package lab.linuxservice.com.linuxservice.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Łukasz on 10.08.2015.
 */
public class Preferences {


    SharedPreferences savedSettings;
    SharedPreferences.Editor editor;

    private String prefName;
    private String ipAddress;
    private String ipStatus;
    private String serverStatus;
    private String imagePath;
    private String sshServer;
    private String miniSipServer;
    private String scriptPath;
    private String wifiipaddres;
    private String internalipaddress;
    private String AppLog;
    private String vncStatus;
    private String systemRun;

    public  Preferences(){

        Log.d("lab", "Wlan0: " + Utils.getMACAddress("wlan0"));
        Log.d("lab", "eth0: " + Utils.getMACAddress("eth0"));
        Log.d("lab", "Adres IP: " + Utils.getIPAddress(true)); // IPv4
        Utils.getIPAddress(false); // IPv6

    }


    public Preferences(String prefName){
        this.prefName = prefName;
    }

    public void loadPref(Context context){

        savedSettings = context.getSharedPreferences(prefName,Context.MODE_PRIVATE);

        ipAddress = savedSettings.getString("IP", "");
        ipStatus = savedSettings.getString("IPAva", "");
        serverStatus = savedSettings.getString("WWWAva","");
        AppLog = savedSettings.getString("AppLog", "");
        systemRun = savedSettings.getString("systemRun", "");


        vncStatus = savedSettings.getString("VNC", "");
        imagePath = savedSettings.getString("imagePath", "");
        sshServer = savedSettings.getString("sshServer", "");
        miniSipServer = savedSettings.getString("miniSipServer","");
        scriptPath = savedSettings.getString("scriptPath", "");


        Log.d("lab", "Wlan0: " + Utils.getMACAddress("wlan0"));
        Log.d("lab", "eth0: " +  Utils.getMACAddress("eth0"));
        Log.d("lab", "Adres IP: " + Utils.getIPAddress(true)); // IPv4
        Utils.getIPAddress(false); // IPv6

    }

    public String getVncStatus() {
        return vncStatus;
    }

    public String getSystemRun() {
        return systemRun;
    }

    public void setSystemRun(String systemRun) {
        this.systemRun = systemRun;
    }

    public void setVncStatus(String vncStatus) {
        this.vncStatus = vncStatus;
    }

    public String getWifiipaddres() {
        return wifiipaddres;
    }

    public void setWifiipaddres(String wifiipaddres) {
        this.wifiipaddres = wifiipaddres;
    }

    public String getInternalipaddress() {
        return internalipaddress;
    }

    public void setInternalipaddress(String internalipaddress) {
        this.internalipaddress = internalipaddress;
    }

    public void savePref(Context context, String key, String value){

        savedSettings = context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
        editor = savedSettings.edit();

        editor.putString(key,value);
        editor.apply();

    }

    public String getAppLog() {
        return AppLog;
    }

    public void setAppLog(String appLog) {
        AppLog = appLog;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public void setIpStatus(String ipStatus) {
        this.ipStatus = ipStatus;
    }

    public String getIpAddress(){

        return ipStatus;
    }

    public String getIpStatus(){

        return ipStatus;
    }

    public String getServerStatus(){

        return serverStatus;
    }
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getMiniSipServer() {
        return miniSipServer;
    }

    public void setMiniSipServer(String miniSipServer) {
        this.miniSipServer = miniSipServer;
    }

    public String getSshServer() {
        return sshServer;
    }

    public void setSshServer(String sshServer) {
        this.sshServer = sshServer;
    }



}
