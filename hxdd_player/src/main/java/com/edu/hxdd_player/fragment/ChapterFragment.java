package com.edu.hxdd_player.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.edu.hxdd_player.R;
import com.edu.hxdd_player.adapter.ChapterAdapter;
import com.edu.hxdd_player.api.ApiUtils;
import com.edu.hxdd_player.api.net.ApiCall;
import com.edu.hxdd_player.bean.ChapterBean;
import com.edu.hxdd_player.bean.media.Catalog;
import com.edu.hxdd_player.bean.parameters.GetChapter;
import com.edu.hxdd_player.utils.LiveDataBus;
import com.edu.hxdd_player.utils.StartPlayerUtils;

import java.util.ArrayList;
import java.util.List;

public class ChapterFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView tvDownload;
    private View viewLine;
    ChapterAdapter chapterAdapter;
    GetChapter getChapter;

    public static ChapterFragment newInstance(GetChapter getChapter) {
        ChapterFragment fragment = new ChapterFragment();
        Bundle args = new Bundle();
        args.putSerializable("getChapter", getChapter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            getChapter = (GetChapter) args.getSerializable("getChapter");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hxdd_player_layout_recyclerview, container, false);
        initView(view);
        if (getChapter != null) {
            getData();
        }
        return view;
    }

    /**
     * @param view
     */
    private void initView(View view) {
        recyclerView = view.findViewById(R.id.hxdd_player_recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        chapterAdapter = new ChapterAdapter(null);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(chapterAdapter);
        chapterAdapter.setOnItemClickListener((adapter, view1, position) -> {
            if (chapterAdapter.isMedia(position)) {
                ChapterBean chapterBean = (ChapterBean) chapterAdapter.getItem(position);
                if (chapterAdapter.downloadMode) {//下载模式 废弃了
                    getChapter.id = chapterBean.id;
                    LiveDataBus.get().with("download").setValue(getChapter);
                } else {
                    chapterAdapter.checked(position);//播放模式
                    getMedia(chapterBean.id);
                }
            }
        });
        tvDownload = view.findViewById(R.id.tv_download);
        viewLine = view.findViewById(R.id.line);
//        viewLine.setBackgroundColor(StartPlayerUtils.getColorPrimary());
        tvDownload.setTextColor(StartPlayerUtils.getColorPrimary());

        tvDownload.setOnClickListener(v -> {
            chapterAdapter.changeMode();
        });
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
                if (TextUtils.isEmpty(getChapter.catalogId))
                    setLast(0);
                else {
                    setLast(chapterAdapter.getCheckedIndex(getChapter.catalogId));
                }
                LiveDataBus.get().with("chatper").setValue(data);
            }

            @Override
            public void onApiFailure() {
                super.onApiFailure();
                if (getActivity() != null)
                    getActivity().finish();
            }
        });
    }

    private void getMedia(String catalogId) {
        ApiUtils.getInstance(getContext()).getChapterDetail(getChapter, catalogId, new ApiCall<Catalog>() {
            @Override
            protected void onResult(Catalog data) {

                LiveDataBus.get().with("Catalog").setValue(data);
            }
        });
    }

}
