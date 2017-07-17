package us.xingkong.bucketchat.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;

import us.xingkong.bucketchat.app.network.Network;

/**
 * Created by 饶翰新 on 2017/7/14.
 */

public class NetworkService extends Service{

    Network network;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(network == null)
            network = new Network();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        if(network == null)
            network = new Network();
        super.onCreate();
    }

    public Network getNetwork() {
        return network;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new NetworkBinder();
    }

    public class NetworkBinder extends Binder
    {

        public NetworkService getServer()
        {
            return NetworkService.this;
        }
    }

    @Override
    protected void finalize() throws Throwable {

        super.finalize();
    }

    @Override
    public void onDestroy() {
        try {
            network.finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
