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

import com.amazonaws.amplify.generated.graphql.CreateTeamMutation;
import com.amazonaws.amplify.generated.graphql.ListTeamsQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.util.LinkedList;

import javax.annotation.Nonnull;

import type.CreateTeamInput;

public class Settings extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private String team;
    private LinkedList<ListTeamsQuery.Item> teams;
    private AWSAppSyncClient awsAppSyncClient;
    private final String TAG = "Dansie";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        teams = new LinkedList<>();

        awsAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();


        Button saveTeamButton = findViewById(R.id.teamSaveButton);
        saveTeamButton.setOnClickListener((event) -> {
            TextView teamTextBox = findViewById(R.id.teamTextBox);
            String teamName = teamTextBox.getText().toString();

            CreateTeamInput input = CreateTeamInput.builder()
                    .name(teamName)
                    .build();
            CreateTeamMutation createTeamMutation = CreateTeamMutation.builder().input(input).build();
            awsAppSyncClient.mutate(createTeamMutation).enqueue(teamCreateCallback);
        });

        ListTeamsQuery query = ListTeamsQuery.builder().build();
        awsAppSyncClient.query(query)
                .responseFetcher(AppSyncResponseFetchers.NETWORK_FIRST)
                .enqueue(allTeamCallback);
    }

    protected GraphQLCall.Callback<ListTeamsQuery.Data> allTeamCallback = new GraphQLCall.Callback<ListTeamsQuery.Data>() {
        @Override
        public void onResponse(@Nonnull Response<ListTeamsQuery.Data> response) {
            Log.d(TAG, "Settings.Callback made it to the callback success");
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
            Log.e(TAG,"Settings.Callback "+ e.getMessage());
        }
    };

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.i(TAG,"TeamSelected "+Integer.toString(i));

        Spinner spinner  = (Spinner)findViewById(R.id.teamSpinner);
        String text = spinner.getSelectedItem().toString();
        Log.i(TAG, "TeamSelected "+text);

        String teamID = "";
        for(ListTeamsQuery.Item team : teams){
            if(team.name().equals(text)){
                Log.i(TAG,"TeamId? "+team.id());
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

    public GraphQLCall.Callback teamCreateCallback = new GraphQLCall.Callback() {
        @Override
        public void onResponse(@Nonnull Response response) {
            Log.i(TAG,"Settings team created");
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e(TAG,"Settings "+e.getMessage());
        }
    };
}
