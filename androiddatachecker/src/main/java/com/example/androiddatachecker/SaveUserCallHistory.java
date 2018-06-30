package com.example.androiddatachecker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.CallLog;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SaveUserCallHistory{

    public static final int INCOMING = CallLog.Calls.INCOMING_TYPE;
    public static final int OUTGOING = CallLog.Calls.OUTGOING_TYPE;
    public static final int MISSED = CallLog.Calls.MISSED_TYPE;
    public static final int TOTAL = 579;
    //public static final int NUMERO_USER = TelephonyManager.
    private static String[] requiredPermissions = {Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS};
    protected Context context;
    //public Context context = (Application)getBaseContext();

    ///////////////////le context de l'application///////////////////
    public SaveUserCallHistory(Context context) {
        this.context = context;
    }

    protected void SaveUserCallHistories() {
        List<LogObject> logs = new ArrayList<>();
        SaveUserCallHistory saveUserCallHistory = new SaveUserCallHistory(context);
        //if (ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_CONTACTS") == PackageManager.PERMISSION_GRANTED) {
        /*if (ActivityCompat.checkSelfPermission((Activity)mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity)mContext, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, 10);*/
        /*if (ActivityCompat.checkSelfPermission((Activity)context, android.Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {

            return;
        }*/
        if (ActivityCompat.checkSelfPermission((Activity)context, android.Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity)context, new String[]{
                    android.Manifest.permission.READ_CALL_LOG},10);
        } else {
            //if (requiredPermissions=="Manifest.permission.READ_CALL_LOG" &&requiredPermissions=="android.permission.READ_CONTACTS"){
            Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
            int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = cursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
            while (cursor.moveToNext()) {
                InsertData(cursor.getColumnIndex(CallLog.Calls.NUMBER),cursor.getColumnIndex(CallLog.Calls.NUMBER),
                        cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION)),
                        cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)),cursor.getString(number),
                        cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE)),
                        cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE)),
                        cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE)),cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE)),"acitf");
            }

            cursor.close();

            //}
            //return logs;
        }
    }


    public int getOutgoingDuration() {
        int sum = 0;
        if (ContextCompat.checkSelfPermission(context, "android.permission.READ_CONTACTS") == PackageManager.PERMISSION_GRANTED) {

            Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                    CallLog.Calls.TYPE + " = " + CallLog.Calls.OUTGOING_TYPE, null, null);

            int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);

            while (cursor.moveToNext()) {
                String callDuration = cursor.getString(duration);
                sum += Integer.parseInt(callDuration);
            }

            cursor.close();
        }
        return sum;
    }

    public int getIncomingDuration() {
        int sum = 0;
        if (ContextCompat.checkSelfPermission(context, "android.permission.READ_CONTACTS") == PackageManager.PERMISSION_GRANTED){

            Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                    CallLog.Calls.TYPE + " = " + CallLog.Calls.INCOMING_TYPE, null, null);

        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);

        while (cursor.moveToNext()) {
            String callDuration = cursor.getString(duration);
            sum += Integer.parseInt(callDuration);
        }

        cursor.close();

    }
        return sum;
}

    public int getTotalDuration() {
        int sum = 0;
        if (ContextCompat.checkSelfPermission(context, "android.permission.READ_CONTACTS") == PackageManager.PERMISSION_GRANTED) {



            Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);

            int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);

            while (cursor.moveToNext()) {
                String callDuration = cursor.getString(duration);
                sum += Integer.parseInt(callDuration);
            }

            cursor.close();
        }
        return sum;
    }

    public String getCoolDuration(int type) {
        float sum;

        switch (type) {
            case INCOMING:
                sum = getIncomingDuration();
                break;
            case OUTGOING:
                sum = getOutgoingDuration();
                break;
            case TOTAL:
                sum = getTotalDuration();
                break;
            default:
                sum = 0;
        }

        String duration = "";
        String result;

        if (sum >= 0 && sum < 3600) {

            result = String.valueOf(sum / 60);
            String decimal = result.substring(0, result.lastIndexOf("."));
            String point = "0" + result.substring(result.lastIndexOf("."));

            int minutes = Integer.parseInt(decimal);
            float seconds = Float.parseFloat(point) * 60;

            DecimalFormat formatter = new DecimalFormat("#");
            duration = minutes + " min " + formatter.format(seconds) + " secs";

        } else if (sum >= 3600) {

            result = String.valueOf(sum / 3600);
            String decimal = result.substring(0, result.lastIndexOf("."));
            String point = "0" + result.substring(result.lastIndexOf("."));

            int hours = Integer.parseInt(decimal);
            float minutes = Float.parseFloat(point) * 60;

            DecimalFormat formatter = new DecimalFormat("#");
            duration = hours + " hrs " + formatter.format(minutes) + " min";

        }

        return duration;
    }

    protected static void InsertData(final int id_user, final int id_call, final String call_duration, final String correspondant_number,
                           final String correspondant_name, final String type_call,final String call_dat,
                           final String call_heure,final String dat_ins_call_history,final String etat) {

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("id_user",Integer.toString(id_user)));
                nameValuePairs.add(new BasicNameValuePair("id_call",Integer.toString(id_call)));
                nameValuePairs.add(new BasicNameValuePair("call_duration",call_duration));
                nameValuePairs.add(new BasicNameValuePair("correspondant_number",correspondant_number));
                nameValuePairs.add(new BasicNameValuePair("correspondant_name",correspondant_name));
                nameValuePairs.add(new BasicNameValuePair("type_call",type_call));
                nameValuePairs.add(new BasicNameValuePair("call_dat",call_dat));
                nameValuePairs.add(new BasicNameValuePair("call_heure",call_heure));
                nameValuePairs.add(new BasicNameValuePair("dat_ins_call_history",dat_ins_call_history));
                nameValuePairs.add(new BasicNameValuePair("etat",etat));

                try {
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpPost httpPost = new HttpPost("http://smart-data-tech.com/dev/API/v1/saveUserCallHistory/");
                    //HttpPost httpPost = new HttpPost("http://smart-data-tech.com/dev/fr/crud.php");

                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse httpResponse = httpClient.execute(httpPost);

                    HttpEntity httpEntity = httpResponse.getEntity();


                } catch (ClientProtocolException e) {

                } catch (IOException e) {

                }
                return "Data Inserted Successfully";
            }

            @Override
            protected void onPostExecute(String result) {

                super.onPostExecute(result);

                //Toast.makeText(MainActivity.this, "Data Submit Successfully", Toast.LENGTH_LONG).show();

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();

        sendPostReqAsyncTask.execute(Integer.toString(id_user), Integer.toString(id_call),call_duration,correspondant_number,
                correspondant_name, type_call,call_dat,
                call_heure, dat_ins_call_history,
                etat);
    }

}
