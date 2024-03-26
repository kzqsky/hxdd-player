package com.edu.hxdd_player.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.edu.hxdd_player.R;
import com.edu.hxdd_player.bean.CourseInfoBean;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;

import java.util.ArrayList;
import java.util.List;

/**
 * 课程介绍
 */
public class CourseInfoFragment extends Fragment {
    public List<CourseInfoBean.Teacher> teacherList;
    public List<CourseInfoBean.Book> textbookList;
    AgentWeb mAgentWeb;
    LinearLayout layout;
    public static CourseInfoFragment newInstance(ArrayList<CourseInfoBean.Teacher> teacherList, ArrayList<CourseInfoBean.Book> textbookList) {
        CourseInfoFragment fragment = new CourseInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable("teacherList", teacherList);
        args.putSerializable("textbookList", textbookList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            teacherList = (List<CourseInfoBean.Teacher>) args.getSerializable("teacherList");
            textbookList = (List<CourseInfoBean.Book>) args.getSerializable("textbookList");
        }
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hxdd_player_just_webview, container, false);
        layout =view.findViewById(R.id.layout);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //加载网页
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(layout, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
                .createAgentWeb()
                .ready().go( "https://www.baidu.com/");
    }
}
