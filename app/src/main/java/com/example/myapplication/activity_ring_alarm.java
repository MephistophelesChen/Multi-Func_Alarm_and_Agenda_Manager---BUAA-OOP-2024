package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;

public class activity_ring_alarm extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring_alarm);
        MediaUtil.playRing(this);

        //TODO: UI、响铃逻辑、关闭/延迟（懒人模式）逻辑、铃声选择逻辑
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaUtil.stopRing();
    }
}
