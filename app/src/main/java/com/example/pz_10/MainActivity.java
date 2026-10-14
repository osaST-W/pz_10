package com.example.pz_10;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private VideoView videoView;
    private ActivityResultLauncher<Intent> videoCaptureLauncher;

    private Button btnRecordVideo;
    private Button btnPlay;
    private Button btnPause;
    private Button btnStop;
    private Button btnRestart;

    private boolean isVideoLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.videoView);
        btnRecordVideo = findViewById(R.id.btnRecordVideo);
        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnStop = findViewById(R.id.btnStop);
        btnRestart = findViewById(R.id.btnRestart);

        videoCaptureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri videoUri = result.getData().getData();
                        if (videoUri != null) {
                            videoView.setVideoURI(videoUri);
                            videoView.start();
                            isVideoLoaded = true;

                            Toast.makeText(
                                    this,
                                    "Видео записано и воспроизводится",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    } else {
                        Toast.makeText(
                                this,
                                "Запись отменена",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );

        btnRecordVideo.setOnClickListener(v -> dispatchTakeVideoIntent());

        btnPlay.setOnClickListener(v -> {
            if (!isVideoLoaded) {
                Toast.makeText(this, "Сначала запишите видео", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!videoView.isPlaying()) {
                videoView.start();
            }
        });

        btnPause.setOnClickListener(v -> {
            if (isVideoLoaded && videoView.isPlaying()) {
                videoView.pause();
            }
        });

        btnStop.setOnClickListener(v -> {
            if (isVideoLoaded) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                }
                videoView.seekTo(0);
            }
        });

        btnRestart.setOnClickListener(v -> {
            if (!isVideoLoaded) {
                Toast.makeText(this, "Сначала запишите видео", Toast.LENGTH_SHORT).show();
                return;
            }
            videoView.seekTo(0);
            videoView.start();
        });
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            videoCaptureLauncher.launch(takeVideoIntent);
        } else {
            Toast.makeText(
                    this,
                    "На устройстве нет приложения камеры",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}