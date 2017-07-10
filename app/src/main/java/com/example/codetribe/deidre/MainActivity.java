package com.example.codetribe.deidre;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
ImageView Housemusic,Deephouse,Jazzmusic,music;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView music = (ImageView) findViewById(R.id.music);

        music.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent musicIntent = new Intent(MainActivity.this,Music.class);
                startActivity(musicIntent);
            }
        });


    }
}

