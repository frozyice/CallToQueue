package com.frozyice.queuemanager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.internal.telephony.ITelephony;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 0;


    Context context;
    BroadcastReceiver Receiver;
    String phoneNumber;

    private TextView textViewCurrent, textViewNext, textViewQueueLength, textViewQueueEnd;
    private RecyclerView recyclerView;
    private MaterialButtonToggleGroup toggleGroup;
    private Button btnYes, btnNo;

    Queue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.wtf("PrintOut", "onCreate"); //debug

        queue = new Queue();

        textViewCurrent = findViewById(R.id.textViewCurrent);
        textViewNext = findViewById(R.id.textViewNext);
        textViewQueueLength = findViewById(R.id.textViewQueueLength);
        textViewQueueEnd = findViewById(R.id.textViewQueueEnd);
        recyclerView = findViewById(R.id.recyclerView);
        toggleGroup = findViewById(R.id.toggleGroup);
        btnYes = findViewById(R.id.btnYes);
        btnNo = findViewById(R.id.btnNo);

        context = this;

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

                        if (queue.isAcceptingNewPersons())
                            addToList(phoneNumber);
                        else
                            sendSms(phoneNumber,"Not accepting new people at the moment.");

                        if (queue.isEndingCalls())
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

        if (!queue.cardListContains(phoneNumber)) {
            queue.addToCardList(phoneNumber);

            updateView();

            Toast.makeText(context, phoneNumber+ " added to queue!", Toast.LENGTH_LONG).show();
            sendSms(phoneNumber,"Added to queue! There are "+ queue.peopleBefore() + " people before You. Your estimated call in time: "+ queue.calculateEstimateTime(queue.getUserEstimatedQueueTime()));
        }
        else sendSms(phoneNumber,"Already in queue! Keep Calm!");
    }

    private void updateView()
    {
        CardsAdapter adapter = new CardsAdapter(queue.getCardList());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        if (!queue.getCardList().isEmpty())
        {
            textViewNext.setText(queue.getCardList().get(0).getPhoneNumber());
            textViewQueueLength.setText(String.valueOf(queue.getCardList().size()));
            textViewQueueEnd.setText(queue.calculateEstimateTime(queue.getUserEstimatedQueueTime()));
        }
        else
        {
            textViewNext.setText("-");
            textViewQueueLength.setText("0");
            textViewQueueEnd.setText("-");

        }

        textViewCurrent.setText(queue.getCurrentPhoneNumber());
    }

    private void sendSms(String phoneNumber, String message) {
        SmsManager smgr = SmsManager.getDefault();
        //smgr.sendTextMessage(phoneNumber,null,message,null,null);
        System.out.println("[SMS] to: "+phoneNumber+" msg: "+message); //debug
    }


    public void onNext(View view) {

        if (!queue.getCardList().isEmpty()) {
            Toast.makeText(context, "SMS sent!", Toast.LENGTH_LONG).show();
            sendSms(queue.getCardList().get(0).getPhoneNumber(), "You are up!");

            queue.setNumberOfPeopleCalledIn();
            queue.setAdaptiveEstimatedQueueTime();

            queue.setCurrentPhoneNumber(queue.getCardList().get(0).getPhoneNumber());
            queue.removeFromPhoneNumbersList();
            if (!queue.getCardList().isEmpty()){
                sendSms(queue.getCardList().get(0).getPhoneNumber(), "Get ready, you are next in queue!");
            }

            updateView();

        }
        else
        {
            Toast.makeText(context, "No people in queue!", Toast.LENGTH_LONG).show();
        }

    }

    public void onRecall(View view) {
        if (queue.getCurrentPhoneNumber()!=null) {
            queue.setRecallTime();
            sendSms(queue.getCurrentPhoneNumber(), "You are up!");
            Toast.makeText(context, "SMS sent!", Toast.LENGTH_LONG).show();
        }
    }


/*    public void onSettings(View view) {
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

    }*/


    public void onDebug(View view) {

        queue.setAcceptingNewPersons(true);
        final int random = new Random().nextInt((5598547 - 5564787) + 1) + 5564787;
        phoneNumber=String.valueOf(random);

        addToList(phoneNumber);
    }

    public void onToggle(View view) {

        int toggledId = toggleGroup.getCheckedButtonId();

        if ( toggledId == btnYes.getId() && queue.getNumberOfPeopleCalledIn()<5 ) {

            queue.setAcceptingNewPersons(true);
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
            builder.setTitle("Please set queue time!");
            builder.setMessage("To help calculate estimated queue time, please set approximate time for one person! App will calculate adaptive queue time, if it has enough data.");
            final View customLayout = getLayoutInflater().inflate(R.layout.dialog_edittext, null);
            builder.setView(customLayout);
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {


                    EditText editText = customLayout.findViewById(R.id.editText);

                    if (!String.valueOf(editText.getText()).equals(""))
                        queue.setUserEstimatedQueueTime(Integer.valueOf(String.valueOf(editText.getText())));
                    else
                        queue.setUserEstimatedQueueTime(5);

                }
            });
            builder.show();
        }

        else if (toggledId == btnNo.getId() ) {
            queue.setAcceptingNewPersons(false);
        }
    }

    @Override
    public void onBackPressed() {

        if (queue.isAcceptingNewPersons() || queue.getCardList().size()!=0) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
            builder.setTitle("Do you like to close the app?");
            builder.setMessage("People will be not added to the queue and current queue will be lost.");
            builder.setNegativeButton("No", null);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
        }
        else
            finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!this.isFinishing()){
            queue.setEndingCalls(false);
            if(queue.isAcceptingNewPersons())
            {
                Toast.makeText(context, "Queue still open! Accepting new people!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        queue.setEndingCalls(true);
    }
}
