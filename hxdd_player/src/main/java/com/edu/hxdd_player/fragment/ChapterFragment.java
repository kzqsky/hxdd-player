package com.edu.hxdd_player.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.edu.hxdd_player.R;
import com.edu.hxdd_player.adapter.ChapterAdapter;
import com.edu.hxdd_player.api.ApiUtils;
import com.edu.hxdd_player.api.net.ApiCall;
import com.edu.hxdd_player.bean.ChapterBean;
import com.edu.hxdd_player.bean.ClientConfigBean;
import com.edu.hxdd_player.bean.media.Catalog;
import com.edu.hxdd_player.bean.parameters.GetChapter;
import com.edu.hxdd_player.utils.DialogUtils;
import com.edu.hxdd_player.utils.LiveDataBus;
import com.edu.hxdd_player.utils.StartPlayerUtils;
import com.edu.hxdd_player.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class ChapterFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView tvDownload;
    private View viewLine;
    ChapterAdapter chapterAdapter;
    GetChapter getChapter;
    ClientConfigBean clientConfigBean;

    /**
     * 是否正在录制视频
     */
    public boolean videoRecord = false;

    public static ChapterFragment newInstance(GetChapter getChapter, ClientConfigBean clientConfigBean) {
        ChapterFragment fragment = new ChapterFragment();
        Bundle args = new Bundle();
        args.putSerializable("getChapter", getChapter);
        args.putSerializable("clientConfigBean", clientConfigBean);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            getChapter = (GetChapter) args.getSerializable("getChapter");
            clientConfigBean = (ClientConfigBean) args.getSerializable("clientConfigBean");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hxdd_player_layout_recyclerview, container, false);
        initView(view);
        initLiveData();
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
        chapterAdapter = new ChapterAdapter(null, clientConfigBean);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(chapterAdapter);
        chapterAdapter.setOnItemClickListener((adapter, view1, position) -> {

            if (chapterAdapter.isMedia(position)) {
                ChapterBean chapterBean = (ChapterBean) chapterAdapter.getItem(position);
                if (chapterAdapter.downloadMode) {//下载模式 废弃了
                    getChapter.id = chapterBean.id;
                    LiveDataBus.get().with("download").setValue(getChapter);
                } else {
                    if (videoRecord) {
                        ToastUtils.showLong(getContext(), "视频录制中禁止切换章节");
                        return;
                    }
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

    private void initLiveData() {
        LiveDataBus.get()
                .with("localVideo", Object.class)
                .observe(this, catalog -> {
                    chapterAdapter.checked(-1);
                });

        LiveDataBus.get().with("playNext").observe(this, catalog -> {
            ChapterBean chapterBean = chapterAdapter.checkNext();
            getMedia(chapterBean.id);
        });
        LiveDataBus.get().with("refreshVid").observe(this, catalog -> {
            if (chapterAdapter.getData() != null && chapterAdapter.getData().size() > 0 && chapterAdapter.selectIndex >= 0) {
                ChapterBean chapterBean = (ChapterBean) chapterAdapter.getData().get(chapterAdapter.selectIndex);
                getMedia(chapterBean.id);
            }
        });
    }

    private void setLast(int index) {
        if (chapterAdapter.isMedia(index)) {
            chapterAdapter.selectIndex = index;
            ChapterBean chapterBean = (ChapterBean) chapterAdapter.getItem(index);
            getMedia(chapterBean.id);
        } else {
            ChapterBean chapterBean = chapterAdapter.checkNext();
            getMedia(chapterBean.id);
        }
    }

    private void getData() {
        ApiUtils.getInstance(getActivity(), getChapter.serverUrl).getChapter(getChapter, new ApiCall<List<ChapterBean>>() {
            @Override
            protected void onResult(List<ChapterBean> data) {
                ArrayList<MultiItemEntity> res = new ArrayList<>();
                res.addAll(data);
                chapterAdapter.setNewData(res);
                //由从考试系统返回改为从课件系统返回
                setLast(chapterAdapter.getCheckedIndex());
                LiveDataBus.get().with("chatper").setValue(data);
            }

            @Override
            public void onApiFailure(String message) {
                super.onApiFailure(message);
                ToastUtils.showLong(getContext(), message);
                if (getActivity() != null)
                    getActivity().finish();
            }
        });
    }

    private void getMedia(String catalogId) {
        ApiUtils.getInstance(getActivity(), getChapter.serverUrl).getChapterDetail(getChapter, catalogId, new ApiCall<Catalog>() {
            @Override
            protected void onResult(Catalog data) {
                LiveDataBus.get().with("Catalog").setValue(data);
            }

            @Override
            public void onApiFailure(String message) {
                super.onApiFailure(message);
                DialogUtils.showDialog(getContext(), message);
            }
        });
    }

}
