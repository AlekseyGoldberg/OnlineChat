package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static Integer PORT = getPort();
    public static List<RunnableClient> CLIENTS = new ArrayList<>();

    public static void start() throws IOException {
        Socket clientSocket = null;
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            log("Server start");
            while (true) {
                clientSocket = serverSocket.accept();
                RunnableClient client = new RunnableClient(clientSocket);
                CLIENTS.add(client);
                new Thread(client).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            assert clientSocket != null;
            clientSocket.close();
            log("SERVER STOP");
        }
    }

    public static int getClients(){
        return CLIENTS.size();
    }

    private static int getPort() {
        StringBuilder builder = new StringBuilder();
        try (FileReader fileReader = new FileReader("src/main/resources/settings/ServerSettings.log")) {
            int c;
            while ((c = fileReader.read()) != -1) {
                builder.append((char) c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String [] settings=builder.toString()
                .split(" ");
        return Integer.parseInt(settings[1]);
    }

    public static void log(String msg) throws IOException {
        String path = "src/main/resources/logs/serverLogs.log";
        try (FileWriter fw = new FileWriter(path, true)) {
            fw.append(msg)
                    .append("\n");
            fw.flush();
        }
    }
    public static void  removeClient(RunnableClient client,List<RunnableClient> CLIENTS){
        CLIENTS.remove(client);
    }
}
