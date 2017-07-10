package com.example.codetribe.deidre;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;
import static com.example.codetribe.deidre.R.layout.activity_music;

public class Music extends Activity {
    private MediaPlayer mMediaPlayer;


    private AudioManager mAudioManager;

    AudioManager.OnAudioFocusChangeListener mOnAudioFocusListener =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        mMediaPlayer.pause();
                        mMediaPlayer.seekTo(0);
                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        mMediaPlayer.start();
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        releaseMediaPlayer();
                    }

                }



            };
    private Button button1, button2, button3, button4;
    private ImageView iv;
    boolean play = false;
    private double startTime = 0;
    private double finalTime = 0;

    ImageButton pause, previous;
    ListView listView;
    Button stop;
    List<String> list;

    ListAdapter adapter;

    MediaPlayer mediaPlayer;



    private Handler myHandler = new Handler();
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private SeekBar seekbar;
    private TextView tx1, tx2, tx3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_music);

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);


        tx1 = (TextView) findViewById(R.id.textView2);
        tx2 = (TextView) findViewById(R.id.textView3);
        tx3 = (TextView) findViewById(R.id.textView4);
        tx3.setText("Song.mp3");
        list = new ArrayList<>();
        Field[] fields = R.raw.class.getFields();

        list = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);

        for (int i = 0; i < fields.length; i++) {
            list.add(fields[i].getName());
        }

        list.remove(0);
        list.remove(4);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);

        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (mediaPlayer != null) {
                    mediaPlayer.release();

                }
                button2.setEnabled(true);
                button3.setEnabled(false);

                int resID = getResources().getIdentifier(list.get(i), "raw", getPackageName());

                mediaPlayer = MediaPlayer.create(Music.this, resID);
                mediaPlayer.start();
            }

        });


        mediaPlayer = MediaPlayer.create(this, R.raw.gloria);
        seekbar = (SeekBar) findViewById(R.id.seekBar);
        seekbar.setClickable(false);


        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Playing sound", Toast.LENGTH_SHORT).show();
                mediaPlayer.start();

                finalTime = mediaPlayer.getDuration();
                startTime = mediaPlayer.getCurrentPosition();

                int oneTimeOnly = 0;
                if (oneTimeOnly == 0) {
                    seekbar.setMax((int) finalTime);
                    oneTimeOnly = 1;
                }

                tx2.setText(String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                        finalTime)))
                );

                tx1.setText(String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                        startTime)))
                );

                seekbar.setProgress((int) startTime);
                myHandler.postDelayed(UpdateSongTime, 100);
                button2.setEnabled(true);
                button3.setEnabled(false);

            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Pausing sound", Toast.LENGTH_SHORT).show();
                mediaPlayer.pause();
                button2.setEnabled(false);
                button3.setEnabled(true);
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = (int) startTime;

                if ((temp + forwardTime) <= finalTime) {
                    startTime = startTime + forwardTime;
                    mediaPlayer.seekTo((int) startTime);
                    Toast.makeText(getApplicationContext(), "You have Jumped forward 5 seconds", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Cannot jump forward 5 seconds", Toast.LENGTH_SHORT).show();
                }
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = (int) startTime;

                if ((temp - backwardTime) > 0) {
                    startTime = startTime - backwardTime;
                    mediaPlayer.seekTo((int) startTime);
                    Toast.makeText(getApplicationContext(), "You have Jumped backward 5 econds", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Cannot jump backward 5 seconds", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
            mAudioManager.abandonAudioFocus(mOnAudioFocusListener);
        }
    }
    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            tx1.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );
            seekbar.setProgress((int) startTime);
            myHandler.postDelayed(this, 100);
        }
    };
}
