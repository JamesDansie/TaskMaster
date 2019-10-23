package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.TextView;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button saveSettingsButton = findViewById(R.id.settingsSaveButton);
        saveSettingsButton.setOnClickListener((event) -> {
            TextView usernameTextBox = findViewById(R.id.usernameEditBox);
            String username = usernameTextBox.getText().toString();

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("username", username);
            editor.apply();
        });
    }
}
