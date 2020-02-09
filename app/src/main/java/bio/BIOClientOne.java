package bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Date;

public class BIOClientOne {
    public static void main(String[] args) throws IOException {
        final Socket socket = new Socket("127.0.0.1",8080);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        byte[] data = new byte[1024];
                        InputStream inputStream = socket.getInputStream();
                        int length;
                        while ((length = inputStream.read(data)) != -1) {
                            System.out.println(new String(data, 0, length));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
