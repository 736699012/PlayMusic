package com.example.playmusic.interfaces;

public interface IPlayControl {

    int PLAY_STATE_PLAY = 0;
    int PLAY_STATE_PAUSE = 1;
    int PLAY_STATE_STOP = 2;

    /**
     * 把UI的控制权交给逻辑层
     *
     * @param iPlayViewControl
     */
    void registerViewControl(IPlayViewControl iPlayViewControl);

    /**
     * 取消接口的注册
     */
    void unRegisterViewControl();

    /**
     * 播放或暂停
     */
    void playOrPause();


    /**
     * 停止
     */

    void stop();


    /**
     * 设置进度条
     *
     * @param seek： 进度
     */
    void seekTo(int seek);


}
