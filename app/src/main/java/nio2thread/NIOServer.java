package nio2thread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {
    public static void main(String[] args) {
        try {
            final Selector serversSelector = Selector.open();
            final Selector clientSelector = Selector.open();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ServerSocketChannel listenerChannel = ServerSocketChannel.open();
                        listenerChannel.socket().bind(new InetSocketAddress(8000));
                        listenerChannel.configureBlocking(false);
                        listenerChannel.register(serversSelector, SelectionKey.OP_ACCEPT);

                        while (true) {
                            //1检测是否有新的连接，这里的1指的是阻塞的时间为1ms
                            if (serversSelector.select(1) > 0) {
                                Set<SelectionKey> set = serversSelector.selectedKeys();
                                Iterator<SelectionKey> keyIterator = set.iterator();
                                while (keyIterator.hasNext()) {
                                    SelectionKey key = keyIterator.next();
                                    if (key.isAcceptable()) {
                                        try {
                                            SocketChannel clientChannel = ((ServerSocketChannel) key.channel()).accept();
                                            clientChannel.configureBlocking(false);
                                            clientChannel.register(clientSelector, SelectionKey.OP_READ);
                                        } finally {
                                            keyIterator.remove();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            //2批量轮询是否有哪些连接有数据可读，这里的1指的是阻塞的时间为1ms
                            if (clientSelector.select(1) > 0) {
                                Set<SelectionKey> set = clientSelector.selectedKeys();
                                Iterator<SelectionKey> keyIterator = set.iterator();
                                SelectionKey key = keyIterator.next();
                                try {
                                    if (key.isReadable()) {
                                        SocketChannel clientChannel = (SocketChannel) key.channel();
                                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                                        clientChannel.read(byteBuffer);
                                        byteBuffer.flip();
                                        System.out.println(Charset.defaultCharset().newDecoder().decode(byteBuffer).toString());
                                    }
                                } finally {
                                    keyIterator.remove();
                                    key.interestOps(SelectionKey.OP_READ);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
