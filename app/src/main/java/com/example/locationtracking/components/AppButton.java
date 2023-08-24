package com.example.locationtracking.components;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

import com.example.locationtracking.util.ButtonAnimUtil;

public class AppButton extends AppCompatButton {

    public AppButton(Context context) {
        super(context);
    }

    public AppButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        ButtonAnimUtil.addBounceEffect(this);
        Typeface tf = Typeface.createFromAsset(context
                .getAssets(), "fonts/" + "MONTSERRAT-SEMIBOLD.OTF");

        setTypeface(tf);
    }


    public AppButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        ButtonAnimUtil.addBounceEffect(this);
        Typeface tf = Typeface.createFromAsset(context
                .getAssets(), "fonts/" + "MONTSERRAT-SEMIBOLD.OTF");

        setTypeface(tf);
    }
}
