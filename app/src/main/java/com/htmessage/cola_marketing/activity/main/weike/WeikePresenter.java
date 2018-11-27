package com.htmessage.cola_marketing.activity.main.weike;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;

import java.util.ArrayList;
import java.util.List;

public class WeikePresenter implements WeikeContract.Presenter {
    private static final String uid = HTApp.getInstance().getUsername();
    private WeikeContract.View view;
    private WeikeContract.DetailView detailView;

    public WeikePresenter(WeikeContract.View view) {
        this.view = view;
        view.setPresenter(this);
    }

    public WeikePresenter(WeikeContract.DetailView detailView) {
        this.detailView = detailView;
        detailView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void getWeikeList(int page, final boolean isRefresh) {
        final Context context = view.getBaseActivity();
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid",uid));
        params.add(new Param("page",page+""));
        new OkHttpUtils(context).post(params, HTConstant.URL_WEIKE_POST_LIST, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                view.stopRefresh();
                Log.d("getWeikeList",jsonObject.toString());
                switch (jsonObject.getInteger("code")){
                    case 1:
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        if (isRefresh) {
                            view.refreshList(JSONArray.parseArray(jsonArray.toJSONString(),JSONObject.class));
                        } else {
                            view.loadMore(JSONArray.parseArray(jsonArray.toJSONString(),JSONObject.class));
                        }
                        break;
                    default:
                        Toast.makeText(context, R.string.just_nothing,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                view.stopRefresh();
                Log.e("getWeikeList",errorMsg);
                Toast.makeText(context, R.string.request_failed_msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void deleteWeike(final int position, String id) {
        final Context context = view.getBaseActivity();
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid",uid));
        params.add(new Param("post_id",id));
        new OkHttpUtils(context).post(params, HTConstant.URL_WEIKE_DELETE_POST, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                switch (jsonObject.getInteger("code")){
                    case 1:
                        view.deletePost(position);
                        Toast.makeText(context, R.string.delete_sucess,Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(context, R.string.delete_failed,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(context, R.string.delete_failed,Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void sendComment(final String tid, String content) {
        final Context context = detailView.getBaseContext();
        List<Param> params = new ArrayList<>();
        params.add(new Param("text",content));
        params.add(new Param("uid",uid));
        params.add(new Param("post_id",tid));
        new OkHttpUtils(context).post(params, HTConstant.URL_WEIKE_SEND_COMMENT, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                switch (jsonObject.getInteger("code")){
                    case 1:
                        getCommentList(1,tid,true);
                        Toast.makeText(context, R.string.sending_success,Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(context, R.string.update_failed,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Log.e("sendComment",errorMsg);
                Toast.makeText(context, R.string.update_failed,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void getWeikeDetail(String tid) {
        final Context context = detailView.getBaseContext();
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid",uid));
        params.add(new Param("post_id",tid));
        new OkHttpUtils(context).post(params, HTConstant.URL_WEIKE_POST_DETAIL, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("getWeikeDetail",jsonObject.toString());
                switch (jsonObject.getInteger("code")){
                    case 1:
                        JSONObject object = jsonObject.getJSONObject("data");
                        detailView.showCommentDetail(object);
                        break;
                    default:
                        Toast.makeText(context, R.string.just_nothing,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Log.e("getWeikeDetail",errorMsg);
                Toast.makeText(context, R.string.request_failed_msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void getCommentList(int page, String tid, final boolean isRefresh) {
        final Context context = detailView.getBaseContext();
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid",uid));
        params.add(new Param("post_id",tid));
        params.add(new Param("page",page+""));
        new OkHttpUtils(context).post(params, HTConstant.URL_WEIKE_COMMENT_LIST, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                detailView.stopRefresh();
                Log.d("getCommentList",jsonObject.toString());
                switch (jsonObject.getInteger("code")){
                    case 1:
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        if (isRefresh) {
                            detailView.refreshList(JSONArray.parseArray(jsonArray.toJSONString(),JSONObject.class));
                        } else {
                            detailView.updateList(JSONArray.parseArray(jsonArray.toJSONString(),JSONObject.class));
                        }
                        break;
                    default:
                        Toast.makeText(context, "暂无评论",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                detailView.stopRefresh();
                Log.e("getCommentList",errorMsg);
                Toast.makeText(context, R.string.request_failed_msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void deleteComment(final int position, String id) {
        final Context context = detailView.getBaseContext();
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid",uid));
        params.add(new Param("post_comment_id",id));
        new OkHttpUtils(context).post(params, HTConstant.URL_WEIKE_DELETE_COMMENT, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                switch (jsonObject.getInteger("code")){
                    case 1:
                        detailView.deleteComment(position);
                        Toast.makeText(context, R.string.delete_sucess,Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(context, R.string.delete_failed,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(context, R.string.delete_failed,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void postFabulous(final int position, String tid, String pid, final int type) {
        final Context context = detailView.getBaseContext();
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid",uid));
        params.add(new Param("post_id",tid));
        params.add(new Param("post_comment_id",pid));
        params.add(new Param("type",type+""));
        new OkHttpUtils(context).post(params, HTConstant.URL_WEIKE_POST_FABULOUS, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("postFabulous",jsonObject.toString());
                switch (jsonObject.getInteger("code")){
                    case 1:
                        JSONObject data = jsonObject.getJSONObject("data");
                        int goods = data.getInteger("goods");
                        detailView.updateFabulous(position,goods,type);
                        break;
                    case 2:
                        break;
                    default:
                        Toast.makeText(context, R.string.praise_failed,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Log.e("postFabulous",errorMsg);
                Toast.makeText(context, R.string.praise_failed,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void getReplyList(final int position, int page, String pid) {
        final Context context = detailView.getBaseContext();
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid",uid));
        params.add(new Param("post_comment_id",pid));
        params.add(new Param("page",page+""));
        new OkHttpUtils(context).post(params, HTConstant.URL_WEIKE_COMMENT_REPLY_LIST, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("getReplyList",jsonObject.toString());
                switch (jsonObject.getInteger("code")){
                    case 1:
                        JSONArray data = jsonObject.getJSONArray("data");
                        detailView.updateInside(position,data);
                        break;
                    default:
                        Toast.makeText(context, R.string.just_nothing,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Log.e("getReplyList",errorMsg);
                Toast.makeText(context, R.string.request_failed_msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void deleteReply(final int position, final int insidePosition, String id) {
        final Context context = detailView.getBaseContext();
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid",uid));
        params.add(new Param("post_comment_reply_id",id));
        new OkHttpUtils(context).post(params, HTConstant.URL_WEIKE_DELETE_COMMENT_REPLY, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                switch (jsonObject.getInteger("code")){
                    case 1:
                        detailView.deleteReply(position,insidePosition);
                        Toast.makeText(context, R.string.delete_sucess,Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(context, R.string.delete_failed,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(context, R.string.delete_failed,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void sendReply(final int position, String pid, String tid, String content) {
        final Context context = detailView.getBaseContext();
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid",uid));
        params.add(new Param("post_comment_id",pid));
        params.add(new Param("to_uid",tid));
        params.add(new Param("text",content));
        new OkHttpUtils(context).post(params, HTConstant.URL_WEIKE_SEND_REPLY, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("sendReply",jsonObject.toString());
                switch (jsonObject.getInteger("code")){
                    case 1:
                        JSONObject data = jsonObject.getJSONObject("data");
                        detailView.sendSuccess(position,data);
                        break;
                    default:
                        Toast.makeText(context, R.string.just_nothing,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Log.e("sendReply",errorMsg);
                Toast.makeText(context, R.string.request_failed_msg,Toast.LENGTH_SHORT).show();
            }
        });
    }


}


