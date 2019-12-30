package com.frozyice.queuemanager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 0;


    Context context;
    BroadcastReceiver Receiver;
    String phoneNumber;

    private ListView listView;
    private TextView textViewCurrent;
    private ToggleButton toggleQueue;

    Settings settings;
    Queue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.wtf("PrintOut", "onCreate"); //debug

        settings = new Settings();
        queue = new Queue();

        listView = findViewById(R.id.ListView);
        textViewCurrent = findViewById(R.id.textViewCurrent);

        context = this;

        toggleQueue = findViewById(R.id.toggleQueue);
        if (settings.isAcceptingNewPersons())
            toggleQueue.setChecked(true);
        else
            toggleQueue.setChecked(false);

        toggleQueue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                    settings.setAcceptingNewPersons(true);
                else
                    settings.setAcceptingNewPersons(false);
            }
        });

        checkAndRequestPermissions();

        IntentFilter filter = new IntentFilter();
        filter.addAction("service.to.activity.transfer");
        Receiver =  new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {

                    if (intent.getStringExtra("number")!=null)
                    {

                        phoneNumber=intent.getStringExtra("number");

                        if (settings.isAcceptingNewPersons())
                            addToList(phoneNumber);
                        else
                            sendSms(phoneNumber,"Not accepting new people at the moment.");

                        if(settings.isEndingCalls())
                            endCurrentCall();
                    }
                }
            }
        };
        registerReceiver(Receiver, filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(Receiver);
        Toast.makeText(context, "App is closed", Toast.LENGTH_LONG).show();
    }

    private void endCurrentCall() {
        try {
            TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            Class clazz = Class.forName(telephonyManager.getClass().getName());
            Method method = clazz.getDeclaredMethod("getITelephony");
            method.setAccessible(true);
            ITelephony telephonyService = (ITelephony) method.invoke(telephonyManager);
            telephonyService.endCall();
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private boolean checkAndRequestPermissions() {
        int sendSmsPremission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int readPhoneStatePremission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int readCallLogPremission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG);
        int callPhonePremission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (sendSmsPremission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (readPhoneStatePremission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (readCallLogPremission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CALL_LOG);
        }
        if (callPhonePremission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    private void addToList(String phoneNumber) {

       if (!queue.getPhoneNumbersList().contains(phoneNumber)) {
            queue.addToPhoneNumbersList(phoneNumber);
            updateListView();

            Toast.makeText(context, phoneNumber+ " added to queue!", Toast.LENGTH_LONG).show();
            sendSms(phoneNumber,"Added to queue! There are "+ queue.peopleBefore() + " people before You. Your estimated time: "+ queue.calculateEstimateTime(settings.getUserEstimatedQueueTime()));
        }
        else sendSms(phoneNumber,"Already in queue! Keep Calm!");
    }

    private void updateListView()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, queue.getPhoneNumbersList());
        listView.setAdapter(adapter);
    }

    private void sendSms(String phoneNumber, String message) {
        SmsManager smgr = SmsManager.getDefault();
        //smgr.sendTextMessage(phoneNumber,null,message,null,null);
        System.out.println("[SMS] to: "+phoneNumber+" msg: "+message); //debug
    }


    public void onNext(View view) {

        if (!queue.getPhoneNumbersList().isEmpty()) {
            Toast.makeText(context, "SMS sent!", Toast.LENGTH_LONG).show();
            sendSms(queue.getPhoneNumbersList().get(0), "Your up! It is your turn now!");

            queue.setNumberOfPeopleCalledIn();
            queue.setAdaptiveEstimatedQueueTime();

            queue.setCurrentPhoneNumber(queue.getPhoneNumbersList().get(0));
            queue.removeFromPhoneNumbersList();
            if (!queue.getPhoneNumbersList().isEmpty()){
                sendSms(queue.getPhoneNumbersList().get(0), "Get ready, you are next in queue!");
            }

            textViewCurrent.setText("Current person: " + queue.getCurrentPhoneNumber());
            updateListView();

        }
        else
        {
            Toast.makeText(context, "No people in queue!", Toast.LENGTH_LONG).show();
        }

    }

    public void onRecall(View view) {
        if (queue.getCurrentPhoneNumber()!=null) {
            queue.setRecallTime();
            sendSms(queue.getCurrentPhoneNumber(), "Your up! It is your turn now!");
            Toast.makeText(context, "SMS sent!", Toast.LENGTH_LONG).show();
        }
    }


    public void onSettings(View view) {
        gotoSettings();
    }

    private void gotoSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra("settings", settings);
        startActivityForResult(intent,42);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 42)
        {
            if (resultCode == RESULT_OK)
            {
                Bundle bundle = data.getExtras();

                if (bundle!=null)
                {
                    settings = (Settings) bundle.getSerializable("settingsBack");
                }
            }
        }

    }


    public void onDebug(View view) {

        settings.setAcceptingNewPersons(true);
        toggleQueue.setChecked(true);
        final int random = new Random().nextInt((5598547 - 5564787) + 1) + 5564787;
        phoneNumber=String.valueOf(random);


        addToList(phoneNumber);

        if(settings.isEndingCalls())
            endCurrentCall();
    }
}
