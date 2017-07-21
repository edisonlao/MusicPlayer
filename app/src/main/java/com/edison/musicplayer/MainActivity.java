package com.edison.musicplayer;


/*-------------------------------------------------------
********************以下包含**********************
****从内存中获取音乐路径在列表中呈现
****播放、暂停、上一首、下一首按钮
****底部点击跳转到播放窗口
****点击音乐发送播放信息
****点击音乐转换壁纸
****获取快速模糊后的位图


------------------------------------------------------- */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by 相思湖陈建州 on 2016/4/25.
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,View.OnClickListener{
    public ImageButton btnbofang, btnxiayishou, btnshangyishou;//播放暂停、下一首、上一首按钮
    private ListView lvyinyueliebiao;//音乐列表
    private File songdir;//音乐路径
    private String[] songdata;//音乐数据
    private MediaPlayer player;//媒体播放工具
    private int musicIndex;
    private int currentPosition;
    private boolean isrunning;
    private boolean isPause;
    private ProgressBar pbProgress;//进度条
    private Handler handler;
    private MusicAdapter ma1;//音乐列表适配器
    private TextView tvCurrentPosition, tvDuration, tvTitle;//当前播放位置、音乐全部时间、当前音乐名
    private SimpleDateFormat sdf;
    private ImageView ivbizhixia;//底部快速模糊位图放置区
    private RelativeLayout playbar;//底部跳转区

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        songdir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        songdata = songdir.list();
        Intent intentfangge = new Intent(this,MusicService.class);
        intentfangge.putExtra("music_path", songdir.getAbsolutePath());
        intentfangge.putExtra("music_list", songdata);
        startService(intentfangge);
        player = new MediaPlayer();

      //  handler = new UpdateHandler();

      //  new UpdateProgressThread().start();

        playbar = (RelativeLayout)findViewById(R.id.playbar);
        btnbofang = (ImageButton) findViewById(R.id.btnbofang);
        btnxiayishou = (ImageButton) findViewById(R.id.btnxiayishou);
        btnshangyishou = (ImageButton) findViewById(R.id.btnshangyishou);
        lvyinyueliebiao = (ListView) findViewById(R.id.lvmusic);
        pbProgress = (ProgressBar) findViewById(R.id.pbProgress);
        ivbizhixia = (ImageView) findViewById(R.id.ivbizhixia);

        playbar.setOnClickListener(this);
        lvyinyueliebiao.setOnItemClickListener(this);
        // player.setOnCompletionListener(this);
       // player.setOnPreparedListener(this);
        btnshangyishou.setOnClickListener(this);
        btnbofang.setOnClickListener(this);
        btnxiayishou.setOnClickListener(this);

        sdf = new SimpleDateFormat("mm:ss", Locale.CHINA);
        tvCurrentPosition = (TextView) findViewById(R.id.tvCurrentPosition);
        tvDuration = (TextView) findViewById(R.id.tvDuration);
        tvTitle = (TextView) findViewById(R.id.tvTitle);

        BroadcastReceiver receiver = new InnerBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.edison.musicplayer.update");
        registerReceiver(receiver, filter);


        ma1 = new MusicAdapter(songdata, this);
        lvyinyueliebiao.setAdapter(ma1);

    }
/*
-------------------------------------------歌曲列表播放意----------------------------------------------------------------------
*/
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
       /* Intent intentdianji = new Intent(this,MusicService.class);
        intentdianji.putExtra("position",position);
        intentdianji.putExtra("action", "play");
        startService(intentdianji);*/

        Intent intentdianji = new Intent();
        intentdianji.setAction("com.edison.musicplayer.play");//这边每set一个action，service那边都要add每一个action
        intentdianji.putExtra("position", position);//这边传的position，service那边musicIndex要指定获取position，不然点击任何item都默认播放第一首歌
        btnbofang.setImageResource(R.drawable.zhanting3);
        sendBroadcast(intentdianji);
        isrunning = true;
            //  player.start();

       /* musicIndex = position;
        try {
            play();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

  /*  private void play() throws IOException {
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
    }*/
    /*
----------------------------------------按钮意图---------------------------------------------------
*/
    @Override
    public void onClick(View v) {
        Intent intent2 = new Intent(this,MusicService.class);
        switch (v.getId()) {
            case R.id.btnbofang:
                if(isrunning){
                    intent2.putExtra("action", "bofangzhanting");
                }else if(isPause){
                    btnbofang.setImageResource(R.drawable.zhanting3);
                }
                startService(intent2);
            /*  if (player != null && player.isPlaying()) {
                    // player.pause();
                    btnbofang.setImageResource(R.drawable.zhanting3);
                }else {
                    btnbofang.setImageResource(R.drawable.bofang3);
                }*/

                break;

            case R.id.btnshangyishou:
                intent2.putExtra("action","shangyishou");
                startService(intent2);
                btnbofang.setImageResource(R.drawable.zhanting3);
               /* try {
                    shangyishou();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                break;
            case R.id.btnxiayishou:
                intent2.putExtra("action","xiayishou");
                startService(intent2);
                btnbofang.setImageResource(R.drawable.zhanting3);
              /*  try {
                    xiayishou();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                break;
            case R.id.playbar:
                Intent intentzhuan = new Intent(this,bofangchuangkou.class);
                startActivity(intentzhuan);
        }

    }


    private class InnerBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intentupdate) {
            String action = intentupdate.getAction();
            if("com.edison.MusicPlayer.update".equals(action)){
                int currentposition = intentupdate.getIntExtra("currentposition",0);
                int duration = intentupdate.getIntExtra("duration",1);
                int progress = currentposition*100/duration;
                pbProgress.setProgress(progress);
                tvTitle.setText(intentupdate.getStringExtra("tvTitle"));
                tvCurrentPosition.setText(sdf.format(new Date(currentposition)));
                tvDuration.setText(sdf.format(new Date(duration)));
            }

        }
    }
 /*   @Override
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
                pbProgress.setProgress(progress);
                tvCurrentPosition.setText(sdf.format(new Date(currentPosition)));
                tvDuration.setText(sdf.format(new Date(duration)));
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
    }*/
}

   /* private void applyBlur() {
        ivbizhixia.getViewTreeObserver().removeOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                ivbizhixia.getViewTreeObserver().removeOnPreDrawListener(this);
                ivbizhixia.buildDrawingCache();

                Bitmap bizhi = ivbizhixia.getDrawingCache();
                blur(bizhi,ivbizhixia);
                return true;
            }
        });
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void blur(Bitmap bizhi,View view) {
        long startMs = System.currentTimeMillis();
        float radius = 20;

        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth()),
                (int) (view.getMeasuredHeight()), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft(), -view.getTop());
        canvas.drawBitmap(bizhi, 0, 0, null);
        overlay = FastBlur.doBlur(overlay, (int)radius, true);
        view.setBackground(new BitmapDrawable(getResources(), overlay));
    }*/

