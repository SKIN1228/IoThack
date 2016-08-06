package com.example.tcp2;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {

    private Button mButtonSendOne;
    private Button mButtonSendTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonSendOne = (Button)findViewById(R.id.button_send_one);
        mButtonSendTwo = (Button)findViewById(R.id.button_send_two);



        //1が押されたとき
        mButtonSendOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TcpTest th = new TcpTest("1");
                th.start();
            }
        });

        //2が押されたとき
        mButtonSendTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TcpTest th = new TcpTest("2");
                th.start();
            }
        });

//        TcpTest tt = new TcpTest();
//        tt.start();

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
}
