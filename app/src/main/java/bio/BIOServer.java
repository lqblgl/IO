package bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BIOServer {
    public static void main(String[] args) {
        try {
            final ServerSocket serverSocket = new ServerSocket(8080);
            while (true) {
                try {
                    //阻塞的方法1
                    final Socket accept = serverSocket.accept();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                byte[] data = new byte[1024];
                                //阻塞的方法2
                                InputStream inputStream = accept.getInputStream();
                                int length;
                                while ((length = inputStream.read(data)) != -1) {
                                    System.out.println(new String(data, 0, length));
                                    accept.getOutputStream().write(("收到客戶端的信息:"+new String(data, 0, length)).getBytes());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
