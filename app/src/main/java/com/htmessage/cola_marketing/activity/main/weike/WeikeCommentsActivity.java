package com.htmessage.cola_marketing.activity.main.weike;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.activity.common.BigImageActivity;
import com.htmessage.cola_marketing.activity.moments.widget.SquareImageView;
import com.htmessage.cola_marketing.utils.DensityUtil;
import com.htmessage.cola_marketing.widget.swipyrefresh.SwipyRefreshLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WeikeCommentsActivity extends BaseActivity implements SwipyRefreshLayout.OnRefreshListener, WeikeContract.DetailView {
    SwipyRefreshLayout srl_weike_comment;
    RecyclerView rv_comments;
    EditText et_weike_commment;
    TextView tv_send_comment;

    WeikeCommentsAdapter adapter;
    List<JSONObject> objects = new ArrayList<>();
    WeikeContract.Presenter presenter;
    WeikePresenter weikePresenter;

    static int page = 1;
    private String post_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weike_comments);
        weikePresenter = new WeikePresenter(this);
        initView();
        getData();
        setView();
    }

    @Override
    protected void onStop() {
        weikePresenter = null;
        super.onStop();
    }

    private void initView() {
        srl_weike_comment = findViewById(R.id.srl_weike_comment);
        rv_comments = findViewById(R.id.rv_weike_comments);
        et_weike_commment = findViewById(R.id.et_weike_commment);
        tv_send_comment = findViewById(R.id.tv_send_comment);
    }

    private void getData() {
        post_id = getIntent().getStringExtra("post_id");
        presenter.getWeikeDetail(post_id);
        presenter.getCommentList(page,post_id,true);
    }

    private void setView() {
        setTitle("帖子");
        srl_weike_comment.setOnRefreshListener(this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        adapter = new WeikeCommentsAdapter(this,objects);
        rv_comments.setLayoutManager(manager);
        rv_comments.setAdapter(adapter);
        adapter.setListener(new WeikeCommentsAdapter.AdpterListener() {
            @Override
            public void postFabulous(int position, String tid, String pid, int type) {
                presenter.postFabulous(position,tid,pid,type);
            }

            @Override
            public void sendReply(int position, String pid, String tid) {
                showInputDialog(position,pid,tid);
            }

            @Override
            public void getReplyList(int position, int page, String pid) {
                presenter.getReplyList(position,page,pid);
            }
        });

        tv_send_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = et_weike_commment.getText().toString();
                if (text.isEmpty())
                    Toast.makeText(WeikeCommentsActivity.this,R.string.input_text,Toast.LENGTH_SHORT).show();
                else
                    presenter.sendComment(post_id,text);
            }
        });
        et_weike_commment.addTextChangedListener(new MyInputListener());

        showRightTextView("分享", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    class MyInputListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length()>0 && s.length()<200){
                tv_send_comment.setEnabled(true);
                tv_send_comment.setTextColor(getResources().getColor(R.color.LightBlue));
            } else {
                tv_send_comment.setEnabled(false);
                tv_send_comment.setTextColor(getResources().getColor(R.color.text_color_light));
            }
        }
    }

    private void showInputDialog(final int position, final String pid, final String tid) {
        View view = View.inflate(WeikeCommentsActivity.this,R.layout.dialog_input,null);
        ImageView iv_cancel = view.findViewById(R.id.iv_dialog_cancel);
        TextView tv_title = view.findViewById(R.id.tv_dialog_title);
        final EditText et_input = view.findViewById(R.id.et_dialog_input);
        Button btn_send = view.findViewById(R.id.btn_dialog_send);

        tv_title.setText("回复内容");
        et_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(WeikeCommentsActivity.this);
        builder.setView(view);
        final AlertDialog dialog = builder.show();
        //dialog.show();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = DensityUtil.dp2px(WeikeCommentsActivity.this,280);
        window.setAttributes(lp);

        iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_input.getText().toString().isEmpty()){
                    Toast.makeText(WeikeCommentsActivity.this,R.string.input_text,Toast.LENGTH_SHORT).show();
                }else {
                    presenter.sendReply(position,pid,tid,et_input.getText().toString());
                    dialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onRefresh(int index) {
        page = 1;
        presenter.getCommentList(page,post_id,true);
    }

    @Override
    public void onLoad(int index) {
        page++;
        presenter.getCommentList(page,post_id,false);
    }

    @Override
    public void setPresenter(WeikeContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Activity getBaseActivity() {
        return this;
    }


    @Override
    public void showCommentDetail(JSONObject object) {
        View headerView = View.inflate(WeikeCommentsActivity.this,R.layout.item_weike_comments_header,null);
        TextView tv_title = headerView.findViewById(R.id.tv_weike_header_title);
        TextView tv_nick = headerView.findViewById(R.id.tv_weike_header_nick);
        ImageView iv_avatar = headerView.findViewById(R.id.iv_weike_header_avatar);
        TextView tv_time = headerView.findViewById(R.id.tv_weike_header_time);
        TextView tv_content = headerView.findViewById(R.id.tv_weike_header_content);
        LinearLayout ll_imgs = headerView.findViewById(R.id.ll_weike_header_imgs);

        String usernick = object.getString("usernick");
        String avatar = object.getString("avatar");
        int secTime = object.getInteger("add_time");
        String text = object.getString("text");
        String title = object.getString("title");
        JSONArray imgurl = object.getJSONArray("url");

        tv_nick.setText(usernick);
        tv_content.setText(text);
        tv_title.setText(title);
        tv_time.setText(getStringTime(secTime*1000));
        RequestOptions options = new RequestOptions().placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar);
        Glide.with(WeikeCommentsActivity.this).load(avatar).apply(options).into(iv_avatar);
        initImageView(JSONArray.parseArray(imgurl.toJSONString(),JSONObject.class), ll_imgs);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,0,20);
        headerView.setLayoutParams(params);
        adapter.setHeaderView(headerView);
    }

    private String getStringTime(long ms){
        Date date = new Date(ms);
        Long now = System.currentTimeMillis();
        if (now-ms > 24*60*60*1000){
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            return format.format(date);
        } else if (now-ms < (long)365*24*60*60*1000) {
            SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
            return format.format(date);
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return format.format(date);
        }
    }

    private void initImageView(List<JSONObject> objects, LinearLayout linearLayout) {
        linearLayout.removeAllViews();
        final ArrayList<String> images = new ArrayList<>();
        for (int i=0;i<objects.size();i++) {
            images.add(objects.get(i).getString("url"));
        }
        int lines = images.size() % 3 == 0 ? images.size()/3 : images.size()/3 + 1;
        for (int i=0;i<lines;i++ ) {
            LinearLayout ll_line = new LinearLayout(WeikeCommentsActivity.this);
            ll_line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            ll_line.setOrientation(LinearLayout.HORIZONTAL);
            ll_line.setWeightSum(3);
            for (int j = i*3;j < 3*(i+1); j++){
                if (j>=images.size()) break;
                String imgUrl = images.get(j);
                SquareImageView imageView = new SquareImageView(WeikeCommentsActivity.this);
                imageView.setPadding(0,0,20,20);
                ll_line.addView(imageView, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,1));
                RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_image2).centerCrop();
                Glide.with(WeikeCommentsActivity.this).load(HTConstant.baseImgUrl+imgUrl).apply(options).into(imageView);
                final int index = j;
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(WeikeCommentsActivity.this, BigImageActivity.class);
                        intent.putExtra("images", images.toArray(new String[images.size()]));
                        intent.putExtra("page", index);
                        WeikeCommentsActivity.this.startActivity(intent);
                    }
                });
            }
            linearLayout.addView(ll_line);
        }
    }

    @Override
    public void stopRefresh(){
        srl_weike_comment.setRefreshing(false);
    }

    public void refreshList(List<JSONObject> list) {
        adapter.refreshComment(list);
    }

    public void updateList(List<JSONObject> list) {
        adapter.addComment(list);
    }

    public void updateFabulous(int position, int count,int type) {
        adapter.updateFabulous(position,count,type);
    }

    @Override
    public void sendSuccess(int pos,JSONObject data) {
        et_weike_commment.setText("");
        et_weike_commment.clearFocus();
        adapter.refreshOne(pos,data);
    }

    public void updateInside(int position, JSONArray data) {
        adapter.updateInside(position,data);
    }

}
