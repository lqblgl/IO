package masterandslave;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;


public class Acceptor implements Runnable {

    private final ServerSocketChannel ssc;
    private final int cores = Runtime.getRuntime().availableProcessors();
    private final Selector[] selectors = new Selector[cores];
    private int selIdx = 0;
    private TCPSubReactor[] r = new TCPSubReactor[cores];
    private Thread[] t = new Thread[cores];

    public Acceptor(ServerSocketChannel ssc) throws IOException {
        this.ssc = ssc;
        for (int i = 0; i < cores; i++) {
            selectors[i] = Selector.open();
            r[i] = new TCPSubReactor(selectors[i], ssc, i);
            t[i] = new Thread(r[i]);
            t[i].start();
        }
    }

    @Override
    public void run() {
        try {
            SocketChannel sc = ssc.accept();
            System.out.println(sc.socket().getRemoteSocketAddress().toString() + "is connected");
            if (null != sc) {
                sc.configureBlocking(false);
                r[selIdx].setRestart(true);
                selectors[selIdx].wakeup();
                SelectionKey sk = sc.register(selectors[selIdx], SelectionKey.OP_READ);
                selectors[selIdx].wakeup();
                r[selIdx].setRestart(false);
                sk.attach(new TCPHandler(sk, sc));
                if (++selIdx == selectors.length) {
                    selIdx = 0;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
