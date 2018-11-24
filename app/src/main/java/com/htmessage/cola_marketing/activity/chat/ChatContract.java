package com.htmessage.cola_marketing.activity.chat;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.activity.BasePresenter;
import com.htmessage.cola_marketing.activity.BaseView;
import com.htmessage.sdk.model.HTMessage;

import java.util.List;


public interface ChatContract {

    interface View extends BaseView<Presenter> {
        void showNoMoreMessage();

        void refreshListView();

        Fragment getFragment();
    }

    interface Presenter extends BasePresenter {
        void loadMoreMessages();

        void onEditTextLongClick();

        void sendVideoMessage(String videoPath, String thumbPath, int duration);

        void sendTextMessage(String content);

        HTMessage getMessageById(String msgId);

        void selectPicFromCamera();

        void selectPicFromLocal();

        void selectLocation();

        void selectVideo();

        void selectFile();

        void sendRedPackage();

        void sendTransferMessage();

        void selectCall();

        void onResult(int requestCode, int resultCode, Intent data);

        void sendVoiceMessage(String voiceFilePath, int voiceTimeLength);

        List<HTMessage> getMessageList();

        void resendMessage(HTMessage htMessage);

        void sendRedCmdMessage(JSONObject jsonObject);

        void sendTransferCmdMessage(JSONObject jsonObject);

        void deleteMessage(HTMessage htMessage);

        void copyMessage(HTMessage htMessage);

        void forwordMessage(HTMessage htMessage);

        void withdrowMessage(HTMessage htMessage, int position);

        void sendCopyMessage(String copyType, String localPath, HTMessage message, String imagePath);

        void onMessageWithdrow(String msgId);

        void onNewMessage(HTMessage htMessage);

        void onMeesageForward(HTMessage htMessage);

        void onMessageClear();
    }
}
