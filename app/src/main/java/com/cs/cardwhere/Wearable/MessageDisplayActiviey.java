package com.cs.cardwhere.Wearable;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cs.cardwhere.R;

public class MessageDisplayActiviey extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_display_activiey);

        if (getIntent() != null) {
            Intent intent = getIntent();
            TextView textView = findViewById(R.id.display);
            textView.setText(intent.getStringExtra(MessageService.REPORT_KEY));
        }
    }
}
