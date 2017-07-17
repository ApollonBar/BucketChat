package us.xingkong.bucketchat.app.activity.controller;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import Common.Tool;
import network.bucketobject.USER;
import network.connection.Connection;
import network.listener.BucketListener;
import network.listener.OnlineListListener;
import us.xingkong.bucketchat.R;
import us.xingkong.bucketchat.app.activity.view.ChatActivity;
import us.xingkong.bucketchat.app.network.Network;
import us.xingkong.bucketchat.others.Util;


/**
 * Created by 饶翰新 on 2017/7/14.
 */

public class OnlineListController extends BucketListener{


    private Activity activity;
    private InitListener initListener;

    private Network net;
    private List<USER> list;
    private boolean done;

    public RecyclerView rv;
    public RecyclerView.Adapter adapter;


    public OnlineListController(Activity activity ,Network network){
        this.initListener = initListener;
        this.activity = activity;
        this.list = new ArrayList<USER>();
        this.net = network;
        initAdapter();
        initView();
        if(net != null) {
            net.addListener(this);
        }

        done = true;
    }


    private void initView() {
        rv = (RecyclerView)activity.findViewById(R.id.list);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
    }

    private void initAdapter() {
        adapter = new RecyclerView.Adapter(){

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(activity).inflate(R.layout.item_user, parent, false);
                return new mHolder(v);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
                USER u = OnlineListController.this.list.get(position);
                mHolder hd = (mHolder)holder;
                //hd.headPic.setImageBitmap();
                hd.nickName.setText(u.getNickname());
                hd.userLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        OnlineListController.this.jump2Chat(position);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return list.size();
            }

            class mHolder extends RecyclerView.ViewHolder
            {

                ImageView headPic;
                TextView nickName;
                LinearLayout userLayout;

                public mHolder(View itemView) {
                    super(itemView);
                    headPic = (ImageView)itemView.findViewById(R.id.headPic);
                    nickName = (TextView)itemView.findViewById(R.id.nickname);
                    userLayout= (LinearLayout) itemView.findViewById(R.id.item_user);

                }
            }
        };
    }

    public void update(final OnlineListListener listListener) {

        if(net == null)
        {
            Util.restart(activity);
            return;
        }

        if(!net.isVaild())
        {
            if(listListener != null)
                listListener.onDisconnection(null);
            return;
        }


        if(!done)
            return;;

        done = false;

        net.getOnlineList(new OnlineListListener() {
            @Override
            public void onResultsCome(Connection connection, int i, List<USER> list) {
                updateList(list);
                if(adapter != null)
                    adapter.notifyDataSetChanged();
                if(listListener != null)
                    listListener.onResultsCome(connection,i,list);
                done = true;
            }
            @Override
            public void onDisconnection(Connection conn) {
                if(listListener != null)
                    listListener.onDisconnection(conn);
            }
        });
    }

    public void update()
    {
        update(null);
    }

    private void updateList(List<USER> newList) {
        list.clear();
        list.addAll(newList);
    }

    private boolean isUserExist(USER user,List<USER> users) {
        for(USER u : users)
        {
            if(u.getUsername().equals(user.getUsername()))
            {
                return true;
            }
        }
        return false;
    }

    public void jump2Chat(int position)
    {
        USER u = list.get(position);
        if(u == null)
            return;
        Intent intent = new Intent(activity,ChatActivity.class);
        intent.putExtra("target", Tool.toJson(u));
        activity.startActivity(intent);
    }

    @Override
    public void onDataCome(Connection connection, String s) {

    }

    @Override
    public void onDisconnection(Connection connection) {
        Util.showText(OnlineListController.this.activity,R.string.err_disconnect);
    }

    public interface  InitListener {
        void onInit(boolean success);
    }
}
