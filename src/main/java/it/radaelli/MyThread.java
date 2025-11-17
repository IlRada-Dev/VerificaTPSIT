package it.radaelli;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyThread implements Runnable {

    private Socket socket;
    private List<Messaggio> messageLog = Collections.synchronizedList(new ArrayList<>());

    public MyThread(Socket socket, List<Messaggio> messageLog) {
        this.socket = socket;
        this.messageLog = messageLog;
    }

    @Override
    public void run() {

        System.out.println("Un cliente si è collegato: " + socket.getInetAddress());

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Welcome");

            String autore = "";
            StringBuilder clientInput = new StringBuilder();
            StringBuilder responseBuilder = new StringBuilder();

            String line;

            while ((line = in.readLine()) != null) { //--ENDCOMMANDS or append
                if (line.equals("ENDCOMMANDS")){};
                clientInput.append(line).append("\n");
            }

            String[] commands = clientInput.toString().split("\n");//--split in commands

            for (String command : commands) { //--gestisci comandi

                String[] parts = command.split(" ", 2);//--split in command and text 
                String cmd = parts[0];//--command 

                switch (cmd) {

                    case "LOGIN":
                        if (autore.equals("") && parts.length > 1) {
                            autore = parts[1];
                            responseBuilder.append("OK\n");
                        } else {
                            responseBuilder.append("ERR LOGINREQUIRED\n");
                        }
                        break;

                    case "ADD":
                        if (parts.length < 2) {
                            responseBuilder.append("ERR SYNTAX\n");
                            break;
                        }
                        Messaggio m = new Messaggio(null, null);
                        m.autore = autore;
                        m.testo = parts[1];
                        messageLog.add(m);
                        responseBuilder.append("OK ADDED ").append(m.id).append("\n");
                        break;

                    case "LIST":
                        for (Messaggio msg : messageLog) {
                            responseBuilder.append("[").append(msg.id).append("] ")
                                    .append(msg.autore).append(": ")
                                    .append(msg.testo).append("\n");
                        }
                        responseBuilder.append("END\n");
                        break;

                    case "DEL":
                        try {
                            if (parts.length < 2) {
                                responseBuilder.append("ERR SYNTAX\n");
                                break;
                            }
                            int id = Integer.parseInt(parts[1]);
                            int idIndex = findMessage(messageLog, id, autore);

                            if (idIndex == -1)
                                responseBuilder.append("ERR NOT FOUND\n");
                            else {
                                messageLog.remove(idIndex);
                                responseBuilder.append("OK DELETED ").append(id).append("\n");
                            }
                        } catch (NumberFormatException e) {
                            responseBuilder.append("ERR SYNTAX\n");
                        }
                        break;

                    case "QUIT":
                        responseBuilder.append("BYE\n");
                        break;

                    default:
                        responseBuilder.append("ERR UNKNOWNCMD\n");
                }
            }

            out.println(responseBuilder.toString()); //--print all responses

            socket.close();

        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }

    private static int findMessage(List<Messaggio> messageLog, int id, String user) {//--Trova indice del messaggio in lista
        for (int i = 0; i < messageLog.size(); i++) {
            if (messageLog.get(i).id == id && messageLog.get(i).autore.equals(user)) {
                return i;
            }
        }
        return -1;
    }
}
