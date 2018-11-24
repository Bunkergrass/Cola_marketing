package com.htmessage.cola_marketing.activity.moments.details;

import android.os.Bundle;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.activity.BasePresenter;
import com.htmessage.cola_marketing.activity.BaseView;

public interface MomentsContract {

    interface View extends BaseView<Presenter> {

        void finish();

        void initMomentView(JSONObject jsonObject, String timeStamp);

        void updateGoodView(JSONArray praises);

        void updateCommentView(JSONArray comments);
    }


    interface Presenter extends BasePresenter {

        void initData(Bundle bundle);

        void setGood(String aid);

        void cancelGood(String aid);

        void deleteComment(String cid);

        void delete(String aid);

        void comment(String content);
    }


}
