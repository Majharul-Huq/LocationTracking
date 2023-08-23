package com.example.locationtracking;

import android.content.Context;
import android.graphics.Typeface;
import android.text.InputType;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

public class AppEditText extends AppCompatEditText {

    public AppEditText(Context context) {
        super(context);
    }

    public AppEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

//        Typeface tf = Typeface.createFromAsset(context
//                .getAssets(), "fonts/" + "MONTSERRAT-REGULAR.OTF");
//
//        setTypeface(tf);

        if (getInputType() != InputType.TYPE_CLASS_NUMBER) {
            setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        }
    }
}
