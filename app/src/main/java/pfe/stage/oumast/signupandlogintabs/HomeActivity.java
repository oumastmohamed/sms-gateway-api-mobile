package pfe.stage.oumast.signupandlogintabs;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{
    ArrayList<SmsObject> smsObjects=null;
    public static int jocker = 0;
    public static Context ctx=null;
    public static boolean f = true, check = true, statusdelivred= false;
    public static TextView txt1;
    public static String text="";
    Button btn_refresh;
    // for send sms :
    private final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;

    // check permession for send sms :
    public void checkPer(){
        if((int) Build.VERSION.SDK_INT >= 23){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String [] {Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }

    //for refresh veiw
    public static void refreshT(String txt){
        text=txt+"\n\n"+text;
        txt1.setText(text.toString());
        txt1.setMovementMethod(new ScrollingMovementMethod());
    }

    //broadcast receiver for receive messages and send to server
    BroadcastReceiver smsReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {

            if (Build.VERSION.SDK_INT < 19) {
                if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
                    Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
                    SmsMessage[] msgs;
                    String sender;
                    if (bundle != null) {
                        //---retrieve the SMS message received---
                        try {
                            Object[] pdus = (Object[]) bundle.get("pdus");
                            msgs = new SmsMessage[pdus.length];
                            for (int i = 0; i < msgs.length; i++) {
                                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                                sender = msgs[i].getOriginatingAddress();
                                String messageBody = msgs[i].getMessageBody();
                                Toast.makeText(getApplicationContext(), messageBody, Toast.LENGTH_LONG).show();
                                // do things here

                                try{
                                    String result = CnxToServer.GETREST("http://192.168.0.118:8080/recus?p="+sender+"&m="+messageBody);
                                    System.out.println("SMS Received "+result);
                                    refreshT(""+new Date()+" :\nfrom "+sender+" : "+messageBody+"\nStatus : Reçu");
                                    //smsObjectMsg = new SmsObject(sender, messageBody);
                                    //jsonSendMessageCustomer = new JsonSendMessageCustomer(smsObjectMsg);
                                    //result= jsonSendMessageCustomer.sendResult("http://192.168.1.122/sms/receiveSMSClient.php");
                                    System.out.println("sift l server");
                                }catch(Exception e){
                                    System.out.println("Error of sending response to server ==> "+e.getMessage());
                                }
                            }

                        } catch (Exception e) {
                            Log.d("Exception caught",e.getMessage());
                        }
                    }
                }
            } else {
                if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
                    for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                        String messageBody = smsMessage.getMessageBody();
                        String sender = smsMessage.getOriginatingAddress();
                        // do things here
                        Toast.makeText(getApplicationContext(), sender+" / "+messageBody, Toast.LENGTH_LONG).show();
                        try{
                            String result = CnxToServer.GETREST("http://192.168.0.118:8080/recus?p="+sender+"&m="+messageBody);
                            System.out.println("SMS Received "+result);
                            refreshT(""+new Date()+" :\nfrom "+sender+" : "+messageBody+"\nStatus : Reçu");
                            //smsObjectMsg = new SmsObject(sender, messageBody);
                            //jsonSendMessageCustomer = new JsonSendMessageCustomer(smsObjectMsg);
                            //result= jsonSendMessageCustomer.sendResult("http://192.168.1.122/sms/receiveSMSClient.php");
                            System.out.println("sift l server");
                        }catch(Exception e){
                            System.out.println("Error of sending response to server ==> "+e.getMessage());
                        }
                    }
                }
            }

        }};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar ac = getSupportActionBar();
        ac.hide();
        setContentView(R.layout.activity_home);
        txt1 = (TextView) findViewById(R.id.textViewlist);
        btn_refresh = (Button) findViewById(R.id.refresh);
        btn_refresh.setOnClickListener(this);
        // fastly request all permission to simplify
        requestPermissionInternet();
        // permession of send sms :
        checkPer();

        //IntentFilter intentFilter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        //intentFilter.setPriority(990);
        //registerReceiver(smsReceiver, intentFilter);
        ctx = HomeActivity.this;
        //text = (TextView) findViewById(R.id.textView1);
        btn_refresh = (Button) findViewById(R.id.refresh);
        btn_refresh.setOnClickListener(this);
        HashMap hmap = MainActivity.db.getAllrecord();
        System.out.println("-----------------> HomeActivity size de hmap : "+hmap.size());
        hmap = MainActivity.db.getAllrecord();

    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        //
        start();
    }
    //vérifier la connextion
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
    public void start(){
        boolean b1 = true, b2= true;
        // debut de boucle b1
        while(b1){
            smsObjects=null;
            refreshT("no sms !");
            sleeping(2000);
            b1= false;
            // debut de la boucle b2
            while(b2){
                if (isConnected()) {
                    HashMap<String, Object> objects = new HashMap<>();
                    String result = null;
                    //objects.put("DEVICE_ID", "80");https://oumastmohamed15.000webhostapp.com/sms.php
                    String id= null;
                    id = MainActivity.db_code.getCode();
                    result = CnxToServer.GETSMS("https://moteur.ma/fr/sms_app/send_sms_app/"+id);
                    //refreshT("----result : "+result);
                    if (result == null) {
                        refreshT("Error : The link does not connect !");
                    } else {
                        refreshT(result);
                        smsObjects = CnxToServer.ArraysmsObject(result);
                    }

                    b2=false;
                    //sleeping(5000);
                }
             //fin de boucle b2
            }
            //-------------------------------------------------------------
            if(smsObjects != null){
                int i =0;
                this.jocker = smsObjects.size();
                for(SmsObject sms : smsObjects){
                    sleeping(300);
                    //pfe.stage.oumast.signupandlogintabs.SMSLibrary s = new pfe.stage.oumast.signupandlogintabs.SMSLibrary();
                    System.out.println("nice 1");
                    //s.sendSMS(getApplicationContext(), sms);
                    Smsgateway m = new Smsgateway();
                    String id = MainActivity.db_code.getCode();
                    HashMap<String, Object> objects = new HashMap<String, Object>();
                    objects.put("sms_id", sms.getId());
                    objects.put("user_id", id);
                    objects.put("status", 1);
                    objects.put("date", new Date());
                    String result = "NOT VALUE";
                    result = m.POSTREST(objects, "https://moteur.ma/fr/sms_app/return_status_sms/");
                    refreshT("Sift id "+sms.getId());
                    refreshT("result : "+result);
                    break;
                }
            }

        }
    }

    public void sleeping(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void requestPermissionInternet(){
        // important for connection to server
        if(android.os.Build.VERSION.SDK_INT > 9){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("--------------------- > True -----> OnRequestPerResult ----- HomeActivity");
                }
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}