package us.xingkong.bucketchat.app.network.listener;

import android.os.Handler;

import network.connection.Connection;
import network.listener.LoginListener;

/**
 * Created by 饶翰新 on 2017/7/14.
 */

public class LoginHandlerListener extends LoginListener{

    Handler handler;
    LoginListener listener;

    public LoginHandlerListener(LoginListener loginListener){
        listener = loginListener;
        handler = new Handler();
    }
    @Override
    public void onDone(final Connection connection, final boolean b) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onDone(connection,b);
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
