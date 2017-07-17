package us.xingkong.bucketchat.app.activity.view;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import network.connection.Connection;
import network.listener.LoginListener;
import us.xingkong.bucketchat.R;
import us.xingkong.bucketchat.app.network.Network;
import us.xingkong.bucketchat.app.service.NetworkService;
import us.xingkong.bucketchat.others.Util;


/**
 * Created by SeaLynn0 on 2017/6/20.
 * Update by Hansin on 2017/7/17.
 */

public abstract class BaseActivity extends AppCompatActivity {
    ActionBar actionbar;

    private Network net;
    private ServiceConnection conn;
    private NetworkService service;
    private int resumeCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FrameLayout viewContent = (FrameLayout) findViewById(R.id.viewContent);
        LayoutInflater.from(BaseActivity.this).inflate(getContentView(), viewContent);
        actionbar = getSupportActionBar();

        resumeCount = 0;

        init(savedInstanceState);
    }

    protected void initService() {
        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                NetworkService.NetworkBinder b = (NetworkService.NetworkBinder) iBinder;
                service = b.getServer();
                net = service.getNetwork();
                onServiceBind(true);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                onServiceBind(false);
            }
        };

        Intent intent = new Intent(this, NetworkService.class);
        this.bindService(intent, conn, Service.BIND_AUTO_CREATE);
    }

    protected abstract int getContentView();

    protected abstract void init(Bundle savedInstanceState);

    protected abstract void onServiceBind(boolean isSuccess);

    @Override
    protected void onResume() {
        resumeCount++;

        if(resumeCount > 1)
            checkNetwork();
        super.onResume();
    }

    private void checkNetwork(){
        if(getNet() == null)
        {
            restart();
        }
        if(!getNet().isVaild())
        {
            getNet().reLogin(new LoginListener() {
                @Override
                public void onDone(Connection connection, boolean b) {
                    if(!b)
                        restart();
                }
            });
        }
    }

    private void restart() {
        Util.restart(this);
    }

    public void setNet(Network net) {
        this.net = net;
    }

    public void setConn(ServiceConnection conn) {
        this.conn = conn;
    }

    public void setService(NetworkService service) {
        this.service = service;
    }

    public Network getNet() {
        return net;
    }

    public NetworkService getService() {
        return service;
    }

    public ServiceConnection getConn() {
        return conn;
    }
}
