package com.htmessage.cola_marketing.activity.homepageFunc.xiangqing;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.utils.DensityUtil;
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;
import com.htmessage.cola_marketing.widget.swipyrefresh.SwipyRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class ConfirmApplyActivity extends BaseActivity implements SwipyRefreshLayout.OnRefreshListener {
    private ListView lv_projects;
    private SwipyRefreshLayout srl_projects;

    private int page = 1;
    private ConfirmApplyAdapter adapter;
    private String onlyId,explainId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_apply);
        setTitle(R.string.confirm_apply);

        onlyId = getIntent().getStringExtra("onlyId");

        lv_projects = findViewById(R.id.lv_confirm_projects);
        srl_projects = findViewById(R.id.srl_confirm_projects);

        srl_projects.setOnRefreshListener(this);

        adapter = new ConfirmApplyAdapter(this,new ArrayList<JSONObject>());
        lv_projects.setAdapter(adapter);
        TextView textView = new TextView(this);
        textView.setText("如需增加新的项目，请前往【我的-项目列表】");
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getResources().getColor(R.color.text_color_light));
        textView.setTextSize(12);
        textView.setPadding(0,20,0,0);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        lv_projects.addFooterView(textView,null,false);

        lv_projects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                explainId = adapter.getProjectId(position);
            }
        });
        showRightTextView("提交", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (explainId == null)
                    Toast.makeText(ConfirmApplyActivity.this,"请选择项目",Toast.LENGTH_SHORT).show();
                else
                    selectType();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        page = 1;
        getProjects(page,true);
    }

    private void selectType() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_social_main,null);
        dialog.setView(view);
        dialog.show();

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = DensityUtil.dp2px(ConfirmApplyActivity.this,280);
        window.setAttributes(lp);

        TextView textView1 = view.findViewById(R.id.tv_content1);
        TextView textView2 = view.findViewById(R.id.tv_content2);
        TextView textView3 = view.findViewById(R.id.tv_content3);
        LinearLayout linearLayout3 = view.findViewById(R.id.ll_content3);

        linearLayout3.setVisibility(View.VISIBLE);
        textView1.setText("段子");
        textView2.setText("故事");
        textView3.setText("视频");

        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postApply(1);
                dialog.dismiss();
            }
        });
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postApply(2);
                dialog.dismiss();
            }
        });
        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postApply(3);
                dialog.dismiss();
            }
        });
    }

    private void getProjects(int page, final boolean isRefresh) {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid", HTApp.getInstance().getUsername()));
        params.add(new Param("page",page + ""));
        new OkHttpUtils(this).post(params, HTConstant.URL_MY_PROJECT_LIST, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                srl_projects.setRefreshing(false);
                Log.d("getProjects",jsonObject.toString());
                int code = jsonObject.getInteger("code");
                switch (code) {
                    case 1:
                        JSONArray data = jsonObject.getJSONArray("data");
                        List<JSONObject> list = JSONArray.parseArray(data.toJSONString(), JSONObject.class);
                        if (isRefresh)
                            adapter.refreshList(list);
                        else
                            adapter.addList(list);
                        break;
                    default:
                        Toast.makeText(ConfirmApplyActivity.this,R.string.just_nothing,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                srl_projects.setRefreshing(false);
                Toast.makeText(ConfirmApplyActivity.this,R.string.request_failed_msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postApply(int type) {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid", HTApp.getInstance().getUsername()));
        params.add(new Param("only_id", onlyId));
        params.add(new Param("explain_id", explainId));
        params.add(new Param("apply_type", type+""));
        new OkHttpUtils(this).post(params, HTConstant.URL_GO_APPLY, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject.getInteger("code") == 1) {
                    Toast.makeText(ConfirmApplyActivity.this,R.string.sending_success,Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                    Toast.makeText(ConfirmApplyActivity.this,R.string.sending_failed,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(ConfirmApplyActivity.this,R.string.sending_failed,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh(int index) {
        page = 1;
        getProjects(page,true);
    }

    @Override
    public void onLoad(int index) {
        page++;
        getProjects(page,false);
    }

    /**
     *  inner adapter
     */
    private static class ConfirmApplyAdapter extends BaseAdapter {
        private Context context;
        private List<JSONObject> list;
        private int selected = -1;

        public ConfirmApplyAdapter(Context context, List<JSONObject> list) {
            this.context = context;
            this.list = list;
        }

        private static class ViewHolder {
            ImageView iv_state;
            TextView tv_num,tv_name;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_confirm_apply,parent,false);
                holder = new ViewHolder();
                holder.iv_state = convertView.findViewById(R.id.iv_confirm);
                holder.tv_num = convertView.findViewById(R.id.tv_confirm_num);
                holder.tv_name = convertView.findViewById(R.id.tv_confirm_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tv_num.setText("项目 " + (position+1));
            holder.tv_name.setText(list.get(position).getString("explain_name"));
            if (selected == position)
                holder.iv_state.setImageResource(R.drawable.icon_shenqing_selected);
            else
                holder.iv_state.setImageResource(R.drawable.icon_shenqing_default);

            return convertView;
        }

        public String getProjectId(int postion) {
            this.selected = postion;
            notifyDataSetChanged();
            return list.get(postion).getString("explain_id");
        }

        public void refreshList(List<JSONObject> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        public void addList(List<JSONObject> list) {
            this.list.addAll(list);
            notifyDataSetChanged();
        }

    }

}
