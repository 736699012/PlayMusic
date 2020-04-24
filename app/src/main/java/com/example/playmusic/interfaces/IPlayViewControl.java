package com.example.playmusic.interfaces;

public interface IPlayViewControl {


    /**
     * 设置开始播放状态
     * @param state:状态
     */
    void onPlayStateChange(int state);

    /**
     * 设置进度条
     * @param seek:进度
     */
    void onSeekChange(int seek);
}
