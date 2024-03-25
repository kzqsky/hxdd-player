package com.edu.hxdd_player.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.edu.hxdd_player.R;
import com.edu.hxdd_player.bean.CourseInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 课程介绍
 */
public class CourseInfoFragment extends Fragment {
    public List<CourseInfoBean.Teacher> teacherList;
    public List<CourseInfoBean.Book> textbookList;
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
        View view = inflater.inflate(R.layout.hxdd_player_layout_recyclerview, container, false);

        return view;
    }

}
