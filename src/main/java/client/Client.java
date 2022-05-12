package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    static final int PORT = Integer.parseInt(getSettingsWithName("PORT"));
    static final String HOST = getSettingsWithName("HOST");


    public static void start() throws IOException {
        Socket clientSocket = new Socket(HOST, PORT);
        new Thread(new InMessageRunnable(clientSocket)).start();

        log("Соединение установлено");
        try (Scanner scanner = new Scanner(System.in);
             clientSocket;
             PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true)) {

            System.out.println("Как ваше имя?");
            String name = scanner.nextLine();
            out.print(name);
            String message;
            System.out.println("Если хотите выйти из чата напишите '/exit'" +
                    "\nНачинайте печатать");
            while (true) {
                message = scanner.nextLine();
                if (message.equalsIgnoreCase("/exit")) {
                    out.print(message);
                    break;
                }
                out.printf("%s: %s\n", name, message);
                log(String.format("%s: %s\n", name, message));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getSettingsWithName(String name) {
        StringBuilder builder = new StringBuilder();
        try (FileReader fileReader = new FileReader("src/main/resources/settings/ClientSettings.log")) {
            int c;
            while ((c = fileReader.read()) != -1) {
                builder.append((char) c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] settings = builder.toString()
                .split("\n");
        String foundProperty = null;
        for (String property : settings) {
            if (property.startsWith(name)) {
                String[] propertyInMass = property.split(" ");
                foundProperty = propertyInMass[1];
            }
        }
        return foundProperty;
    }

    public static void log(String msg) throws IOException {
        String path = "src/main/resources/logs/clientLogs.log";
        try (FileWriter fw = new FileWriter(path, true)) {
            fw.append(msg)
                    .append("\n");
            fw.flush();
        }
    }
}
