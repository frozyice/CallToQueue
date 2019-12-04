package com.frozyice.queuemanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SettingsActivity extends AppCompatActivity {

    TextView textView;
    Settings settings;
    ToggleButton toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        settings = (Settings) getIntent().getSerializableExtra("settings");
        textView = findViewById(R.id.textViewCurrent);

        toggleButton = findViewById(R.id.toggleButton);
        if (settings.IsAcceptingNewPersons())
            toggleButton.setChecked(true);
        else toggleButton.setChecked(false);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                    settings.setAcceptingNewPersons(true);
                else
                    settings.setAcceptingNewPersons(false);
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
        Intent intent = new Intent();
        intent.putExtra("settingsBack", settings);
        setResult(RESULT_OK,intent);
        finish();
    }
}
