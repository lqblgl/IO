package masterandslave;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        TCPReactor reactor = new TCPReactor(1333);
        Thread thread = new Thread(reactor);
        thread.start();
    }
}
