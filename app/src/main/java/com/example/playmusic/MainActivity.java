package com.example.playmusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.example.playmusic.interfaces.IPlayControl;
import com.example.playmusic.interfaces.IPlayViewControl;
import com.example.playmusic.services.PlayService;

/**
 * 音乐播放器:
 * 面向接口编程：
 * 1.先定义接口：
 *    1.IPlayControl  逻辑层：播放，暂停，停止，并且得到对UI修改的控制权
 *    2.IPlayViewControl  对UI就行更新
 * 2.初始化组件，对UI进行布局。
 * 3.写服务，在MainActivity中开始服务，绑定服务。销毁时解绑
 * 4.PlayPresenter 继承Binder 实现IPlayControl
 * 5.进行逻辑操作。
 *   1.点击播放/暂停按钮，对音乐进行控制
 *   2.使用MediaPlayer 加载播放资源
 *   3.然后修改进度条
 *       先根据进度条的百分比,修该音乐播放的时间
 *       在写一个根据音乐的进度，进度条不断改变
 *
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private SeekBar seekBar;
    private Button play_or_pause;
    private Button stop;
    private Connect connect;
    private boolean isBind;
    private IPlayControl iPlayControl;
    private boolean isUserTouchSeek = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//       初始化View
        initView();
//        初始化事件
        initEvent();
//        开始服务
        initService();
//        绑定服务
        initBindService();
    }

    /**
     * 绑定服务
     */
    private void initBindService() {
        Intent intent = new Intent(this, PlayService.class);
        connect = new Connect();
        isBind = bindService(intent, connect, BIND_AUTO_CREATE);
        Log.d(TAG, "initBindService...");
    }

    public class Connect implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iPlayControl = (IPlayControl) service;
            iPlayControl.registerViewControl(iPlayViewControl);
            Log.d(TAG, "onServiceConnected..." + service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            connect = null;
        }
    }

    /**
     * 开始服务
     */
    private void initService() {
        startService(new Intent(this, PlayService.class));
        Log.d(TAG, "initService...");
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUserTouchSeek = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isUserTouchSeek = false;
                int seek = seekBar.getProgress();
                if (iPlayControl != null) {
                    iPlayControl.seekTo(seek);
                }
                Log.d(TAG, "onStopTrackingTouch..." + seek);
            }
        });

        play_or_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iPlayControl != null) {
                    iPlayControl.playOrPause();
                    Log.d(TAG, "iPlayControl...playOrPause ");
                }

            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iPlayControl != null) {
                    iPlayControl.stop();
                    Log.d(TAG, "iPlayControl...stop");
                }
            }
        });
    }

    /**
     * 初始化视图
     */
    private void initView() {
        seekBar = findViewById(R.id.seek_bar);
        play_or_pause = findViewById(R.id.play_or_pause);
        stop = findViewById(R.id.stop);
        Log.d(TAG, "initView...");
    }

    /**
     * 销毁Activity 的时候解除绑定
     */
    @Override
    protected void onDestroy() {
        Log.d(TAG,"onDestroy...");
        super.onDestroy();
        if (isBind && connect != null) {
            unbindService(connect);
            iPlayControl.unRegisterViewControl();
        }
    }

    private IPlayViewControl iPlayViewControl = new IPlayViewControl() {
        @Override
        public void onPlayStateChange(int state) {
            switch (state) {
                case IPlayControl.PLAY_STATE_PLAY:
                    play_or_pause.setText("暂停");
                    break;
                case IPlayControl.PLAY_STATE_PAUSE:

                case IPlayControl.PLAY_STATE_STOP:
                    play_or_pause.setText("播放");
                    break;
            }
        }

        @Override
        public void onSeekChange(int seek) {
            if (!isUserTouchSeek) {
                seekBar.setProgress(seek);
            }
        }
    };
}
