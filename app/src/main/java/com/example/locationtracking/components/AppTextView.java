package com.example.locationtracking.components;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class AppTextView extends AppCompatTextView {

    public AppTextView(Context context) {
        super(context);
    }

    public AppTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Typeface tf = Typeface.createFromAsset(context
                .getAssets(), "fonts/" + "MONTSERRAT-REGULAR.OTF");

        setTypeface(tf);
    }

}
