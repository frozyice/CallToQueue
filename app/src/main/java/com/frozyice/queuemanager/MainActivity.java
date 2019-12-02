package com.frozyice.queuemanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 0;



    Context context;
    BroadcastReceiver Reciver;
    String phoneNumber;

    private ListView listView;
    private List<String> phonenumbersList;
    String currentPhonenumber;
    private TextView textViewCurrent;
    private ToggleButton toggleButton;


    TempClass numberObj=new TempClass(55); //debug
    Settings settings=new Settings();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Log.wtf("PrintOut", "this is my test"); //debug




        toggleButton = findViewById(R.id.toggleButton);
        listView = findViewById(R.id.ListView);
        textViewCurrent = findViewById(R.id.textViewCurrent);
        phonenumbersList = new ArrayList<>();
        context = this;

        checkAndRequestPermissions();

        IntentFilter filter = new IntentFilter();
        filter.addAction("service.to.activity.transfer");
        Reciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {

                    if (intent.getStringExtra("number")!=null)
                    {


                        phoneNumber=intent.getStringExtra("number");
                        //if isAcceptingNewPeople == true

                        addToList(phoneNumber);
                        //else
                        //sendSms(phoneNumber,"not accepting");
                    }
                }
            }
        };
        registerReceiver(Reciver, filter);
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

    private void removeFromList() {
        phonenumbersList.remove(0);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, phonenumbersList);
        listView.setAdapter(adapter);
    }

    private void addToList(String phoneNumber) {

        if (!phonenumbersList.contains(phoneNumber)) {
            phonenumbersList.add(phoneNumber);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, phonenumbersList);
            listView.setAdapter(adapter);
            Toast.makeText(context, phoneNumber+ " added to queue!", Toast.LENGTH_LONG).show();
            sendSms(phoneNumber,"Added to queue! There are "+ String.valueOf(phonenumbersList.size()-1)+ " people before You.");
        }
        else sendSms(phoneNumber,"Already in queue! Keep Calm!");
    }



    private void sendSms(String phoneNumber, String message) {
        SmsManager smgr = SmsManager.getDefault();
        smgr.sendTextMessage(phoneNumber,null,message,null,null);
    }


    public void onNext(View view) {

        if (!phonenumbersList.isEmpty()) {
            sendSms(phonenumbersList.get(0), "Your up! It is your turn now!");
            Toast.makeText(context, "SMS sent!", Toast.LENGTH_LONG).show();
            currentPhonenumber = phonenumbersList.get(0);
            textViewCurrent.setText("Current person: " + currentPhonenumber);
            removeFromList();
        }
        else
            Toast.makeText(context, "No people in queue!", Toast.LENGTH_LONG).show();

    }

    public void onRecall(View view) {
        if (currentPhonenumber!=null) {
            sendSms(currentPhonenumber, "Your up! It is your turn now!");
            Toast.makeText(context, "SMS sent!", Toast.LENGTH_LONG).show();
        }
    }


    public void onSettings(View view) {
        // In activity or fragment

        //debug logic
        if (settings.getIsAcceptingNewPersons())
        {
            settings.setAcceptingNewPersons(false);
        }
        else settings.setAcceptingNewPersons(true);

// using context and next component class to create intent
        Intent intent = new Intent(this, SettingsActivity.class);
// using putExtra(String key, Serializable value) method
        intent.putExtra("settings", settings);
        //startActivity(intent);
        startActivityForResult(intent,42);
    }


}
