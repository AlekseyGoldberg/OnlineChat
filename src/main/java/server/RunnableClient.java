package server;

import java.io.*;
import java.net.Socket;

public class RunnableClient implements Runnable {
    private PrintWriter outMessage;
    private BufferedReader inMessage;
    String name;

    public RunnableClient(Socket socket) {
        try {
            outMessage = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            inMessage = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public RunnableClient() {
    }

    @Override
    public void run() {
        try {
            name=inMessage.readLine();
            String msg;
            loop:
            while (true){
                while ((msg=inMessage.readLine())!=null){
                    if (msg.equalsIgnoreCase("/exit")){
                        sendMessageToAllClients("Пользователь "+ name+" вышел из чата",this);
                        Server.log("Пользователь "+ name+" вышел из чата");
                        break loop;
                    }
                    sendMessageToAllClients(msg,this);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close();
        }
    }
    private synchronized void sendMessageToAllClients(String msg,RunnableClient thisClient) throws IOException {
        Server.log(msg);
        for (RunnableClient client:Server.CLIENTS){
            if (client!=thisClient)
                client.sendMsg(msg);
        }
    }
    public void sendMsg(String msg) {
        try {
            outMessage.println(msg);
            outMessage.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void close(){
        Server.removeClient(this,Server.CLIENTS);
    }
}
