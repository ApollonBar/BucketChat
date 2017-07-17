package us.xingkong.bucketchat.app.activity.controller;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import network.bucketobject.USER;
import network.connection.Connection;
import network.listener.LoginListener;
import us.xingkong.bucketchat.R;
import us.xingkong.bucketchat.app.activity.view.LoginActivity;
import us.xingkong.bucketchat.app.network.Jwxt;
import us.xingkong.bucketchat.app.network.listener.SigninListener;
import us.xingkong.bucketchat.others.Util;
import us.xingkong.bucketchat.app.network.Network;

/**
 * Created by 饶翰新 on 2017/7/14.
 */

public class LoginActivityController {

    private Activity activity;
    private Class<?> nextActivity;

    public EditText et_username;
    public EditText et_password;
    public EditText et_check;
    public Button bt_login;
    public ImageView img_check;

    private Network net;
    private Jwxt j;

    public LoginActivityController(LoginActivity activity, Class<?> nextActivity, Network network) {
        this.activity = activity;
        this.nextActivity = nextActivity;
        this.j = new Jwxt();
        setNet(network);
        initView();

    }


    private void initView() {
        et_username = (EditText) activity.findViewById(R.id.et_userId);
        et_password = (EditText) activity.findViewById(R.id.et_password);
        et_check = (EditText) activity.findViewById(R.id.et_check);
        bt_login = (Button) activity.findViewById(R.id.bt_login);
        img_check = (ImageView) activity.findViewById(R.id.pic_check);

        refreshImageChcek();

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        img_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshImageChcek();
            }
        });

    }

    public void setEnable(boolean enable) {
        et_username.setEnabled(enable);
        et_password.setEnabled(enable);
        et_check.setEnabled(enable);
        bt_login.setEnabled(enable);
        img_check.setEnabled(enable);
    }

    boolean checkEditText() {
        et_username.setText(et_username.getText().toString().replace(" ", ""));
        et_password.setText(et_password.getText().toString().replace(" ", ""));
        if (et_password.getText().toString().length() > 0 && et_username.getText().toString().length() > 0)
            return true;
        else
            return false;
    }

    private void refreshImageChcek() {
        j.parse(new Jwxt.OnParseListener() {

            @Override
            public void OnParseDone(Bitmap pic, Exception e) {
                if(e!=null)
                    e.printStackTrace();
                if (pic == null) {
                    Util.showText(activity, R.string.err_network);
                    return;
                }
                img_check.setImageBitmap(pic);
            }

        });
    }

    private void login() {
        if (net == null) {
            Util.showText(activity, R.string.err_net_null);
            return;
        }
        setEnable(false);

        if (!checkEditText()) {
            Util.showText(activity, R.string.err_input_illegal);
            return;
        }

        final String[] info = new String[]{et_username.getText().toString(), et_password.getText().toString(), et_check.getText().toString()};

        j.login(info[0], info[1], info[2], new Jwxt.OnLoginListener() {
            @Override
            public void OnLoginDone(final Jwxt.Student student, final Exception e) {
                if (e != null || student == null) {
                    e.printStackTrace();
                    return;
                }
                final USER user = new USER(info[0], Util.encode(info[1]));

                net.Login(user, new LoginListener() {
                    @Override
                    public void onDone(Connection connection, boolean b) {

                        if (b) {
                            setEnable(true);
                            Util.jumpAndClose(activity, nextActivity);
                        } else {
                            user.setNickname(student.getXm());
                            net.Signin(user, new SigninListener() {
                                @Override
                                public void onDone(Connection connection, boolean b) {
                                    setEnable(true);
                                    if (!b)
                                    {
                                        refreshImageChcek();
                                        Util.showText(activity, R.string.err_login);
                                    }
                                    else {
                                        Util.jumpAndClose(activity, nextActivity);
                                    }
                                }

                                @Override
                                public void onDisconnection(Connection conn) {
                                    refreshImageChcek();
                                    Util.showText(activity, R.string.err_disconnect);
                                    try {
                                        setEnable(true);
                                    } catch (NullPointerException ne) {
                                    }

                                }

                                @Override
                                public void onException(Exception e) {
                                    refreshImageChcek();
                                    Util.showText(activity, e.toString());
                                    try {
                                        setEnable(true);
                                    } catch (NullPointerException ne) {
                                    }

                                }
                            });
                        }
                    }

                    @Override
                    public void onDisconnection(Connection conn) {
                        refreshImageChcek();
                        Util.showText(activity, R.string.err_disconnect);
                        try {
                            setEnable(true);
                        } catch (NullPointerException ne) {
                        }

                    }

                    @Override
                    public void onException(Exception e) {
                        refreshImageChcek();
                        Util.showText(activity, e.toString());
                        try {
                            setEnable(true);
                        } catch (NullPointerException ne) {
                        }

                    }
                });
            }
        });
    }

    public void setNet(Network net) {
        this.net = net;
    }

    public Network getNet() {
        return net;
    }


}

