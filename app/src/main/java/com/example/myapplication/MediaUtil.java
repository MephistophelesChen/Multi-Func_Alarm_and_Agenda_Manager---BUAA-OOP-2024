package com.example.myapplication;


import android.content.Context;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

public class MediaUtil {
    public static MediaPlayer mediaPlayer=new MediaPlayer();

    //播放铃声
    public static void playRing(Context context,Uri alert) {
        try {
            //获取手机默认铃声
            mediaPlayer = new MediaPlayer();

            mediaPlayer.setDataSource(context, alert);//根据Uri加载音频文件
            //mediaPlayer播放铃声流
            mediaPlayer.setAudioStreamType(RingtoneManager.TYPE_RINGTONE);
            mediaPlayer.setLooping(true);   //循环播放，最好在设置中增加开关
            mediaPlayer.prepare();  //装载音频文件准备播放
            mediaPlayer.start();    //开始或恢复播放，pause可以暂停
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //播放停止
    public static void stopRing() {
     //   if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.stop();
            }catch (IllegalStateException e)
            {
                Log.d("1111","11111");
                mediaPlayer=null;
                mediaPlayer=new MediaPlayer();
            }
            //停止播放
         //释放相关资源
        // 停止振动，把这个放在这边纯属方便
        if (main_alarm_activity.isVibrating) {
            VibrateUtil.stopVibration();
            main_alarm_activity.isVibrating = false;
        }
        mediaPlayer.release();

    }
}
