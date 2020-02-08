package bio;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;

public class BIOClient {
    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket("127.0.0.1",8080);
                    while (true){
                        socket.getOutputStream().write((new Date()+"哈喽").getBytes());
                        socket.getOutputStream().flush();
                        Thread.sleep(1000);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
