package com.frozyice.queuemanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    TextView textView;
    Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        settings = (Settings) getIntent().getSerializableExtra("settings");
        textView = findViewById(R.id.textViewCurrent);
        textView.setText(String.valueOf(settings.getIsAcceptingNewPersons()));
    }

    //TODO: toggle button



    public void onReturn(View view) {
        Intent intent = new Intent();
        intent.putExtra("settings", settings);
        setResult(Activity.RESULT_OK,intent);
        finish();
    }
}
