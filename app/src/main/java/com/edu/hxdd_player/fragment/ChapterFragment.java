package com.edu.hxdd_player.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edu.hxdd_player.R;

public class ChapterFragment extends Fragment {
    private RecyclerView recyclerView;

    public static ChapterFragment newInstance() {
        ChapterFragment fragment = new ChapterFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_recyclerview, container, false);
        initView(view);
        return view;
    }

    /**
     * @param view
     */
    private void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
//        recyclerView.addItemDecoration(new RecycleViewDivider(getContext(),
//                LinearLayoutManager.HORIZONTAL, DensityUtils.dp2px(1),
//                getResources().getColor(R.color.divider)));
//        recyclerView.setAdapter(adapter);
    }

}
