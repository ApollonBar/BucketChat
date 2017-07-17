package us.xingkong.bucketchat.app.activity.view;

import android.os.Bundle;

import us.xingkong.bucketchat.R;
import us.xingkong.bucketchat.app.activity.controller.LoginActivityController;
import us.xingkong.bucketchat.others.Util;

public class LoginActivity extends BaseActivity {

    LoginActivityController ctrler;

    @Override
    protected int getContentView() {
        initService();
        return R.layout.activity_login;

    }

    @Override
    protected void init(Bundle savedInstanceState) {

    }

    @Override
    protected void onServiceBind(boolean isSuccess) {
        if(isSuccess)
        {
            ctrler = new LoginActivityController(this,ListActivity.class,getNet());
        }else{
            Util.showText(this,R.string.err_service);
        }

    }
}
