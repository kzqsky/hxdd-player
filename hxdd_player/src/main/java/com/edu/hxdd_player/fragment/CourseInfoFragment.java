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
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;

/**
 * 课程介绍
 */
public class CourseInfoFragment extends Fragment {
    AgentWeb mAgentWeb;
    LinearLayout layout;
    String url;

    public static CourseInfoFragment newInstance(String url) {
        CourseInfoFragment fragment = new CourseInfoFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            url =  args.getString("url");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hxdd_player_just_webview, container, false);
        layout = view.findViewById(R.id.layout);
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
                .ready().go(url);
    }

    @Override
    public void onPause() {
        if (mAgentWeb != null && mAgentWeb.getWebLifeCycle() != null)
            mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        if (mAgentWeb != null && mAgentWeb.getWebLifeCycle() != null)
            mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (mAgentWeb != null && mAgentWeb.getWebLifeCycle() != null)
            mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }
}
