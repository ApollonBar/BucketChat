package us.xingkong.bucketchat.app.activity.controller;

import android.app.Activity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.jiguang.imui.commons.models.IMessage;
import cn.jiguang.imui.messages.MsgListAdapter;
import network.bucketobject.Message;
import network.bucketobject.USER;
import network.connection.Connection;
import network.listener.MessageListener;
import us.xingkong.bucketchat.R;
import us.xingkong.bucketchat.app.activity.modle.DefaultUser;
import us.xingkong.bucketchat.app.activity.modle.MyMessage;
import us.xingkong.bucketchat.app.activity.modle.MessageType;
import us.xingkong.bucketchat.app.network.Network;
import us.xingkong.bucketchat.others.Util;

/**
 * Created by SeaLynn0 on 2017/7/13.
 * Update by 饶翰新 on 2017/7/16.
 */

public class ChatController extends MessageListener{

    private Activity activity;
    private MsgListAdapter<MyMessage> mAdapter;

    private USER targetUser;
    private USER selfUser;

    private DefaultUser defaultUserSelf;
    private DefaultUser defaultUserTarget;

    private Network net;

    public ChatController(Activity activity,MsgListAdapter<MyMessage> mAdapter,USER targetUser,Network network) {
        this.activity = activity;
        this.mAdapter = mAdapter;
        this.targetUser = targetUser;
        this.net = network;

        selfUser = net.getUser();

        initDefaultUser();
        net.setMessageListener(ChatController.this);


    }

    public void initDefaultUser() {
        defaultUserTarget = new DefaultUser(targetUser.getUsername(),targetUser.getNickname(), "R.drawable.deadpool");
        defaultUserSelf = new DefaultUser(selfUser.getUsername(), selfUser.getNickname(), "R.drawable.ironman");
    }

    public void sendMessage(MyMessage msg) {
        if(msg.getText().isEmpty())
            return;
        MyMessage message = msg;
        message.setUserInfo(defaultUserSelf);
        message.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
        mAdapter.addToStart(message, true);
        Message m = FormatSEND(message);
        m.setReceiver(targetUser.getUsername());
        net.SendMessage(m);

    }

    public void sendImage(String imagePath) {
        final MyMessage message = new MyMessage(null, IMessage.MessageType.SEND_IMAGE);
        message.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
        message.setMediaFilePath(imagePath);

        message.setUserInfo(defaultUserSelf);

        mAdapter.addToStart(message, true);



        try {
            String imageStr = URLEncoder.encode(Util.getImageBase64(Util.getRealPathFromUri(activity,imagePath)),"GBK");
            Message m = new Message();
            m.setContent(imageStr);
            m.setType(MessageType.IMAGE);
            m.setReceiver(targetUser.getUsername());
            net.SendMessage(m);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public MyMessage FormatRECEIVE(Message msg) {
        IMessage.MessageType type = IMessage.MessageType.RECEIVE_TEXT;
        String text = "";
        String image = null;
        switch (msg.getType()){
            case MessageType.TEXT : type =  IMessage.MessageType.RECEIVE_TEXT;
                text = msg.getContent();
                break;
            case MessageType.IMAGE : type =  IMessage.MessageType.RECEIVE_IMAGE;
                //text = "[图片]";
                try {
                    String imageStr = URLDecoder.decode(msg.getContent(), "GBK");
                    byte[] b =  Util.base64toByte(imageStr);
                    if(b != null)
                    {
                        image = activity.getApplicationContext().getExternalFilesDirs(null)[0]+ "/" + String.valueOf(msg.getContent().hashCode());
                        System.out.println(image);
                        Util.writeFile(image,b);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                break;
        }
        MyMessage re = new MyMessage(text,type);
        if(image != null)
            re.setMediaFilePath(image);
        re.setTimeString(msg.getSendTime().toString());
        return re;
    }

    public Message FormatSEND(MyMessage msg) {
        Message re = new Message();

        String type = MessageType.TEXT;
        if(msg.getType() == IMessage.MessageType.SEND_IMAGE)
            type = MessageType.IMAGE;

        re.setContent(msg.getText());
        re.setType(type);
        return re;
    }

    @Override
    public void onMessageCome(Connection connection, Message msg) {

        if(selfUser != null)
        {
            if(msg.getSender().equals(targetUser.getUsername()))
            {
                MyMessage message = FormatRECEIVE(msg);
                message.setUserInfo(defaultUserTarget);
                message.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(msg.getSendTime()));
                mAdapter.addToStart(message, true);
            }
        }

    }

    @Override
    public void onDisconnection(Connection conn) {
        Util.showText(activity,R.string.err_disconnect);
    }

    public USER getSelfUser()
    {
        return selfUser;
    }
}
