package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.amazonaws.amplify.generated.graphql.ListTeamsQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.util.LinkedList;

import javax.annotation.Nonnull;

public class Settings extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private String team;
    private LinkedList<ListTeamsQuery.Item> teams;
    private AWSAppSyncClient awsAppSyncClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        teams = new LinkedList<>();

        awsAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        Button saveSettingsButton = findViewById(R.id.settingsSaveButton);
        saveSettingsButton.setOnClickListener((event) -> {
            TextView usernameTextBox = findViewById(R.id.usernameEditBox);
            String username = usernameTextBox.getText().toString();

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putString("username", username);
            editor.apply();
        });

        ListTeamsQuery query = ListTeamsQuery.builder().build();
        awsAppSyncClient.query(query).enqueue(allTeamCallback);
    }

    protected GraphQLCall.Callback<ListTeamsQuery.Data> allTeamCallback = new GraphQLCall.Callback<ListTeamsQuery.Data>() {
        @Override
        public void onResponse(@Nonnull Response<ListTeamsQuery.Data> response) {
            Log.d("Settings.Callback", "made it to the callback success");
            Handler h = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(Message message){
                    teams.addAll(response.data().listTeams().items());

                    LinkedList<String> teamNames = new LinkedList<>();

                    for(ListTeamsQuery.Item team : teams){
                        teamNames.add(team.name());
                    }

                    Spinner spinner = findViewById(R.id.teamSpinner);

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Settings.this, android.R.layout.simple_spinner_item, teamNames);

                    spinner.setAdapter(adapter);

                    spinner.setOnItemSelectedListener(Settings.this);
                }
            };
            h.obtainMessage().sendToTarget();
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("Settings.Callback", e.getMessage());
        }
    };

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.i("TeamSelected",Integer.toString(i));

        Spinner spinner  = (Spinner)findViewById(R.id.teamSpinner);
        String text = spinner.getSelectedItem().toString();
        Log.i("TeamSelected", text);

        String teamID = "";
        for(ListTeamsQuery.Item team : teams){
            if(team.name().equals(text)){
                Log.i("TeamId?",team.id());
                teamID = team.id();
            }
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("teamName", text);
        editor.putString("teamID",teamID);
        editor.apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
