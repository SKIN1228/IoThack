import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TcpClientTest {

	static final String HOST1 = "192.168.11.2";  //tajima
	static final String HOST2 = "192.168.11.6";  //sakaino
	static final int PORT = 10000;

	/**
     * @param args
     */
    public static void main(String[] args) {
        Socket socket1 = null;
        Socket socket2 = null;
        try {
            socket1 = new Socket(HOST1,PORT);
            socket2 = new Socket(HOST2,PORT);
            PrintWriter pw1 = new PrintWriter(socket1.getOutputStream(),true);
            PrintWriter pw2 = new PrintWriter(socket2.getOutputStream(),true);
            while(true){
            	System.out.println("please input ...");
            	InputStreamReader isr = new InputStreamReader(System.in);
            	BufferedReader br = new BufferedReader(isr);
            	String buf = br.readLine();
            	pw1.println(buf);
            	pw2.println(buf);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if( socket1 != null){
            try {
                socket1.close();
                socket1 = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if( socket2 != null){
            try {
                socket2.close();
                socket2 = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
