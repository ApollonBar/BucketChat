package us.xingkong.bucketchat.app.network.listener;

import android.os.Handler;

import network.command.client.ClientCommand;
import network.connection.Connection;
import network.listener.ClientListener;

/**
 * Created by 饶翰新 on 2017/7/14.
 */

public class ClientHandlerListener extends ClientListener{

    Handler handler;
    ClientListener listener;

    public ClientHandlerListener(ClientListener loginListener){
        listener = loginListener;
        handler = new Handler();
    }

    @Override
    public void onDataCome(final Connection connection, final ClientCommand clientCommand) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onDataCome(connection,clientCommand);
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
