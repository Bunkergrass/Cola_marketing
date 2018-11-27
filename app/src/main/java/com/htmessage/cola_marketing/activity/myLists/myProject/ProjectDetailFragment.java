package com.htmessage.cola_marketing.activity.myLists.myProject;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.domain.Project;
import com.htmessage.cola_marketing.widget.HintSetView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ProjectDetailFragment extends Fragment {
    EditText et_project_name,et_project_desc;
    HintSetView hsv_project_start,hsv_project_end;

    static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    final Date[] dates = new Date[] {new Date(1),new Date(0)};
    boolean isAdd;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_project_detail, container, false);
        initView(root);
        return root;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        final AddProjectActivity activity = (AddProjectActivity) getActivity();
        if (activity != null)
            activity.showRightTextView("保存", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!et_project_name.getText().toString().isEmpty() || dates[1].compareTo(dates[0]) > 0) {
                        activity.project.setExplain_name(et_project_name.getText().toString());
                        activity.project.setExplain_desc(et_project_desc.getText().toString());
                        activity.project.setExplain_start(dates[0]);
                        activity.project.setExplain_end(dates[1]);
                        activity.onBackPressed();
                    } else {
                        Toast.makeText(getActivity(),"结束时间不得小于开始时间",Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    private void initView(View root) {
        final AddProjectActivity activity = (AddProjectActivity) getActivity();
        et_project_desc = root.findViewById(R.id.et_project_desc);
        et_project_name = root.findViewById(R.id.et_project_name);
        hsv_project_start = root.findViewById(R.id.hsv_project_start);
        hsv_project_end = root.findViewById(R.id.hsv_project_end);

        hsv_project_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDataPickerDialog(true,(HintSetView) v);
            }
        });
        hsv_project_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDataPickerDialog(false,(HintSetView) v);
            }
        });
        if (activity != null)
            activity.showRightTextView("保存", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!et_project_name.getText().toString().isEmpty() || dates[1].compareTo(dates[0]) > 0) {
                        activity.project.setExplain_name(et_project_name.getText().toString());
                        activity.project.setExplain_desc(et_project_desc.getText().toString());
                        activity.project.setExplain_start(dates[0]);
                        activity.project.setExplain_end(dates[1]);
                        activity.onBackPressed();
                    } else {
                        Toast.makeText(getActivity(),"结束时间不得小于开始时间",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        if (activity != null && !activity.isAdd)
            setView(activity.project);
    }

    private void setView(Project project) {
        et_project_name.setText(project.getExplain_name());
        et_project_desc.setText(project.getExplain_desc());
        hsv_project_start.setHintText(format.format(project.getExplain_start()));
        hsv_project_end.setHintText(format.format(project.getExplain_end()));
    }


    private void showDataPickerDialog(final boolean isStart, final HintSetView hintSetView) {
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String strTime = year + "-" + month + "-" + dayOfMonth;
                hintSetView.setHintText(strTime);
                try {
                    if (isStart)
                        dates[0] = format.parse(strTime);
                    else
                        dates[1] = format.parse(strTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        },2018,1,1);
        dialog.show();
    }

}
