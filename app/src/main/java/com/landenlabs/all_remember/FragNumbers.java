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

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Locale;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Sample fragment demonstrate GridView with expanding cell and callouts.
 */
public class FragNumbers extends FragBottomNavBase implements View.OnClickListener {

    private ViewGroup digitsHolder;
    private TextView digitsTv;
    private ViewGroup  targetHolder;
    private TextView targetDigitsTv;
    private GridLayout numPad;
    private Vibrator vibrator;
    private Button doneBtn;
    private ImageButton playBtn;
    private ImageButton pauseBtn;
    private ImageButton stopBtn;
    private TextView secondsTv;

    private int value = 0;
    private int target = 0;

    private int DIGITS = 6;
    private int MAX_VAL = 10;

    private static final int DEL = -1;
    private static final int CLR = -2;
    /*
            Numeric Pad
            7 8 9
            4 5 6
            1 2 3
            0 x C
     */
    private int[] nums = { 7, 8, 9, 4, 5, 6, 1, 2, 3, 0, DEL, CLR};
    private int rowHeightPx;
    private static final int NUMPAD_COL = 3;
    private MediaPlayer soundError;
    private MediaPlayer soundClick;
    private MediaPlayer soundGreat;

    private boolean isRunning = false;

    private ColorStateList colorGreen = new ColorStateList(
            new int[][]{ new int[]{}},
            new int[]{  0xff00ff00 }    // GREEN
    );
    private ColorStateList colorOrange = new ColorStateList(
            new int[][]{ new int[]{}},
            new int[]{  0xffFFA500 }    // Orange
    );
    private ColorStateList colorRed = new ColorStateList(
            new int[][]{ new int[]{}},
            new int[]{  Color.RED }
    );
    private ColorStateList colorWhite = new ColorStateList(
            new int[][]{ new int[]{}},
            new int[]{  Color.WHITE }
    );

    private static final int NEW_NUM_COLOR =   0x80ff8080;
    private static final int MATCH_NUM_COLOR = 0x8000ff00;
    private static final int ANY_NUM_COLOR = Color.TRANSPARENT;
    private static final int FAIL_NUM_COLOR = 0x80ff0000;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, R.layout.frag_numbers);
        setBarTitle("Numbers");

        digitsHolder = root.findViewById(R.id.digitsHolder);
        digitsTv = root.findViewById(R.id.digits);
        secondsTv = root.findViewById(R.id.seconds);
        targetHolder = root.findViewById(R.id.targetHolder);
        targetDigitsTv = root.findViewById(R.id.target_digits);

        numPad = root.findViewById(R.id.numpad);
        doneBtn = root.findViewById(R.id.done_btn);

        playBtn = root.findViewById(R.id.play_btn);
        pauseBtn = root.findViewById(R.id.pause_btn);
        stopBtn = root.findViewById(R.id.stop_btn);

        setup();

        MAX_VAL = (int)Math.pow(10, DIGITS);
        setPlayBtns(PlayMode.STOP);
        targetHolder.setVisibility(View.GONE);

        return root;
    }

    enum PlayMode { STOP, PAUSE, RUNNING };
    PlayMode playMode = PlayMode.STOP;
    void setPlayBtns(PlayMode playMode) {
        this.playMode = playMode;
        playBtn.setImageTintList(colorGreen);
        pauseBtn.setImageTintList(colorOrange);
        stopBtn.setImageTintList(colorRed);

        switch(playMode) {
            case RUNNING:
                playBtn.setImageResource(R.drawable.btn_next);
                break;
            case PAUSE:
                stopBtn.setImageTintList(colorWhite);
                pauseBtn.setImageTintList(colorWhite);
                playBtn.setImageResource(R.drawable.btn_play);
                break;
            case STOP:
                stopBtn.setImageTintList(colorWhite);
                pauseBtn.setImageTintList(colorWhite);
                playBtn.setImageResource(R.drawable.btn_play);
                break;
        }
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        // See setHasOptionsMen(true)
    }

    private void setup() {

        doneBtn.setOnClickListener(this);
        playBtn.setOnClickListener(this);
        pauseBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);

        soundError = MediaPlayer.create(getContextSafe(), R.raw.alert_pling);
        soundGreat = MediaPlayer.create(getContextSafe(), R.raw.tada);
        vibrator = (Vibrator) getContextSafe().getSystemService(VIBRATOR_SERVICE);

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

    private String getStr(int value) {
        switch (value) {
            case CLR:
                return "CLR";
            case DEL:
                return "DEL";
            default:
                return String.format(Locale.US, "%6d", value);
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

    long SHOW_NUM_MILLI = 2000;
    long CLOCK_STEP = 500;
    long elpasedMilli = 0;

    Runnable clock = new Runnable() {
        @Override
        public void run() {
            elpasedMilli += CLOCK_STEP;
            secondsTv.setText(String.format(Locale.US, "%.1f sec", elpasedMilli/1000f));
            secondsTv.postDelayed(clock, CLOCK_STEP);
        }
    };

    Runnable newNumberRunnable = new Runnable() {
        @Override
        public void run() {
            elpasedMilli = 0;
            secondsTv.setText("");
            newNumber();
        }
    };

    private void newNumber() {
        digitsHolder.setVisibility(View.GONE);
        targetHolder.setVisibility(View.VISIBLE);
        numPad.setVisibility(View.VISIBLE);

        digitsTv.setBackgroundColor(NEW_NUM_COLOR);
        target = (int)Math.round(Math.random()* (MAX_VAL-1));
        targetDigitsTv.setText(getStr(target));
        value = 0;
        digitsTv.setText(getStr(value) );

        digitsTv.postDelayed(new Runnable() {
            @Override
            public void run() {
                digitsHolder.setVisibility(View.VISIBLE);
                targetHolder.setVisibility(View.GONE);
                elpasedMilli = 0;
                secondsTv.postDelayed(clock, CLOCK_STEP);
            }
        }, SHOW_NUM_MILLI);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.play_btn:
                setPlayBtns(PlayMode.RUNNING);
                newNumber();
                return;
            case R.id.pause_btn:
                if (this.playMode != PlayMode.RUNNING)  return;
                setPlayBtns(PlayMode.PAUSE);
                secondsTv.removeCallbacks(clock);
                return;
            case R.id.done_btn:
                if (this.playMode != PlayMode.RUNNING)  return;
                numPad.setVisibility(View.GONE);
                targetHolder.setVisibility(View.VISIBLE);
                // return;
            case R.id.stop_btn:
                if (this.playMode != PlayMode.RUNNING)  return;
                setPlayBtns(PlayMode.STOP);
                secondsTv.removeCallbacks(clock);
                elpasedMilli = 0;
                secondsTv.setText("");
                return;
        }

        if (view.getTag(R.id.tag_numpad) != null) {
            if (this.playMode != PlayMode.RUNNING)  return;
            vibrator.vibrate(30);
            digitsTv.setBackgroundColor(ANY_NUM_COLOR);

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
                    if (value > MAX_VAL) {
                        value = value / 10;
                        soundError.start();
                    } else {
                        if (value == target) {
                            soundGreat.start();
                            digitsTv.setBackgroundColor(MATCH_NUM_COLOR);
                            digitsTv.postDelayed(newNumberRunnable, 2000);
                        } else {
                            digitsTv.setBackgroundColor(FAIL_NUM_COLOR);
                            // numPad.setVisibility(View.GONE);
                            // targetHolder.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
            }

            digitsTv.setText(getStr(value));
        }
    }


}
