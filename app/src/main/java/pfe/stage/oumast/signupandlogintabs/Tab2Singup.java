package pfe.stage.oumast.signupandlogintabs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import java.util.Random;

/**
 * Created by poste05 on 29/03/2018.
 */
//First class is signup
public class Tab2Singup extends Fragment implements View.OnClickListener {
    EditText textEmail;
    Button btnSingUp;
    public static String idDevice = "Default";
    public static String mail="Null";
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2signup, container, false);
        textEmail = (EditText) rootView.findViewById(R.id.editTextEmailSignUp);
        btnSingUp = (Button) rootView.findViewById(R.id.buttonSignUp);
        btnSingUp.setOnClickListener(this);
        checkPermessionIdDevice();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getIdDevice();
    }

    public boolean isConnected(){
        final ConnectivityManager connMgr = (ConnectivityManager)
                MainActivity.ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnectedOrConnecting ()) {
            return true;
        } else if (mobile.isConnectedOrConnecting ()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        HashMap<String, String> hmap = null;
        //asc permession of get device id
        checkPermessionIdDevice();
        //get device id
        idDeveicePhone();

        if(isConnected()){
        if(validateEmail(textEmail.getText().toString())){
            mail = textEmail.getText().toString();
            mail = mail.replace("\\s+","");
            HashMap<String, Object> objects = new HashMap<String, Object>();
            objects.put("email",mail);
            objects.put("deviceid", idDevice.toString());
            String result = null;
            System.out.println("------------> send email : "+mail+" / deviceid : "+idDevice);
            Toast.makeText(MainActivity.ctx, "Wait ...", Toast.LENGTH_SHORT-1).show();
            result = CnxToServer.GET(objects,"https://moteur.ma/fr/sms_app/signup_sms/");
            if (!result.equals("")){
                int code = 0;
                code = Integer.parseInt(result);
                if(code != -1 && code !=0){
                    System.out.println("code : "+code);
                    try{
                        MainActivity.db_code.insertCode(code+"");
                        MainActivity.db_id.insertDeviceID(idDevice+"");
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.ctx, "You are successfully registered", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.ctx, MainActivity.class);
                    Bundle b = new Bundle();
                    b.putString("email", mail);
                    intent.putExtras(b);
                    startActivity(intent);
                }
                if(code ==-1){
                    Toast.makeText(MainActivity.ctx, "Already registered !", Toast.LENGTH_LONG).show();
                }
                if(code == 0){
                    Toast.makeText(MainActivity.ctx, "Email incorrect !", Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(MainActivity.ctx, "Connection failed !", Toast.LENGTH_LONG).show();
            }

        }else {
            textEmail.setError("Invalid Email");
            textEmail.requestFocus();
        }
        } else{
            Toast.makeText(MainActivity.ctx, "No internet connection !", Toast.LENGTH_LONG+7).show();
        }
    }
    //pour demande la permession
    public void checkPermessionIdDevice() {
        if(Build.VERSION.SDK_INT > 23){
            if (ContextCompat.checkSelfPermission(MainActivity.ctx,
                    Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.act,
                        Manifest.permission.READ_PHONE_STATE)) {
                    checkPermessionIdDevice();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.act,
                            new String[]{Manifest.permission.READ_PHONE_STATE},
                            PERMISSIONS_REQUEST_READ_PHONE_STATE);
                }
            }
        }
        else
            {
                getIdDevice();
        }

    }
    //pour accepter la demande de permession
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        final TelephonyManager tm = (TelephonyManager) MainActivity.baseCntx.getSystemService(Context.TELEPHONY_SERVICE);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getIdDevice();
                }
            }
        }
    }
    //pour engister l'id de telephone
    public String getIdDevice(){
        final TelephonyManager tm = (TelephonyManager) MainActivity.baseCntx.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            // this is not error
            idDevice = "" + tm.getDeviceId();
        }catch (Exception e){
            return "Default";
        }

        return idDevice;
    }
    // cree un random number
    public int randInt(int min, int max) {

        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public int generateid(){
        return randInt(10000, 1000000000);
    }
    // si le telephone ne contient pas l'id
    public void idDeveicePhone(){
        String id = getIdDevice();

        if(id.equals("Default")){
            idDevice = ""+generateid();
        }
    }
    //Return true if email is valid and false if email is invalid
    protected boolean validateEmail(String email) {
        String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
