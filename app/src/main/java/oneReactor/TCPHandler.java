package oneReactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class TCPHandler implements Runnable {
    private final SocketChannel sc;
    private final SelectionKey sk;
    int state;

    public TCPHandler(SelectionKey sk, SocketChannel sc) {
        this.sk = sk;
        this.sc = sc;
        state = 0;
    }

    @Override
    public void run() {
        try {
            if (state == 0) {
                read();
            } else {
                send();
            }
        } catch (IOException e) {
            System.out.println("[warning!] A client has been closed");
            closeChannel();
        }
    }

    private void closeChannel() {
        try {
            sk.cancel();
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send() throws IOException {
        String str = "Your message has sent to" + sc.socket().getLocalSocketAddress().toString() + "\r\n";
        ByteBuffer buf = ByteBuffer.wrap(str.getBytes());
        while (buf.hasRemaining()) {
            sc.write(buf);
        }
        state = 0;
        sk.interestOps(SelectionKey.OP_READ);
        sk.selector().wakeup();
    }

    private void read() throws IOException {
        byte[] arr = new byte[1024];
        ByteBuffer buf = ByteBuffer.wrap(arr);
        int numBytes = sc.read(buf);
        if (numBytes == -1) {
            System.out.println("[warning!] A client has been closed");
            closeChannel();
            return;
        }
        String str = new String(arr);
        if (null != str && !str.equals(" ")) {
            process(str);
            System.out.println(sc.socket().getRemoteSocketAddress().toString() + ">" + str);
            state = 1;
            sk.interestOps(SelectionKey.OP_WRITE);
            sk.selector().wakeup();
        }
    }

    private void process(String str) {
    }
}
