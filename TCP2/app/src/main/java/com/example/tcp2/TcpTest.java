package com.example.tcp2;

        import java.io.BufferedReader;
        import java.io.BufferedWriter;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.OutputStream;
        import java.io.OutputStreamWriter;
        import java.net.Socket;

        import android.util.Log;

public class TcpTest extends Thread {

    private	String	m_szIp = "192.168.11.5";	//アクセス先IP
    private	int		m_nPort = 5000;			//アクセス先ポート
    private String num;  //1か2がはいる


    public TcpTest(String num){
        this.num = num;
    }

    @Override
    public void run()
    {
        try
        {
            //通信用ソケット作成
            Socket socket = new Socket( m_szIp, m_nPort );

//            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

//            BufferedReader br = new BufferedReader( new InputStreamReader(in, "UTF-8")  );
            BufferedWriter bw = new BufferedWriter( new OutputStreamWriter(out, "UTF-8")  );

            //テキストを送る
            bw.write(num);

            //データを確定させて通信処理を起こさせる
            bw.flush();

            //相手からのデータ待ち
//            String	szData = br.readLine();

            //表示する
//            Log.d( "nya", "受信文字列:" + szData );

            //後処理
//            in.close();
            out.close();
            socket.close();
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }
}
