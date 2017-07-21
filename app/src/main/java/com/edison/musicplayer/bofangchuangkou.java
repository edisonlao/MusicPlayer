package com.edison.musicplayer;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by 相思湖陈建州 on 2016/5/5.
 */
public class bofangchuangkou extends Activity implements View.OnClickListener,MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener{
    private ImageButton btnbofang, btnxiayishou, btnshangyishou;
    private File songdir;
    private String[] songdata;
    private MediaPlayer player;
    private int musicIndex;
    private int currentPosition;
    private boolean isrunning;
    private ProgressBar pbProgressplay;
    private Handler handler;
    private TextView  tvTitle;
    private SimpleDateFormat sdf;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bofangchuangkou);

        songdir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        songdata = songdir.list();
        player = new MediaPlayer();
        handler = new UpdateHandler();
        isrunning = true;
        new UpdateProgressThread().start();

        btnbofang = (ImageButton) findViewById(R.id.btnbofang);
        btnxiayishou = (ImageButton) findViewById(R.id.btnxiayishou);
        btnshangyishou = (ImageButton) findViewById(R.id.btnshangyishou);
        pbProgressplay = (ProgressBar) findViewById(R.id.pbProgress);

        player.setOnCompletionListener(this);
        player.setOnPreparedListener(this);
        btnshangyishou.setOnClickListener(this);
        btnbofang.setOnClickListener(this);
        btnxiayishou.setOnClickListener(this);

        sdf = new SimpleDateFormat("mm:ss", Locale.CHINA);

        tvTitle = (TextView) findViewById(R.id.tvTitle);
    }
    private void play() throws IOException {
        player.reset();
        player.setDataSource(songdir.getAbsolutePath() + "/" + songdata[musicIndex]);
        tvTitle.setText("正在播放:" + songdata[musicIndex]);
        player.prepareAsync();


    }

    private void pause() {
        player.pause();
        currentPosition = player.getCurrentPosition();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player.isPlaying() && player != null) {
            player.stop();
        }
        // 退出程序时要释放内存
        player.release();
        // 把MediaPlayer的player对象清空
        player = null;
        isrunning = false;
    }

    private void shangyishou() throws IOException {
        musicIndex--;
        if (musicIndex > songdata.length - 1) {
            musicIndex = 0;
        }
        play();
    }

    private void xiayishou() throws IOException {
        musicIndex++;
        if (musicIndex < 0) {
            musicIndex = songdata.length - 1;
        }
        play();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnbofang:
                if (player != null && player.isPlaying()) {
                    player.pause();
                    btnbofang.setImageResource(R.drawable.bofang3);
                } else {
                    player.start();
                    btnbofang.setImageResource(R.drawable.zhanting3);

                }
                break;
            case R.id.btnshangyishou:
                try {
                    shangyishou();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btnxiayishou:
                try {
                    xiayishou();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        try {
            xiayishou();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        player.start();
        player.seekTo(currentPosition);
        currentPosition = 0;
        btnbofang.setImageResource(R.mipmap.zhanting);
    }

    private class UpdateHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (player != null && player.isPlaying()) {
                int currentPosition = player.getCurrentPosition();
                int duration = player.getDuration();
                int progress = currentPosition * 100 / duration;
                pbProgressplay.setProgress(progress);

            }
        }
    }

    private class UpdateProgressThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (isrunning) {
                Message msg = new Message();
                handler.sendMessage(msg);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}