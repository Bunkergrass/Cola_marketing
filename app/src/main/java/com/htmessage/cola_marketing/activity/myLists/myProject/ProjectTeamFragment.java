package com.htmessage.cola_marketing.activity.myLists.myProject;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.common.BigImageActivity;
import com.htmessage.cola_marketing.activity.moments.widget.SquareImageView;
import com.htmessage.cola_marketing.domain.Project;
import com.htmessage.cola_marketing.utils.CommonUtils;
import com.htmessage.cola_marketing.utils.DensityUtil;
import com.htmessage.cola_marketing.utils.HTPathUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.iwf.photopicker.PhotoPicker;


public class ProjectTeamFragment extends Fragment {
    EditText et_name,et_intro;
    GridView ll_images;

    private static final int REQUEST_CODE_CAMERA = 0;
    private static final int REQUEST_CODE_LOCAL = 1;
    private File cameraFile;
    private List<String> pathList = new ArrayList<>();
    private ImageAdapter adapter;
    boolean isAdd;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_project_team, container, false);

        et_name = root.findViewById(R.id.et_team_name);
        et_intro = root.findViewById(R.id.et_team_intro);
        ll_images = root.findViewById(R.id.ll_team_img);

        setView();

        return root;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        final AddProjectActivity activity = (AddProjectActivity) getActivity();
        if (activity != null) {
            activity.showRightTextView("保存", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.pathList = pathList;
                    activity.project.setTeam_name(et_name.getText().toString());
                    activity.project.setTeam(et_intro.getText().toString());
                    activity.onBackPressed();
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) { // capture new image
                if (cameraFile != null && cameraFile.exists()) {
                    pathList.add(cameraFile.getAbsolutePath());
                    adapter.notifyDataSetChanged();
                }
            } else if (requestCode == REQUEST_CODE_LOCAL) { // send local image
                if (data != null) {
                    ArrayList<String> list = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    if (list != null) {
                        pathList.addAll(list);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setView() {
        final AddProjectActivity activity = (AddProjectActivity) getActivity();

        if (activity != null) {
            activity.showRightTextView("保存", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.pathList = pathList;
                    activity.project.setTeam_name(et_name.getText().toString());
                    activity.project.setTeam(et_intro.getText().toString());
                    activity.onBackPressed();
                }
            });
        }

        adapter = new ImageAdapter(activity,pathList);
        ll_images.setAdapter(adapter);
        ll_images.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (pathList.size() < 3 && position == pathList.size()) {
                    showPhotoDialog();
                } else {
                    checkDialog(position);
                }
            }
        });

        if (activity != null && !activity.isAdd)
            showData(activity.project);
    }

    private void showData(Project project) {
        et_name.setText(project.getTeam_name());
        et_intro.setText(project.getTeam());
        pathList.addAll(transfer(project.getTeam_pic()));
        adapter.notifyDataSetChanged();
    }

    private List<String> transfer(String[] strings) {
        for (int i = 0; i < strings.length; i++) {
            strings[i] = HTConstant.baseImgUrl + strings[i];
        }
        return Arrays.asList(strings);
    }

    private void showPhotoDialog() {
        final AlertDialog dlg = new AlertDialog.Builder(getActivity()).create();
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.dialog_social_main);

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = DensityUtil.dp2px(getActivity(),280);
        window.setAttributes(lp);

        TextView tv_paizhao = window.findViewById(R.id.tv_content1);
        tv_paizhao.setText(R.string.attach_take_pic);
        tv_paizhao.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SdCardPath")
            public void onClick(View v) {
                if (!CommonUtils.isSdcardExist()) {
                    Toast.makeText(getActivity(), R.string.sd_card_does_not_exist, Toast.LENGTH_SHORT).show();
                    return;
                }
                cameraFile = new File(new HTPathUtils(null, getActivity()).getImagePath() + "/" + HTApp.getInstance().getUsername()
                        + System.currentTimeMillis() + ".png");
                Uri uri = FileProvider.getUriForFile(getActivity(),getActivity().getPackageName()+".provider",cameraFile);
                startActivityForResult(
                        new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, uri),
                        REQUEST_CODE_CAMERA);

                dlg.cancel();
            }
        });
        TextView tv_xiangce = window.findViewById(R.id.tv_content2);
        tv_xiangce.setText(R.string.image_manager);
        tv_xiangce.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PhotoPicker.builder()
                        .setPhotoCount(3 - pathList.size())
                        .setShowCamera(true)
                        .setShowGif(false)
                        .setPreviewEnabled(false)
                        .start(getContext(),ProjectTeamFragment.this, REQUEST_CODE_LOCAL);
                dlg.cancel();
            }
        });
    }

    private void checkDialog(final int position) {
        final AlertDialog dlg = new AlertDialog.Builder(getActivity()).create();
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.dialog_social_main);

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = DensityUtil.dp2px(getActivity(),280);
        window.setAttributes(lp);

        TextView tv_paizhao = window.findViewById(R.id.tv_content1);
        tv_paizhao.setText("看大图");
        tv_paizhao.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), BigImageActivity.class);
                intent.putExtra("images", pathList.toArray(new String[pathList.size()]));
                intent.putExtra("page", position);
                intent.putExtra("isNetUrl", false);//表示非网络图片,而是本地图片
                startActivity(intent);
                dlg.cancel();
            }
        });
        TextView tv_xiangce = window.findViewById(R.id.tv_content2);
        tv_xiangce.setText(R.string.delete);
        tv_xiangce.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pathList.remove(position);
                adapter.notifyDataSetChanged();
                dlg.cancel();
            }
        });
    }

    private static class ImageAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private Context context;
        private List<String> list;

        public ImageAdapter(Context context, List<String> list) {
            this.context = context;
            this.list = list;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            int total = list.size();
            if (total < 3)
                total++;
            return total;
        }

        @Override
        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SquareImageView iv_grid_moment = new SquareImageView(context);
            convertView = iv_grid_moment;
            //ImageView iv_grid_moment = convertView.findViewById(R.id.iv_grid_moment);
            iv_grid_moment.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            if (position == list.size() && list.size() < 3){
                Glide.with(context).load(R.drawable.icon_add).into(iv_grid_moment);
            } else {
                RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.default_image);
                Glide.with(context).load(getItem(position)).apply(options).into(iv_grid_moment);
            }
            return convertView;
        }

    }

}
