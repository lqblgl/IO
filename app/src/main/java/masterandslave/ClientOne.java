package masterandslave;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientOne {
    public static void main(String[] args) throws IOException {
        String hostname = "127.0.0.1";
        int port = 1333;

        Socket client = new Socket(hostname, port);
        System.out.println("連接至目的地：" + hostname);
        PrintWriter out = new PrintWriter(client.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String input;
        while ((input = stdIn.readLine()) != null) {
            out.println(input);
            out.flush();
            if (input.equals("exit")) {
                break;
            }
            System.out.println("server:" + in.readLine());
        }
        client.close();
        System.out.println("client stop");
    }
}
