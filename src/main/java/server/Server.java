package server;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    // Храним канал и его буфер(сообщение)
    private static Map<SocketChannel, ByteBuffer> sockets = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        FileWriter fw = new FileWriter("src/main/resources/logs/serverLogs.log", false);
        fw.close();
        String[] settings = readFile().split(" ");

        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress("localhost", Integer.parseInt(settings[1])));
        serverChannel.configureBlocking(false);

        Selector selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        log("SERVER START");
        try {
            while (true) {
                selector.select();
                for (SelectionKey event : selector.selectedKeys()) {
                    if (event.isValid()) {
                        try {
                            if (event.isAcceptable()) {
                                SocketChannel socketChannel = serverChannel.accept();
                                socketChannel.configureBlocking(false);
                                log("Connected: " + socketChannel.getRemoteAddress());
                                //чтобы socketChanel хранил 1000 бит информации
                                sockets.put(socketChannel, ByteBuffer.allocate(1000));
                                socketChannel.register(selector, SelectionKey.OP_READ);
                            } else if (event.isReadable()) {
                                SocketChannel socketChannel = (SocketChannel) event.channel();
                                ByteBuffer buffer = sockets.get(socketChannel);
                                int byteRead = socketChannel.read(buffer);
                                if (byteRead == -1) {
                                    log("Connection closed " + socketChannel.getRemoteAddress());
                                    sockets.remove(socketChannel);
                                    socketChannel.close();
                                }
                                // Если байт > 0, то в этот сокет можно что-то записать
                                if (byteRead > 0) {
                                    socketChannel.register(selector, SelectionKey.OP_WRITE);
                                }
                            } else if (event.isWritable()) {
                                SocketChannel socketChannel = (SocketChannel) event.channel();
                                ByteBuffer buffer =sockets.get(socketChannel);

                                buffer.flip();
                                String clientMsg = new String(buffer.array(), buffer.position(), buffer.limit());

                                buffer.clear();
                                buffer.put(ByteBuffer.wrap(("Пользователь: [" +
                                        socketChannel.getRemoteAddress() + "] отправил сообщение: " + clientMsg)
                                        .getBytes()));
                                buffer.flip();

//                                socketChannel.write(buffer);
                                // По идее мы проходимся по пользователям, которые сейчас онлайн
                                // и отправляем им сообщение, которое получили
                                for (SocketChannel user:sockets.keySet()){
                                    user.write(buffer);
                                }

                                log("Пользователь: [" +
                                        socketChannel.getRemoteAddress() + "] отправил сообщение: " + clientMsg);
                                if (!buffer.hasRemaining()) {
                                    buffer.compact();
                                    socketChannel.register(selector, SelectionKey.OP_READ);
                                }
                            }
                        } catch (IOException e) {
                            log("Error " + e.getMessage());
                        }
                    }
                }
                selector.selectedKeys().clear();
            }
        } catch (IOException err) {
        } finally {
            serverChannel.close();
        }


    }

    // проверить как работает сам сервер(возвращает сообщения, клиенту и проверить через телнет)
    private static void sendMessageOnline(SocketAddress localAddress, ByteBuffer buffer) throws IOException {


    }

    private static String readFile() {
        StringBuilder builder = new StringBuilder();
        try (FileReader fileReader = new FileReader("src/main/resources/settings/settings.log")) {
            int c;
            while ((c = fileReader.read()) != -1) {
                builder.append((char) c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    private static void log(String msg) throws IOException {
        String path = "src/main/resources/logs/serverLogs.log";
        try (FileWriter fw = new FileWriter(path, true)) {
            fw.append(msg)
                    .append("\n");
            fw.flush();
        }
    }
}
