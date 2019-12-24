package com.landenlabs.all_remember;

/*
 * Copyright (C) 2019 Dennis Lang (landenlabs@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;

import java.util.Locale;

/**
 * Sample fragment demonstrate GridView with expanding cell and callouts.
 */
public class FragNumbers extends FragBottomNavBase implements View.OnClickListener {
    private GridLayout numPad;
    private TextView digitsTv;
    private int value = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, R.layout.frag_numbers);
        setBarTitle("Numbers");

        digitsTv = root.findViewById(R.id.digits);
        numPad = root.findViewById(R.id.numpad);
        setup();
        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        // See setHasOptionsMen(true)
    }

    private static final int DEL = -1;
    private static final int CLR = -2;
    /*
    7 8 9
    4 5 6
    1 2 3
    0 x C
     */
    private int[] nums = { 7, 8, 9, 4, 5, 6, 1, 2, 3, 0, DEL, CLR};
    private int rowHeightPx;
    private static final int NUMPAD_COL = 3;

    private void setup() {
        // GridView.LayoutParams lp = new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, rowHeightPx);
        rowHeightPx = getContextSafe().getResources().getDimensionPixelSize(R.dimen.page_row_height);
        numPad.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        numPad.setColumnCount(NUMPAD_COL);
        numPad.setRowCount((nums.length + NUMPAD_COL -1)/NUMPAD_COL);

        for (int pos = 0; pos < nums.length; pos++) {
            int row = pos/NUMPAD_COL;
            int col = pos%NUMPAD_COL;

            Button textView = makeText();
            // textView.setBackgroundColor(((pos&1)==1)? 0xf0cccccc : 0xf0dddddd);

            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.height = ViewGroup.LayoutParams.WRAP_CONTENT;     //  rowHeightPx;
            param.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            int marginPx = 5;
            param.rightMargin = marginPx;
            param.leftMargin = marginPx;
            param.bottomMargin = marginPx;
            param.topMargin = marginPx;
            param.setGravity(Gravity.CENTER);
            param.columnSpec = GridLayout.spec(col);
            param.rowSpec = GridLayout.spec(row);

            // The last parameter in the specs is the weight, which gives equal size to the cells
            param.columnSpec = GridLayout.spec(col, 1, 1);
            param.rowSpec = GridLayout.spec(row, 1, 1);

            textView.setLayoutParams(param);
            numPad.addView(textView, pos);
            int padPx = 5;
            textView.setPadding(padPx, padPx, padPx, padPx);

            textView.setOnClickListener(this);
            textView.setTag(R.id.tag_numpad, nums[pos]);
            textView.setText(getStr(nums[pos]));
        }
    }

    private String getStr(int num) {
        switch (num) {
            case CLR:
                return "CLR";
            case DEL:
                return "DEL";
            default:
                return String.valueOf(num);
        }
    }

    private Button makeText() {
        Button textView = new Button(getContextSafe());
            // new Button(new ContextThemeWrapper(getContext(), R.style.Widget_AppCompat_Button), null, 0);
        textView.setBackgroundResource(R.drawable.btn_states);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setTextColor(Color.WHITE);
        return textView;
    }

    @Override
    public void onClick(View view) {
        if (view.getTag(R.id.tag_numpad) != null) {

            int num = (Integer)view.getTag(R.id.tag_numpad);
            switch (num) {
                case DEL:
                    value = value/10;
                    break;
                case CLR:
                    value = 0;
                    break;
                default:
                    value = value*10 + num;
                    break;
            }

            digitsTv.setText(String.format(Locale.US, "%6d", value));
        }
    }
}
