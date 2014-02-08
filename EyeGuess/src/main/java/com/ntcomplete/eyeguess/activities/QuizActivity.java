package com.ntcomplete.eyeguess.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.common.primitives.Booleans;
import com.ntcomplete.eyeguess.R;
import com.ntcomplete.eyeguess.helpers.JSONHelper;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author nick
 */
public class QuizActivity extends Activity {

    private  final String TAG = getClass().getSimpleName();

    public static final String EXTRA_QUIZ_CATEGORY = "QuizCategory";

    private GestureDetector mGestureDetector;

    private TextView mQuestionView;
    private TextView mScoreView;
    private TextView mTimerView;

    private View mResponseView;

    private int mScore = 0;
    private int mPassed = 0;

    private JSONHelper mJSONHelper;

    private AlphaAnimation mAlphaAnimation;

    private ArrayList<String> mQuestions;
    private ArrayList<Boolean> mResults;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Keep the screen on, please.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        int categoryId = getIntent().getExtras().getInt(EXTRA_QUIZ_CATEGORY);

        mQuestionView = (TextView) findViewById(R.id.activity_quiz_question);
        mScoreView = (TextView) findViewById(R.id.activity_quiz_score);
        mTimerView = (TextView) findViewById(R.id.activity_quiz_time_left);


        mScoreView.setText(String.valueOf(mScore));

        mResponseView = findViewById(R.id.activity_quiz_response_view);

        mJSONHelper = new JSONHelper(this, categoryId);


        mQuestions = new ArrayList<String>();
        mResults = new ArrayList<Boolean>();

        String question = mJSONHelper.getQuestion();
        mQuestionView.setText(question);
        mQuestions.add(question);

        mGestureDetector = new GestureDetector(this);
        mGestureDetector.setBaseListener(mBaseListener);

        mAlphaAnimation = new AlphaAnimation(1.f, 0.f);
        mAlphaAnimation.setDuration(750);


        final Timer timer = new Timer();

        final TimerTask task = new TimerTask() {
            int milliseconds = 0;
            int seconds = 3;
            int minutes = 0;

            @Override
            public void run() {
                milliseconds--;
                if(milliseconds <= 0) {
                    Log.d(TAG, String.format("Minute: %d, second: %d, ms: %d", minutes, seconds, milliseconds));
                    if(minutes < 0 && seconds <= 0 && milliseconds <= 0) {
                        Log.d(TAG, "Cancelling!");
                        cancel();
                        startResultActivity();
                    }
                    milliseconds = 99;
                    seconds--;
                    if(seconds <= 0) {
                        minutes--;
                        seconds = 59;
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(seconds >= 0 && minutes >= 0 && milliseconds >= 0) {
                            mTimerView.setText(String.format("%02d:%02d:%02d", minutes, seconds, milliseconds));
                        } else {
                            timer.cancel();
                            startResultActivity();
                        }
                   }});

            }
        };

        timer.scheduleAtFixedRate(task, 0, 10);

    }

    private void startResultActivity() {
        Intent resultIntent = new Intent(QuizActivity.this, ResultActivity.class);
        resultIntent.putExtra(ResultActivity.EXTRA_PASSED, mPassed);
        resultIntent.putExtra(ResultActivity.EXTRA_SCORE, mScore);
        resultIntent.putExtra(ResultActivity.EXTRA_QUESTIONS, mQuestions);
        boolean[] resultArray = Booleans.toArray(mResults);
        resultIntent.putExtra(ResultActivity.EXTRA_RESULTS, resultArray);
        startActivity(resultIntent);
    }

    GestureDetector.BaseListener mBaseListener = new GestureDetector.BaseListener() {
        @Override
        public boolean onGesture(Gesture gesture) {
            if(gesture == Gesture.SWIPE_RIGHT || gesture == Gesture.TWO_SWIPE_RIGHT) {
                mScore++;
                mScoreView.setText(String.valueOf(mScore));

                String question = mJSONHelper.getQuestion();
                mQuestions.add(question);
                mQuestionView.setText(question);
                mResults.add(true);


                mResponseView.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                mResponseView.clearAnimation();
                mResponseView.startAnimation(mAlphaAnimation);
                return true;
            }
            if(gesture == Gesture.SWIPE_LEFT || gesture == Gesture.TWO_SWIPE_LEFT) {
                mPassed++;

                String question = mJSONHelper.getQuestion();
                mQuestions.add(question);
                mQuestionView.setText(question);
                mResults.add(false);

                mResponseView.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                mResponseView.clearAnimation();
                mResponseView.startAnimation(mAlphaAnimation);
                return true;
            }

            return false;
        }
    };

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {

        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }
}
