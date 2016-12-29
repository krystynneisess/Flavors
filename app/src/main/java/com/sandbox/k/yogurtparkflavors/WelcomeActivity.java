package com.sandbox.k.yogurtparkflavors;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        final Button flavorsBtn = (Button) findViewById(R.id.see_flavors_btn);
        flavorsBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent seeFlavorsIntent = new Intent(WelcomeActivity.this, FlavorsActivity.class);
                startActivity(seeFlavorsIntent);
            }
        });
    }
}
