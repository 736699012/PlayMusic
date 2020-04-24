package com.example.playmusic.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.playmusic.presenter.PlayPresenter;

public class PlayService extends Service {

    private PlayPresenter playPresenter ;


    @Override
    public void onCreate() {
        super.onCreate();
        if (playPresenter == null) {
            playPresenter = new PlayPresenter();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
       return  playPresenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        playPresenter =null;
    }
}
