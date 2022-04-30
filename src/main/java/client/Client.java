package client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class Client {
    public static void main(String[] args) throws IOException {
        InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 8080);
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(socketAddress);

        System.out.println("Соединение установлено");
        try {
            Scanner scanner = new Scanner(System.in);
            ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 10);
            AtomicReference<String> msg = new AtomicReference<>();
            while (true) {
                Thread inputThread = new Thread(() -> {
                    System.out.print("Введите строку: ");
                    msg.set(scanner.nextLine());
                });
                inputThread.start();
                inputThread.join();
                if (msg.get().equalsIgnoreCase("/exit")) {
                    break;
                }
                socketChannel.write(ByteBuffer.wrap(msg.get().getBytes(StandardCharsets.UTF_8)));
                int byteCount = socketChannel.read(inputBuffer);
                Thread outThread = new Thread(() -> {
                    System.out.println(new String(inputBuffer.array(), 0, byteCount, StandardCharsets.UTF_8).trim());
                });
                outThread.start();
                outThread.join();
                inputBuffer.clear();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            socketChannel.close();
        }
    }
}
