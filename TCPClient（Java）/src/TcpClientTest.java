import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TcpClientTest {

	static final String HOST = "192.168.11.2";
	static final int PORT = 10000;

	/**
     * @param args
     */
    public static void main(String[] args) {
        Socket socket = null;
        try {
            socket = new Socket(HOST,PORT);
            PrintWriter pw = new PrintWriter(socket.getOutputStream(),true);
            while(true){
            	System.out.println("please input ...");
            	InputStreamReader isr = new InputStreamReader(System.in);
            	BufferedReader br = new BufferedReader(isr);
            	String buf = br.readLine();
            	pw.println(buf);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if( socket != null){
            try {
                socket.close();
                socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
