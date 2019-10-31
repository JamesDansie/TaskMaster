package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

public class Settings extends AppCompatActivity {
    public String team;

    public void onRadioButtonClicked(View view){
        boolean checked  = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radioButtonBlueTeam:
                if (checked){
                    team = "blue";
                }
                break;
            case R.id.radioButtonRedTeam:
                if(checked){
                    team = "red";
                }
                break;
            case R.id.radioButtonGreenTeam:
                if(checked)
                    team = "green";
                break;
        }
        Log.i("TeamIs",team);
    }

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
            editor.putString("team",team);
            editor.apply();
        });
    }
}
