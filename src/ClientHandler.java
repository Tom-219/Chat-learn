import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{

    public static ArrayList<ClientHandler> clientHandler = new ArrayList<>();

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWrite;
    private String clientUsername;

    public ClientHandler (Socket socket){
        try{
            this.socket = socket;
            this.bufferedWrite = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandler.add(this);
            broadcastMessage(" SERVER: " + clientUsername + " has entered the chat!");

        }   catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWrite);
        }
    }




    @Override
    public void run() {
        String messageFromClient;

        while(socket.isConnected()){
            try{
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            }   catch (IOException e){
                closeEverything(socket, bufferedReader, bufferedWrite);
                break;
            }

        }
    }

    public void broadcastMessage(String messageToSend)  {
        for(ClientHandler clientHandler : clientHandler){
            try{
                if(!clientHandler.clientUsername.equals(clientUsername)){
                    clientHandler.bufferedWrite.write(messageToSend);
                    clientHandler.bufferedWrite.newLine();
                    clientHandler.bufferedWrite.flush();
                }
            }   catch (IOException e){
                closeEverything(socket, bufferedReader, bufferedWrite);
            }
        }
    }

    public void removeClientHandler(){
        clientHandler.remove(this);
        broadcastMessage("SERVER: " +  clientUsername + " has left chat!");
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeClientHandler();
        try{
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        }   catch (IOException e){
            e.printStackTrace();
        }
    }
}

// Test git add lan 2
