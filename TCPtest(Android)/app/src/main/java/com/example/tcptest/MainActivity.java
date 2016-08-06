package com.example.tcptest;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import android.os.Handler;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {

    private Button mButtonSendOne;
    private Button mButtonSendTwo;
    private ImageView mWaitImageView;
    private ImageView mSheekImage;
    private VideoView mLoadingAnimation;
    private Vibrator mVibrator;
    private Socket mServerSocket;
    private Socket mClientSocket;
    private TextView mTextViewWait;
    private Timer mTimerLimit = null;
    private Handler mHandlerLimit= new Handler();
    private static int PORTNUM = 5000;  //接続先のポート番号
    private static String IPADDRESS = "192.168.11.4";  //接続先のIPAddress
    private int mTimerCountLimit = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        final Handler handler = new Handler();

        mButtonSendOne = (Button)findViewById(R.id.button_send_one);
        mButtonSendTwo = (Button)findViewById(R.id.button_send_two);
        mWaitImageView = (ImageView) findViewById(R.id.title);
        mSheekImage=(ImageView)findViewById(R.id.mSheekImage);
        mVibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);

        mButtonSendOne.setVisibility(View.INVISIBLE);
        mButtonSendTwo.setVisibility(View.INVISIBLE);
        mSheekImage.setVisibility(View.INVISIBLE);
        mWaitImageView.setVisibility(View.VISIBLE);
        //通知領域の非表示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mLoadingAnimation = (VideoView) findViewById(R.id.videoView);
        mLoadingAnimation.setVideoPath("android.resource://com.example.tcptest/" + R.raw.load);
        mLoadingAnimation.start();
        //動画が停止したら、シークバーを最初に戻して再度スタート
        mLoadingAnimation.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
            @Override
            public void onCompletion(MediaPlayer mp) {
                // 先頭に戻す
                mLoadingAnimation.seekTo(0);
                // 再生開始
                mLoadingAnimation.start();
            }
        });

        //サーバの処理
        Runnable receiver = new Runnable() {
            @Override
            public void run() {
                int PORT = PORTNUM;
                ServerSocket serverSocket = null;

                try{
                    serverSocket = new ServerSocket(PORT);
                    boolean runFlag = true;
                    while (runFlag){
                        Log.i("state", "start wait ...");

                        //接続があるまでブロック
                        mServerSocket = serverSocket.accept();
                        //第二引数で文字コードの指定が可能
                        BufferedReader br = new BufferedReader(new InputStreamReader(mServerSocket.getInputStream()));
                        String str;
                        while((str = br.readLine()) != null){

                            //受け取った文字列をログで表示する
                            Log.i("received", str);

                            //startを受け取ったらボタンなどを表示する
                            //別スレッドのためUI操作はhandlerの中で行う
                            if("start".equals(str)) {
                                runFlag = false;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mButtonSendOne.setVisibility(View.VISIBLE);
                                        mButtonSendTwo.setVisibility(View.VISIBLE);
                                        mWaitImageView.setVisibility(View.INVISIBLE);
                                        mSheekImage.setVisibility(View.VISIBLE);
                                        mLoadingAnimation.setVisibility(View.INVISIBLE);

                                    }
                                });

                                //10秒で投票締め切り
                                mTimerLimit = new Timer(true);
                                mTimerLimit.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        mHandlerLimit.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                mButtonSendOne.setVisibility(View.INVISIBLE);
                                                mButtonSendTwo.setVisibility(View.INVISIBLE);
                                                mSheekImage.setVisibility(View.INVISIBLE);
                                                mLoadingAnimation.setVisibility(View.VISIBLE);
                                                mWaitImageView.setVisibility(View.VISIBLE);
                                                mLoadingAnimation.start();
                                                //動画が停止したら、シークバーを最初に戻して再度スタート
                                                            mLoadingAnimation.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
                                                        @Override
                                                    public void onCompletion(MediaPlayer mp) {
                                                        // 先頭に戻す
                                                        mLoadingAnimation.seekTo(0);
                                                        // 再生開始
                                                        mLoadingAnimation.start();
                                                    }
                                                });


                                                Toast.makeText(MainActivity.this, "投票を締め切りました", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }, mTimerCountLimit);
                            }

                            //vibを受け取ったら振動する
                            if("vib".equals(str)){
                                mVibrator.vibrate(500);
                            }

                            //exitを受け取ったら終了する
                            if("exit".equals(str)){
                                runFlag = false;
                            }
                        }
                    }
                }catch (IOException e){
                    Log.d("IOException", e.getMessage());
                }
            }
        };
        Thread th1 = new Thread(receiver);
        th1.start();

        //1に投票するボタンが押されたときの処理（クライアントの処理）
        mButtonSendOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //クライアントの処理
                Runnable sender = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //PCのサーバに接続
                            mClientSocket = new Socket(IPADDRESS, PORTNUM);
                            //1を投票する送る
                            PrintWriter pw = new PrintWriter(mClientSocket.getOutputStream(), true);
                            pw.println("1");
                            //一度接続を切る
                            if (mClientSocket != null) {
                                mClientSocket.close();
                                mClientSocket = null;
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mButtonSendOne.setVisibility(View.INVISIBLE);
                                    mButtonSendTwo.setVisibility(View.INVISIBLE);
                                    mSheekImage.setVisibility(View.INVISIBLE);
                                    mLoadingAnimation.setVisibility(View.VISIBLE);
                                    mWaitImageView.setVisibility(View.VISIBLE);
                                    mLoadingAnimation.start();
                                    //動画が停止したら、シークバーを最初に戻して再度スタート
                                    mLoadingAnimation.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            // 先頭に戻す
                                            mLoadingAnimation.seekTo(0);
                                            // 再生開始
                                            mLoadingAnimation.start();
                                        }
                                    });

                                    Toast.makeText(MainActivity.this, "1に投票しました", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (IOException e) {
                            Log.d("IOException", e.getMessage());
                        }
                    }
                };
                Thread th2 = new Thread(sender);
                th2.start();
            }
        });

        //2に投票するボタンが押されたときの処理（クライアントの処理）
        mButtonSendTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //クライアントの処理
                Runnable sender = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //PCのサーバに接続
                            mClientSocket = new Socket(IPADDRESS, PORTNUM);
                            //2を投票する送る
                            PrintWriter pw = new PrintWriter(mClientSocket.getOutputStream(), true);
                            pw.println("2");
                            //一度接続を切る
                            if (mClientSocket != null) {
                                mClientSocket.close();
                                mClientSocket = null;
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mButtonSendOne.setVisibility(View.INVISIBLE);
                                    mButtonSendTwo.setVisibility(View.INVISIBLE);
                                    mSheekImage.setVisibility(View.INVISIBLE);
                                    mLoadingAnimation.setVisibility(View.VISIBLE);
                                    mWaitImageView.setVisibility(View.VISIBLE);
                                    mLoadingAnimation.start();
                                    //動画が停止したら、シークバーを最初に戻して再度スタート
                                    mLoadingAnimation.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            // 先頭に戻す
                                            mLoadingAnimation.seekTo(0);
                                            // 再生開始
                                            mLoadingAnimation.start();
                                        }
                                    });

                                    Toast.makeText(MainActivity.this, "2に投票しました", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (IOException e) {
                            Log.d("IOException", e.getMessage());
                        }
                    }
                };
                Thread th2 = new Thread(sender);
                th2.start();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (mServerSocket != null) {
            try {
                mServerSocket.close();
                mServerSocket = null;
            } catch (IOException e) {
                Log.d("IOException", e.getMessage());
            }
        }
    }
}
