package us.xingkong.bucketchat.app.network.listener;

import android.os.Handler;

import network.connection.Connection;
import network.listener.BucketListener;

/**
 * Created by 饶翰新 on 2017/7/15.
 */

public class BucketHandlerListener extends BucketListener {

    BucketListener listener;
    Handler handler;

    public BucketHandlerListener(BucketListener bucketListener)
    {
        this.listener = bucketListener;
        handler = new Handler();
    }

    @Override
    public void onDataCome(final Connection connection, final String s) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onDataCome(connection,s);
            }
        });
    }

    @Override
    public void onDisconnection(final Connection connection) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onDisconnection(connection);
            }
        });
    }
}
