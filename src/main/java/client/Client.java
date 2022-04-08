package client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 8080);
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(socketAddress);

        System.out.println("Соединение установлено");
        try {
            Scanner scanner = new Scanner(System.in);
            ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 10);
            String msg;
            while (true) {
                System.out.print("Введите строку: ");
                msg = scanner.nextLine();
                if (msg.equalsIgnoreCase("end"))
                    break;
                socketChannel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
                int byteCount = socketChannel.read(inputBuffer);
                System.out.println(new String(inputBuffer.array(), 0, byteCount, StandardCharsets.UTF_8).trim());
                inputBuffer.clear();
            }

        } finally {
            socketChannel.close();
        }
    }
}
