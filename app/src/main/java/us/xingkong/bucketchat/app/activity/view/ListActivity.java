package us.xingkong.bucketchat.app.activity.view;

import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;

import java.util.List;

import network.bucketobject.USER;
import network.connection.Connection;
import network.listener.OnlineListListener;
import us.xingkong.bucketchat.R;
import us.xingkong.bucketchat.app.activity.controller.OnlineListController;
import us.xingkong.bucketchat.others.Util;

public class ListActivity extends BaseActivity {

    OnlineListController controller;
    SwipeRefreshLayout srl;
    SwipeRefreshLayout.OnRefreshListener onRefreshListener;

    @Override
    protected int getContentView() {
        return R.layout.activity_list;
    }

    @Override
    protected void init(Bundle savedInstanceState) {


        initService();
    }



    public void refresh() {
        if(srl != null)
            srl.setRefreshing(true);
        controller.update(new OnlineListListener() {
            @Override
            public void onResultsCome(Connection connection, int i, List<USER> list) {
                if(srl != null)
                    srl.setRefreshing(false);
            }

            @Override
            public void onDisconnection(Connection conn) {
                Util.showText(ListActivity.this,R.string.err_disconnect);
                if(srl != null)
                    srl.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onServiceBind(boolean isSuccess) {
        if(isSuccess)
        {
            initView();


        }else{
            Util.showText(this,R.string.err_service);
        }
    }

    public void initView() {
        srl = (SwipeRefreshLayout)findViewById(R.id.refresh);
        onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        };
        srl.setOnRefreshListener(onRefreshListener);
        controller = new OnlineListController(this,getNet());
        refresh();
    }
}
