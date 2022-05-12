package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class InMessageRunnable implements Runnable {
    private BufferedReader inMessage;

    public InMessageRunnable(Socket socket) {
        try {
            inMessage = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String line;
            while (true) {
                while ((line = inMessage.readLine()) != null) {
                    System.out.println(line);
                    Client.log(line);
                }
            }
        } catch (IOException e) {}
    }
}
