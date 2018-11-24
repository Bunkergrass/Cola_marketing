package com.htmessage.cola_marketing.domain;

import android.content.ContentValues;
import android.content.Context;

import com.htmessage.cola_marketing.manager.DBManager;

import java.util.List;

public class InviteMessgeDao {
    public static final String TABLE_NAME = "new_friends_msgs";
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_FROM = "username";
    public static final String COLUMN_NAME_GROUP_ID = "groupid";
    public static final String COLUMN_NAME_GROUP_Name = "groupname";
    public static final String COLUMN_NAME_TIME = "time";
    public static final String COLUMN_NAME_REASON = "reason";
    public static final String COLUMN_NAME_STATUS = "status";
    public static final String COLUMN_NAME_ISINVITEFROMME = "isInviteFromMe";
    public static final String COLUMN_NAME_GROUPINVITER = "groupinviter";
    public static final String COLUMN_NAME_UNREAD_MSG_COUNT = "unreadMsgCount";

    public InviteMessgeDao(Context context){
    }

    /**
     * save message
     * @param message
     * @return  return cursor of the message
     */
    public Integer saveMessage(InviteMessage message){
        return DBManager.getInstance().saveMessage(message);
    }

    /**
     * update message
     * @param msgId
     * @param values
     */
    public void updateMessage(int msgId,ContentValues values){
        DBManager.getInstance().updateMessage(msgId, values);
    }

    /**
     * get messges
     * @return
     */
    public List<InviteMessage> getMessagesList(){
        return DBManager.getInstance().getMessagesList();
    }

    public void deleteMessage(String from){
        DBManager.getInstance().deleteMessage(from);
    }

    public int getUnreadMessagesCount(){
        return DBManager.getInstance().getUnreadNotifyCount();
    }

    public void saveUnreadMessageCount(int count){
        DBManager.getInstance().setUnreadNotifyCount(count);
    }

}
