package com.example.tcptest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Handler;


public class MainActivity extends Activity {

    private Button mButtonSendOne;
    private Button mButtonSendTwo;
    private Vibrator mVibrator;
    private Socket mServerSocket;
    private Socket mClientSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonSendOne = (Button)findViewById(R.id.button_send_one);
        mButtonSendTwo = (Button)findViewById(R.id.button_send_two);
        mVibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);

        //サーバの処理
        Runnable receiver = new Runnable() {
            @Override
            public void run() {
                int PORT = 10000;
                ServerSocket serverSocket = null;

                try{
                    serverSocket = new ServerSocket(PORT);
                    boolean runFlag = true;
                    while (runFlag){
                        Log.i("state", "start wait ...");

                        //接続があるまでブロック
                        mServerSocket = serverSocket.accept();
                        BufferedReader br = new BufferedReader(new InputStreamReader(mServerSocket.getInputStream()));
                        String str;
                        while((str = br.readLine()) != null){

                            //受け取った文字列をログで表示する
                            Log.i("received", str);

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
                            mClientSocket = new Socket("192.168.11.4", 10000);
                            //1を投票する送る
                            PrintWriter pw = new PrintWriter(mClientSocket.getOutputStream(), true);
                            pw.println("1");
                            //一度接続を切る
                            if (mClientSocket != null) {
                                mClientSocket.close();
                                mClientSocket = null;
                            }
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
                            mClientSocket = new Socket("192.168.11.4", 10000);
                            //2を投票する送る
                            PrintWriter pw = new PrintWriter(mClientSocket.getOutputStream(), true);
                            pw.println("2");
                            //一度接続を切る
                            if (mClientSocket != null) {
                                mClientSocket.close();
                                mClientSocket = null;
                            }
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
