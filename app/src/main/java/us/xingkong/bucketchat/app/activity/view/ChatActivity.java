package us.xingkong.bucketchat.app.activity.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Common.Tool;
import cn.jiguang.imui.commons.ImageLoader;
import cn.jiguang.imui.commons.models.IMessage;
import cn.jiguang.imui.messages.MessageList;
import cn.jiguang.imui.messages.MsgListAdapter;
import network.bucketobject.USER;
import us.xingkong.bucketchat.R;
import us.xingkong.bucketchat.app.activity.controller.ChatController;
import us.xingkong.bucketchat.app.activity.modle.DefaultUser;
import us.xingkong.bucketchat.app.activity.modle.MyMessage;
import us.xingkong.bucketchat.others.Util;


/**
 * Created by SeaLynn0 on 2017/7/12.
 */

public class ChatActivity extends BaseActivity implements View.OnClickListener {
    private AppCompatEditText et_send;
    private AppCompatImageButton bt_send;
    private AppCompatImageButton bt_camera;
    private AppCompatImageButton bt_photo;
    private AppCompatImageButton bt_emo;
    private AppCompatImageButton bt_record;

    private MessageList messageList;
    private List<MyMessage> mData;
    private MsgListAdapter<MyMessage> mAdapter;

    private File mTmpFile;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_ALBUM_OK = 2;

    private ChatController controller;
    private USER targetUser;

    @Override
    protected int getContentView() {
        return R.layout.activity_chat;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initService();

    }

    @Override
    protected void onServiceBind(boolean isSuccess) {
        if(!isSuccess)
        {
            Util.showText(this,R.string.err_service);
            return;
        }
        getDataFromIntent();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);

        }
        initViews();
        getPermission();
        setListeners();

        mData = getMessages();
        initMsgAdapter();
        controller = new ChatController(this,mAdapter,targetUser,getNet());
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        String target = intent.getCharSequenceExtra("target").toString();
        if(target != null)
        {
            targetUser = Tool.JSON2E(target,USER.class);
            actionbar.setTitle(targetUser.getNickname());
        }

    }

    private void initMsgAdapter() {
        ImageLoader imageLoader = new ImageLoader() {
            @Override
            public void loadAvatarImage(ImageView avatarImageView, String string) {
                if (string.contains("R.drawable")) {
                    Integer resId = getResources().getIdentifier(string.replace("R.drawable.", ""), "drawable", getPackageName());

                    avatarImageView.setImageResource(resId);
                } else {
                    Glide.with(getApplicationContext())
                            .load(string)
                            .placeholder(R.drawable.aurora_headicon_default)
                            .into(avatarImageView);
                }
            }

            @Override
            public void loadImage(ImageView imageView, String string) {
                // You can use other image load libraries.
                Glide.with(getApplicationContext())
                        .load(string)
                        .fitCenter()
                        .placeholder(R.drawable.aurora_picture_not_found)
                        .override(400, Target.SIZE_ORIGINAL)
                        .into(imageView);
            }
        };

        MsgListAdapter.HoldersConfig holdersConfig = new MsgListAdapter.HoldersConfig();
        mAdapter = new MsgListAdapter<>("0", holdersConfig, imageLoader);
        mAdapter.setMsgLongClickListener(new MsgListAdapter.OnMsgLongClickListener<MyMessage>() {
            @Override
            public void onMessageLongClick(MyMessage message) {
                Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.message_long_click_hint), Toast.LENGTH_SHORT).show();
            }
        });

        mAdapter.setOnAvatarClickListener(new MsgListAdapter.OnAvatarClickListener<MyMessage>() {
            @Override
            public void onAvatarClick(MyMessage message) {
                DefaultUser userInfo = (DefaultUser) message.getFromUser();
                Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.avatar_click_hint) + userInfo.getDisplayName(), Toast.LENGTH_SHORT).show();
            }
        });


        mAdapter.addToEnd(mData);
        mAdapter.setOnLoadMoreListener(new MsgListAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int page, int totalCount) {
                if (totalCount <= mData.size()) {
                    Log.i("ChatActivity", "Loading next page");
                    loadNextPage();
                }
            }
        });
        messageList.setAdapter(mAdapter);
        mAdapter.getLayoutManager().scrollToPosition(0);
    }

    private void getPermission() {

    }

    private void setListeners() {
        bt_photo.setOnClickListener(this);
        bt_camera.setOnClickListener(this);
    }

    private void initViews() {
        et_send = (AppCompatEditText) findViewById(R.id.et_send);
        bt_send = (AppCompatImageButton) findViewById(R.id.btn_send);
        bt_camera = (AppCompatImageButton) findViewById(R.id.btn_camera);
        bt_photo = (AppCompatImageButton) findViewById(R.id.btn_photo);
        bt_emo = (AppCompatImageButton) findViewById(R.id.btn_emoji);
        bt_record = (AppCompatImageButton) findViewById(R.id.btn_record);

        messageList = (MessageList) findViewById(R.id.msg_list);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_camera:
                openCamera();
                break;
            case R.id.btn_photo:
                openGallery();
                break;
            case R.id.btn_send:
                controller.sendMessage(new MyMessage(et_send.getText().toString(), IMessage.MessageType.SEND_TEXT));
                et_send.setText(null);
                break;
            case R.id.btn_emoji:
                Toast.makeText(this, "请用输入法的Emoji表情", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_record:
                Toast.makeText(this, "暂未实现", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void openGallery() {
        Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(albumIntent, REQUEST_ALBUM_OK);
    }

    private void openCamera() {
        // 跳转到系统照相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // 设置系统相机拍照后的输出路径
            // 创建临时文件
            mTmpFile = Util.createFile(getApplicationContext());
            Uri photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", mTmpFile);

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        } else {
            Toast.makeText(getApplicationContext(), R.string.msg_no_camera, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAMERA:
                    String photoPath = mTmpFile.getAbsolutePath();
                    controller.sendImage(photoPath);
                    break;
                case REQUEST_ALBUM_OK:
                    String _photoPath = data.getData().toString();
                    controller.sendImage(_photoPath);
                    break;
            }
        }
    }

    private void loadNextPage() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.addToEnd(mData);
            }
        }, 1000);
    }

    private List<MyMessage> getMessages() {
        List<MyMessage> list = new ArrayList<>();
//        Resources res = getResources();
//        String[] messages = res.getStringArray(R.array.messages_array);
//        for (int i = 0; i < messages.length; i++) {
//
//            MyMessage message;
//            if (i % 2 == 0) {
//                message = new MyMessage(messages[i], IMessage.MessageType.RECEIVE_TEXT);
//                message.setUserInfo(deadpool);
//            } else {
//                message = new MyMessage(messages[i], IMessage.MessageType.SEND_TEXT);
//                message.setUserInfo(ironman);
//            }
//            message.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
//            list.add(message);
//        }
        Collections.reverse(list);
        return list;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return  super.onOptionsItemSelected(item);
    }
}
