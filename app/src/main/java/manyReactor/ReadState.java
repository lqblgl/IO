package manyReactor;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadPoolExecutor;

public class ReadState implements HandlerState {
    private SelectionKey sk;
    @Override
    public void changeState(TCPHandler h) {
        h.setState(new WorkState());
    }

    @Override
    public void handle(TCPHandler h, SelectionKey sk, SocketChannel sc, ThreadPoolExecutor pool) throws IOException {
        this.sk = sk;
        byte[] arr = new byte[1024];
        ByteBuffer buf = ByteBuffer.wrap(arr);
        int numBytes = sc.read(buf);
        if (numBytes == -1) {
            System.out.println("[warning!] A client has been closed");
            h.closeChannel();
            return;
        }
        String str = new String(arr);
        if (null != str && !str.equals(" ")) {
            h.setState(new WorkState());
            pool.execute(new WorkerThread(h,str));
            System.out.println(sc.socket().getRemoteSocketAddress().toString() + ">" + str);
        }
    }

    class WorkerThread implements Runnable{

        TCPHandler h;
        String str;
        public WorkerThread(TCPHandler h, String str) {
            this.h = h;
            this.str = str;
        }

        @Override
        public void run() {
            process(h,str);
        }
    }

    private synchronized void process(TCPHandler h, String str) {
        process(str);
        h.setState(new WriteState());
        this.sk.interestOps(SelectionKey.OP_WRITE);
        this.sk.selector().wakeup();
    }

    private void process(String str) {
    }
}
