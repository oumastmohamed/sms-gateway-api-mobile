package pfe.stage.oumast.signupandlogintabs;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.Date;
import java.util.HashMap;

public class SMSLibrary {

    public void sendSMS(final Context context, final SmsObject smsObject) {
        System.out.println("nice 2");
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        final PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);

        final PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        context.registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                Smsgateway m = new Smsgateway();
                switch (getResultCode()) {

                    case Activity.RESULT_OK:
                        Log.w("LOG", "SMS send");

                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Log.w("LOG", "Generic failure");
                        //smsObject.setDate(new Date());
                        //smsObject.setStatus("fail");
                        //smsObject.setDetails("Generic failure");
                        //c.sendToServer(smsObject);
                        //m.GETFAILED(smsObject, "http://192.168.0.118:8080/aaaa?id="+smsObject.getId());
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Log.w("LOG", "No service");
                        //smsObject.setDate(new Date());
                        //smsObject.setStatus("fail");
                        //smsObject.setDetails("No service");
                        //b.sendToServer(smsObject);
                        //m.GETFAILED(smsObject, "http://192.168.0.118:8080/aaaa?id="+smsObject.getId());
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Log.w("LOG", "Null PDU");
                        //smsObject.setDate(new Date());
                        //smsObject.setStatus("fail");
                        //smsObject.setDetails("Null PDU");
                        //e.sendToServer(smsObject);
                        //m.GETFAILED(smsObject, "http://192.168.0.118:8080/aaaa?id="+smsObject.getId());
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Log.w("LOG", "Radio off");
                        //smsObject.setDate(new Date());
                        //smsObject.setStatus("fail");
                        //smsObject.setDetails("Radio off");
                        //z.sendToServer(smsObject);
                        //m.GETFAILED(smsObject, "http://192.168.0.118:8080/aaaa?id="+smsObject.getId());
                        break;
                }
            }

        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                System.out.println(smsObject);
                Smsgateway m = new Smsgateway();
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Log.w("LOG", "SMS delivered");
                        String id = MainActivity.db_code.getCode();
                        HashMap<String, Object> objects = new HashMap<String, Object>();
                        objects.put("sms_id", smsObject.getId());
                        objects.put("user_id", id);
                        objects.put("status", 1);
                        objects.put("date", new Date());
                        m.POSTREST(objects, "https://moteur.ma/fr/sms_app/return_status_sms/");
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.w("LOG", "SMS not delivered");
                        //smsObject.setDate(new Date());
                        //smsObject.setStatus("no delivered");
                        //smsObject.setDetails("SMS no delivered");
                        //System.out.println("nice broadcast 1");
                        //System.out.println("---------------------");
                        //m.GETDILIVRED(smsObject, "http://192.168.0.118:8080/updatesms?id="+smsObject.getId());

                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        System.out.println("nice 3");
        sms.sendTextMessage(smsObject.getPhone(), null, smsObject.getMessage(), sentPI, deliveredPI);
        System.out.println("nice 4");
        /* To save in Sent items */
        ContentValues values = new ContentValues();
        values.put("address", smsObject.getPhone());
        values.put("body", smsObject.getMessage());
        context.getContentResolver().insert(Uri.parse("content://sms/sent"), values);
    }

}
