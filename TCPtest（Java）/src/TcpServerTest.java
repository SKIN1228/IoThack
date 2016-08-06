import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServerTest {
	static final int PORT = 10000;

    /**
     * @param args
     */
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            boolean runFlag = true;
            while(runFlag){
                System.out.println("start wait...");
                // 接続があるまでブロック
                Socket socket = serverSocket.accept();
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String str;
                while( (str = br.readLine()) != null ){
                    System.out.println(str);
                    // exitという文字列を受け取ったら終了する
                    //if( "exit".equals(str)){
                    //    runFlag = false;
                    //}
                }
                if( socket != null){
                    socket.close();
                    socket = null;
                }
            }
        } catch (IOException e) {
            System.out.println("IOException1:" + e.getMessage());
        }
        if( serverSocket != null){
            try {
                serverSocket.close();
                serverSocket = null;
            } catch (IOException e) {
            	System.out.println("IOException2:" + e.getMessage());
            }
        }
    }
}
