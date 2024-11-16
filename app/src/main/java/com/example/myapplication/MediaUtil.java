package com.example.myapplication;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

public class MediaUtil {
    private static MediaPlayer mediaPlayer;

    //播放铃声
    public static void playRing(Context context){
        try{
            //获取手机默认铃声
            Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
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
    public static void stopRing(){
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.stop(); //停止播放
            mediaPlayer.release();  //释放相关资源

        }
    }
}
