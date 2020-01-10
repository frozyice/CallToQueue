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
    EditText editTextQueueTime;
    EditText editTextAddedToQueue;
    boolean hasQueueTimeChanged=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        settings = (Settings) getIntent().getSerializableExtra("settings");
        textView = findViewById(R.id.textViewCurrent);



        editTextQueueTime = findViewById(R.id.editTextQueueTime);


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

        editTextAddedToQueue = findViewById(R.id.editTextAddedToQueue);
        editTextAddedToQueue.setText(settings.getTextAddedToQueue("5"));


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
        }

        settings.setTextAddedToQueue(String.valueOf(editTextAddedToQueue.getText()));

        Intent intent = new Intent();
        intent.putExtra("settingsBack", settings);
        setResult(RESULT_OK,intent);
        Toast.makeText(this,"Settings saved!", Toast.LENGTH_LONG).show();
        finish();
    }
}
