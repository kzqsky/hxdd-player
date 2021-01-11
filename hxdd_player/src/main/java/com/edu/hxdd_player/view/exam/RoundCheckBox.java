package com.edu.hxdd_player.view.exam;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatCheckBox;

public class RoundCheckBox extends AppCompatCheckBox {

    public  RoundCheckBox(Context context) {
        this(context, null);
    }

    public  RoundCheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, androidx.appcompat.R.attr.radioButtonStyle);
    }

    public  RoundCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}