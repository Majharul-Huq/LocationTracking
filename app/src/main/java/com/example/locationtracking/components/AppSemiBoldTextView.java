package com.example.locationtracking.components;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class AppSemiBoldTextView extends AppCompatTextView {

    public AppSemiBoldTextView(Context context) {
        super(context);
    }

    public AppSemiBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Typeface tf = Typeface.createFromAsset(context
                .getAssets(), "fonts/" + "MONTSERRAT-SEMIBOLD.OTF");

        setTypeface(tf);
    }
}
