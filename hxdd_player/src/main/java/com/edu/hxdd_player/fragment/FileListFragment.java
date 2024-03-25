package com.edu.hxdd_player.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edu.hxdd_player.R;
import com.edu.hxdd_player.adapter.FileListAdapter;
import com.edu.hxdd_player.bean.ClientConfigBean;
import com.edu.hxdd_player.bean.CourseInfoBean;

import java.util.ArrayList;

/**
 * 讲义
 */
public class FileListFragment extends Fragment {
    public ArrayList<CourseInfoBean.UploadedFile> uploadedFiles;
    ClientConfigBean clientConfigBean;
    private RecyclerView recyclerView;
    FileListAdapter fileListAdapter;
    public static FileListFragment newInstance(ArrayList<CourseInfoBean.UploadedFile> uploadedFiles, ClientConfigBean clientConfigBean) {
        FileListFragment fragment = new FileListFragment();
        Bundle args = new Bundle();
        args.putSerializable("uploadedFiles", uploadedFiles);
        args.putSerializable("clientConfigBean", clientConfigBean);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            uploadedFiles = (ArrayList<CourseInfoBean.UploadedFile>) args.getSerializable("uploadedFiles");
            clientConfigBean = (ClientConfigBean) args.getSerializable("clientConfigBean");
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hxdd_player_layout_recyclerview, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.hxdd_player_recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        fileListAdapter = new FileListAdapter(uploadedFiles,clientConfigBean.download);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(fileListAdapter);

        fileListAdapter.setOnItemClickListener((baseQuickAdapter, view1, i) -> {
            if (clientConfigBean.download){


            }
        });
    }
}
