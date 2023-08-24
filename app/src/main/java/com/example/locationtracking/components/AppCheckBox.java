package com.example.locationtracking.components;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatCheckBox;

public class AppCheckBox extends AppCompatCheckBox {

    public AppCheckBox(Context context) {
        super(context);
    }

    public AppCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);

        Typeface tf = Typeface.createFromAsset(context
                .getAssets(), "fonts/" + "MONTSERRAT-REGULAR.OTF");

        setTypeface(tf);
    }

}
