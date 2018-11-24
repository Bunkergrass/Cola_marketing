package com.htmessage.cola_marketing.domain;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.utils.CommonUtils;

@SuppressLint("ParcelCreator")
public class User implements Parcelable {
    protected String initialLetter;
    protected String avatar;
    private String username;
    protected String userInfo;
    private String nick;

    public User(String username){
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getNick() {
        return nick;
    }
    public void setNick(String nick) {
        this.nick = nick;
    }

    /**
     * initial letter for nickname
     */
    public String getInitialLetter() {
        if(initialLetter == null){
            CommonUtils.setUserInitialLetter(this);
        }
        return initialLetter;
    }
    public void setInitialLetter(String initialLetter) {
        this.initialLetter = initialLetter;
    }

    /**
     * avatar of the user
     */
    public String getAvatar() {
        if(!TextUtils.isEmpty(avatar)){
            if (!avatar.contains("http")){
                //get avatar from net
                avatar = HTConstant.URL_AVATAR+avatar;
            }
        }
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUserInfo(){
        return userInfo;
    }
    public void setUserInfo(String userInfo){
        this.userInfo=userInfo;
    }

    @Override
    public int hashCode() {
        return 17 * getUsername().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof User)) {
            return false;
        }
        return getUsername().equals(((User) o).getUsername());
    }

    /**
     * Parcelable
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //
    }
}
