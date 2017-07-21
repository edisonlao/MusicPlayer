package com.edison.musicplayer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.ImageButton;

import java.io.File;
import java.io.IOException;
/**
 * Created by 相思湖陈建州 on 2016/5/9.
 */
public class MusicService extends Service implements MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener {
    private File songdir;
    private String[] songdata;
    private MediaPlayer player;
    private int musicIndex;
    private int CurrentPosition;
    private boolean isrunning;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    public void onCreate() {
        super.onCreate();

/*------------------------播放器继承和设定监听-------------------- */
        player = new MediaPlayer();
        player.setOnCompletionListener(this);
        player.setOnPreparedListener(this);

/*------------------------广播接受者继承和注册---------------------*/
        BroadcastReceiver receiver = new InnerBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.edison.musicplayer.play");
        registerReceiver(receiver, filter);

        isrunning = true;
        new UpdateProgressThread().start();
    }
/*---------------------------获取音乐路径及调用播放功能------------------------*/
    @Override
    public int onStartCommand(Intent intentfangge, int flags, int startId) {
        Intent intent2 = new Intent(this,MainActivity.class);
        if (intentfangge != null) {
            String music_path = intentfangge.getStringExtra("music_path");
            System.out.println(music_path);
            if (music_path != null) {
                songdir = new File(music_path);
                songdata = intentfangge.getStringArrayExtra("music_list");
                System.out.println(songdata[0]);
            }
            String action = intentfangge.getStringExtra("action");
            if (action != null) {
               // Intent intenthuichuan = new Intent(this,MainActivity.class);
                if ("play".equals(action)) {
                    musicIndex = intentfangge.getIntExtra("position", 0);
                    try {
                        play();

                       /* intenthuichuan.putExtra("xinhao","1");
                        startActivity(intenthuichuan);*/
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if ("bofangzhanting".equals(action)) {
                    if (player != null && player.isPlaying()) {
                        pause();
                        intent2.putExtra("action", "btnpause");
                        startService(intent2);
                       /* intenthuichuan.putExtra("xinhao","2");
                        startActivity(intenthuichuan);*/
                    } else {
                        try {
                            play();
                            intent2.putExtra("action","btnplay");
                            startService(intent2);
                           /* intenthuichuan.putExtra("xinhao","1");
                            startActivity(intenthuichuan);*/
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else if ("shangyishou".equals(action)) {
                    try {
                        shangyishou();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if ("xiayishou".equals(action)) {
                    try {
                        xiayishou();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        } return super.onStartCommand(intentfangge, flags, startId);
    }
/*------------------------------广播动作接收和响应------------------------------*/
    private class InnerBroadcastReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intentdianji) {
        String action = intentdianji.getAction();
        if("com.edison.musicplayer.play".equals(action)){
            try {
                musicIndex = intentdianji.getIntExtra("position",0);
                play();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

/*------------------------------------------播放功能--------------------------------------------*/
    private void play() throws IOException {
        player.reset();
        player.setDataSource(songdir.getAbsolutePath() + "/" + songdata[musicIndex]);
      /*  tvTitle.setText("正在播放:" + songdata[musicIndex]);*/
        player.prepareAsync();



    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        player.start();
        player.seekTo(CurrentPosition);
        CurrentPosition = 0;
        //  btnbofang.setImageResource(R.mipmap.zhanting);
    }

    private void pause() {
        player.pause();
        CurrentPosition = player.getCurrentPosition();
    }

    @Override
    public void onDestroy() {
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

   /* public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnbofang:
                if (player != null && player.isPlaying()) {
                    player.pause();
                    //   btnbofang.setImageResource(R.drawable.bofang3);
                } else {
                    player.start();
                    //   btnbofang.setImageResource(R.drawable.zhanting3);

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
            case R.id.playbar:
                Intent intentzhuan = new Intent();
                intentzhuan.setClass(this,bofangchuangkou.class);
                startActivity(intentzhuan);
                break;
        }
    }*/


    @Override
    public void onCompletion(MediaPlayer mp) {
        try {
            xiayishou();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

  /*  private class UpdateHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (player != null && player.isPlaying()) {
                int currentPosition = player.getCurrentPosition();
                int duration = player.getDuration();
                int progress = currentPosition * 100 / duration;
                pbProgress.setProgress(progress);
                tvCurrentPosition.setText(sdf.format(new Date(currentPosition)));
                tvDuration.setText(sdf.format(new Date(duration)));
            }
        }
    }*/

    private class UpdateProgressThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (isrunning) {
                if (player != null && player.isPlaying()) {
                    Intent intentupdate = new Intent();
                    intentupdate.setAction("com.edison.musicplayer.update");
                    intentupdate.putExtra("currentposition", player.getCurrentPosition());
                    intentupdate.putExtra("duration", player.getDuration());
                    intentupdate.putExtra("tvTitle", songdata[musicIndex]);
                    sendBroadcast(intentupdate);

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

