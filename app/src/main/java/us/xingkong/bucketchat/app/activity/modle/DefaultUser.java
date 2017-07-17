package us.xingkong.bucketchat.app.activity.modle;

import cn.jiguang.imui.commons.models.IUser;

/**
 * Created by SeaLynn0 on 2017/7/13.
 */

public class DefaultUser implements IUser {

    public String username;     //学号
    public String nickname;     //姓名
    public String avatar;       //头像

    public DefaultUser(String username, String displayName, String avatar) {
        this.username = username;
        this.nickname = displayName;
        this.avatar = avatar;
    }

    @Override
    public String getId() {
        return username;
    }

    @Override
    public String getDisplayName() {
        return nickname;
    }

    @Override
    public String getAvatarFilePath() {
        return avatar;
    }
}
