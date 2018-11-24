package com.htmessage.cola_marketing.activity.main.weike;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.activity.BasePresenter;
import com.htmessage.cola_marketing.activity.BaseView;

import java.util.List;

public interface WeikeContract {

    interface View extends BaseView<Presenter> {

        void stopRefresh();

        void loadMore(List<JSONObject> list);

        void refreshList(List<JSONObject> list);
    }

    interface DetailView extends BaseView<Presenter> {

        void stopRefresh();

        void showCommentDetail(JSONObject object);

        void refreshList(List<JSONObject> list);

        void updateList(List<JSONObject> list);

        void updateFabulous(int position, int count, int type);

        void sendSuccess(int position,JSONObject data);

        void updateInside(int position, JSONArray data);
    }

    interface Presenter extends BasePresenter {

        void getWeikeList(int page, boolean isRefresh);

        void sendComment(String tid, String content);

        void getWeikeDetail(String tid);

        void getCommentList(int page,String tid,boolean isRefresh);

        void postFabulous(int position, String tid, String pid, int type);

        void getReplyList(int position, int page, String pid);

        void sendReply(int position, String pid, String tid, String content);
    }
}
