package com.example.mousse;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;

public class MainActivity extends AppCompatActivity {

    ImageView mic;
    TextView userTextTV, agentTextTV;
    TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 121);

        mic = findViewById(R.id.imageView);
        userTextTV = findViewById(R.id.textView3);
        agentTextTV = findViewById((R.id.textView7));

        tts = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                }
            }
        });

        final AIConfiguration config = new AIConfiguration("c155929f09734c30b73771aaaac66a5d",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        final AIService aiService = AIService.getService(getApplicationContext(), config);
        aiService.setListener(new AIListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResult(AIResponse result) {
                tts.speak(result.getResult().getFulfillment().getSpeech(), TextToSpeech.QUEUE_ADD, null, null);
                agentTextTV.setText(result.getResult().getFulfillment().getSpeech());
                userTextTV.setText(result.getResult().getResolvedQuery());

                if(result.getResult().getAction().equals("websearch")) {
                    String searchEngine = result.getResult().getStringParameter("search-engine");
                    String query = result.getResult().getStringParameter("any");
                    String url = "https://www.google.com/search?q="+query;

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            }

            @Override
            public void onError(AIError error) {
                mic.setImageResource(R.drawable.mic23);
            }

            @Override
            public void onAudioLevel(float level) {

            }

            @Override
            public void onListeningStarted() {
                mic.setImageResource(R.drawable.mic1);
            }

            @Override
            public void onListeningCanceled() {
                mic.setImageResource(R.drawable.mic23);
            }

            @Override
            public void onListeningFinished() {
                mic.setImageResource(R.drawable.mic23);
            }
        });

        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aiService.startListening();
            }
        });
    }
}