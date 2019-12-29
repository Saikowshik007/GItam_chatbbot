package com.example.gitam_chatbbot;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.time.LocalTime;

import ai.api.AIDataService;
import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;


public class MainActivity extends AppCompatActivity implements AIListener {
    AIService aiService;
    AIRequest aiRequest;
    AIDataService aiDataService;
    EditText et;
    String s="";
    Vibrator vibe;
    TextView sent;
    TextView recieve;
    LocalTime time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sent = findViewById(R.id.sent);
        recieve=findViewById(R.id.recieved);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest();
        }


        final AIConfiguration config = new AIConfiguration("Your_DilougeFlow key",
                AIConfiguration.SupportedLanguages.English, AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);
        aiRequest = new AIRequest();
        aiDataService = new AIDataService(config);
        et=findViewById(R.id.editText);
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageButton:
                vibe.vibrate(20);
                aiService.startListening();

                break;
            case R.id.imageButton2:
                vibe.vibrate(20);
                text();

            break;

        }
    }


        protected void makeRequest () {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    101);
        }

        @Override
        public void onRequestPermissionsResult ( int requestCode, String permissions[],
        int[] grantResults){
            switch (requestCode) {
                case 101: {

                    if (grantResults.length == 0
                            || grantResults[0] !=
                            PackageManager.PERMISSION_GRANTED) {


                    } else {

                    }
                    return;
                }
            }
        }

        @Override
        public void onResult ( final AIResponse response){
            Result result = response.getResult();
            sent.setText(result.getResolvedQuery());
            //.text_message_body2.setText("User: "+result.getResolvedQuery()+"\n\n"+"Penny: " + result.getFulfillment().getSpeech());
            recieve.setText(result.getFulfillment().getSpeech());
            vibe.vibrate(20);
        }


        @Override
        public void onError (AIError error){
            recieve.setText("No Internet");
            vibe.vibrate(20);
            vibe.vibrate(60);
            vibe.vibrate(40);
        }

        @Override
        public void onAudioLevel ( float level){

        }

        @Override
        public void onListeningStarted () {

        }

        @Override
        public void onListeningCanceled () {

        }

        @Override
        public void onListeningFinished () {

        }
        public void text(){
        aiRequest.setQuery(et.getText().toString());
            new AsyncTask<AIRequest, Void, AIResponse>() {
                @Override
                protected AIResponse doInBackground(AIRequest... requests) {
                    final AIRequest request = requests[0];
                    try {
                        final AIResponse response = aiDataService.request(aiRequest);
                        return response;
                    } catch (AIServiceException e) {
                    }
                    return null;
                }
                @Override
                protected void onPostExecute(AIResponse aiResponse) {
                    if (aiResponse != null) {
                        Result result = aiResponse.getResult();
                        //s=("User: "+result.getResolvedQuery()+"\n\n"+"Penny: " + result.getFulfillment().getSpeech());

                        //text_message_body.setText(s);
                        sent.setText(result.getResolvedQuery());
                        //.text_message_body2.setText("User: "+result.getResolvedQuery()+"\n\n"+"Penny: " + result.getFulfillment().getSpeech());
                        recieve.setText(result.getFulfillment().getSpeech());
                        et.setText("");
                        vibe.vibrate(20);
                    }
                    else{vibe.vibrate(60);recieve.setText("No Internet");}
                }
            }.execute(aiRequest);
        }


    }

