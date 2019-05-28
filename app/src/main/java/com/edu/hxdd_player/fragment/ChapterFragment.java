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

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.edu.hxdd_player.R;
import com.edu.hxdd_player.adapter.ChapterAdapter;
import com.edu.hxdd_player.api.ApiUtils;
import com.edu.hxdd_player.api.net.ApiCall;
import com.edu.hxdd_player.bean.ChapterBean;
import com.edu.hxdd_player.bean.media.Catalog;
import com.edu.hxdd_player.bean.media.Media;
import com.edu.hxdd_player.bean.parameters.GetChapter;
import com.edu.hxdd_player.utils.LiveDataBus;

import java.util.ArrayList;
import java.util.List;

public class ChapterFragment extends Fragment {
    private RecyclerView recyclerView;
    ChapterAdapter chapterAdapter;
    GetChapter getChapter = new GetChapter();

    public static ChapterFragment newInstance() {
        ChapterFragment fragment = new ChapterFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_recyclerview, container, false);
        initView(view);
        getData();
        return view;
    }

    /**
     * @param view
     */
    private void initView(View view) {
        recyclerView = view.findViewById(R.id.recyclerview);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        chapterAdapter = new ChapterAdapter(null);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(chapterAdapter);
        chapterAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                chapterAdapter.checked(position);
                ChapterBean chapterBean = (ChapterBean) chapterAdapter.getItem(position);
                getMedia(chapterBean.id);
            }
        });

        getChapter.publicKey = "216bf87d1ab84652f3b29b8fe8f865c4";
        getChapter.timestamp = "1559012862459";
        getChapter.businessLineCode = "ld_gk";
        getChapter.coursewareCode = "2216_ept";
        getChapter.courseCode = "04732";
        getChapter.catalogId = "314972266083385344";
        getChapter.clientCode = "123456";
        getChapter.userId = "123456654";
        getChapter.userName = "李亚飞测试";
        getChapter.validTime = "0";
        getChapter.lastTime = "0";
    }

    private void setLast(int index) {
        chapterAdapter.selectIndex = index;
        ChapterBean chapterBean = (ChapterBean) chapterAdapter.getItem(index);
        getMedia(chapterBean.id);
    }

    private void getData() {
        ApiUtils.getInstance(getContext()).getChapter(getChapter, new ApiCall<List<ChapterBean>>() {
            @Override
            protected void onResult(List<ChapterBean> data) {
                ArrayList<MultiItemEntity> res = new ArrayList<>();
                res.addAll(data);
                chapterAdapter.setNewData(res);
                setLast(0);
            }
        });
    }

    private void getMedia(String catalogId) {
        ApiUtils.getInstance(getContext()).getChapterDetail(getChapter, catalogId, new ApiCall<Catalog>() {
            @Override
            protected void onResult(Catalog data) {
                LiveDataBus.get().with("media").setValue(data.media);
            }
        });
    }
}
