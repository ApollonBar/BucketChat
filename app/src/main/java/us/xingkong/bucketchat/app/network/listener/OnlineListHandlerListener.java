package us.xingkong.bucketchat.app.network.listener;

import android.os.Handler;

import java.util.List;

import network.bucketobject.USER;
import network.connection.Connection;
import network.listener.OnlineListListener;

/**
 * Created by 饶翰新 on 2017/7/14.
 */

public class OnlineListHandlerListener extends OnlineListListener {

    Handler handler;
    OnlineListListener listener;

    public OnlineListHandlerListener(OnlineListListener messageListener){
        listener = messageListener;
        handler = new Handler();
    }

    @Override
    public void onResultsCome(final Connection connection, final int i, final List<USER> list) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onResultsCome(connection,i,list);
            }
        });
    }

    @Override
    public void onException(final Exception e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onException(e);
            }
        });
    }
}
