package masterandslave;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TCPHandler implements Runnable {
    private final SocketChannel sc;
    private final SelectionKey sk;
    private static final int THREAD_COUNTING = 10;
    private static ThreadPoolExecutor pool = new ThreadPoolExecutor(THREAD_COUNTING,THREAD_COUNTING,
            10, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>());
    private HandlerState state;

    public void setState(HandlerState state) {
        this.state = state;
    }

    public TCPHandler(SelectionKey sk, SocketChannel sc) {
        this.sk = sk;
        this.sc = sc;
        state = new ReadState();
        pool.setMaximumPoolSize(32);
    }

    @Override
    public void run() {
        try {
            state.handle(this,sk,sc,pool);
        } catch (IOException e) {
            System.out.println("[warning!] A client has been closed");
            closeChannel();
        }
    }

    public void closeChannel() {
        try {
            sk.cancel();
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
