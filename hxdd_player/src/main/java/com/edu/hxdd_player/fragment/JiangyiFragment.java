package com.edu.hxdd_player.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edu.hxdd_player.R;

public class JiangyiFragment extends Fragment {
    public static JiangyiFragment newInstance() {
        JiangyiFragment fragment = new JiangyiFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hxdd_player_jiangyi_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

    }
}
