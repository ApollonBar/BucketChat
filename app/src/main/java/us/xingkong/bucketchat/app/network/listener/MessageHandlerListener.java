package us.xingkong.bucketchat.app.network.listener;

import android.os.Handler;

import network.bucketobject.Message;
import network.connection.Connection;
import network.listener.MessageListener;

/**
 * Created by 饶翰新 on 2017/7/14.
 */

public class MessageHandlerListener extends MessageListener{

    Handler handler;
    MessageListener listener;

    public MessageHandlerListener(MessageListener messageListener){
        listener = messageListener;
        handler = new Handler();
    }

    @Override
    public void onMessageCome(final Connection connection, final Message message) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onMessageCome(connection,message);
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
