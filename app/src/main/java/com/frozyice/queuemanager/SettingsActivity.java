package com.frozyice.queuemanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class SettingsActivity extends AppCompatActivity {

    TextView textView;
    Settings settings;
    ToggleButton toggleQueue;
    ToggleButton toggleEndCalls;
    EditText editTextQueueTime;
    boolean hasQueueTimeChanged=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        settings = (Settings) getIntent().getSerializableExtra("settings");
        textView = findViewById(R.id.textViewCurrent);

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

        toggleEndCalls = findViewById(R.id.toggleEndCalls);
        if (settings.isEndingCalls())
            toggleEndCalls.setChecked(true);
        else
            toggleEndCalls.setChecked(false);

        toggleEndCalls.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    settings.setEndingCalls(true);
                else
                    settings.setEndingCalls(false);
            }
        });

        editTextQueueTime = findViewById(R.id.editTextQueueTime);

        editTextQueueTime.setText(settings.getEstimatedQueueTime()+" min");


        editTextQueueTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {
                    if (editTextQueueTime.getText().toString().length()!=0)
                    {
                        editTextQueueTime.setText("");
                        hasQueueTimeChanged=true;
                    }
                }
            }
        });

    }

    public void onReturn(View view) {
        moveBackToMain();
    }

    @Override
    public void onBackPressed() {
        moveBackToMain();
    }

    public void moveBackToMain()
    {

        if (hasQueueTimeChanged && !String.valueOf(editTextQueueTime.getText()).isEmpty())
        {
            int queueTime = Integer.valueOf(String.valueOf(editTextQueueTime.getText()));
            settings.setEstimatedQueueTime(queueTime);
        }

        Intent intent = new Intent();
        intent.putExtra("settingsBack", settings);
        setResult(RESULT_OK,intent);
        Toast.makeText(this,"Settings saved!", Toast.LENGTH_LONG).show();
        finish();
    }
}
