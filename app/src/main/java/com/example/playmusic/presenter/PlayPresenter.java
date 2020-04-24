package com.example.playmusic.presenter;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Binder;
import android.util.Log;

import com.example.playmusic.interfaces.IPlayControl;
import com.example.playmusic.interfaces.IPlayViewControl;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class PlayPresenter extends Binder implements IPlayControl {
    private static final String TAG = "PlayPresenter";
    private IPlayViewControl mViewControl;
    private int mCurrentState = PLAY_STATE_STOP;
    private MediaPlayer mediaPlayer;
    private Timer mTimer;
    private MyTimer myTimerTask;

    @Override
    public void registerViewControl(IPlayViewControl iPlayViewControl) {
        this.mViewControl = iPlayViewControl;
        Log.d(TAG, "registerViewControl...");
    }

    @Override
    public void unRegisterViewControl() {
        this.mViewControl = null;
        Log.d(TAG, "unRegisterViewControl...");
    }

    @Override
    public void playOrPause() {
        Log.d(TAG, "playOrPause...");
        //如果当前状态是停止
        if (mCurrentState == PLAY_STATE_STOP) {
//            创建数据源
            initPlayer();
            try {
                mediaPlayer.setDataSource("/data/data/com.example.playmusic/files/Sweety - 樱花草.mp3");
                mediaPlayer.prepare();
                mediaPlayer.start();
                mCurrentState = PLAY_STATE_PLAY;
                startTimer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (mCurrentState == PLAY_STATE_PLAY) {
//          如果当前是播放，我们就暂停
            if (mediaPlayer!=null){
                mediaPlayer.pause();
                mCurrentState =PLAY_STATE_PAUSE;
                stopTimer();
            }

        } else if (mCurrentState == PLAY_STATE_PAUSE) {
//            如果当前是暂停，我们就播放
            if (mediaPlayer!=null){
                mediaPlayer.start();
                mCurrentState = PLAY_STATE_PLAY;
                startTimer();
            }

        }
        if (mViewControl != null) {
            mViewControl.onPlayStateChange(mCurrentState);
        }

    }

    private void initPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            mediaPlayer.setAudioAttributes(audioAttributes);
        }

    }

    @Override
    public void stop() {
        Log.d(TAG, "stop...");
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mCurrentState =PLAY_STATE_STOP;
            stopTimer();
            if (mViewControl != null) {
                mViewControl.onPlayStateChange(mCurrentState);
            }
            mediaPlayer=null;
        }
    }

    @Override
    public void seekTo(int seek) {
        Log.d(TAG, "seekTo...");
        if (mediaPlayer != null) {
            int tarSeek = (int) (seek*1.0f /100 *mediaPlayer.getDuration());
            mediaPlayer.seekTo(tarSeek);
        }
    }

    private void startTimer(){
        if (mTimer ==null){
            mTimer = new Timer();
        }
        if (myTimerTask == null) {
            myTimerTask = new MyTimer();
        }
        mTimer.schedule(myTimerTask,0,500);

    }

    private void stopTimer(){
        if (mTimer !=null){
            mTimer.cancel();
            mTimer = null;
        }
        if (myTimerTask != null) {
            myTimerTask.cancel();
            myTimerTask =null;
        }
    }

    private class MyTimer extends TimerTask{

        @Override
        public void run() {
//            获取当前进度
            if (mediaPlayer != null&&mViewControl!=null) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                int curPos = (int) (currentPosition *1.0f /mediaPlayer.getDuration() *100);
                Log.d(TAG,"当前进度"+curPos);
                mViewControl.onSeekChange(curPos);
            }

        }
    }
}
