package us.xingkong.bucketchat.app.network.listener;

import android.os.Handler;

import java.util.List;

import network.connection.Connection;
import network.listener.QueryListener;

/**
 * Created by 饶翰新 on 2017/7/14.
 */

public class Test <T> extends QueryListener<T> {

    QueryListener<T> listener;
    Handler handler;
    public  Test(QueryListener<T> queryListener)
    {
        listener = queryListener;
        handler = new Handler();

    }

    @Override
    public void onResultsCome(final Connection connection, final int i, final List<T> list) {
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
