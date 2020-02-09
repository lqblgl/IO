package masterandslave;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadPoolExecutor;

public class WorkState implements HandlerState {
    @Override
    public void changeState(TCPHandler h) {
        h.setState(new WriteState());
    }

    @Override
    public void handle(TCPHandler tcpHandler, SelectionKey sk, SocketChannel sc, ThreadPoolExecutor pool) {

    }
}
