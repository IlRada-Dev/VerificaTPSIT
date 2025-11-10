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
            while (autore == "") {
                String linea = in.readLine();
                String[] parti = linea.split(" ", 2);
                if (parti[0].equals("LOGIN")) {
                    autore = parti[1];
                    out.println("OK");
                } else {
                    out.println("ERR LOGINREQUIRED");
                }
            }
            while (true) {
                Messaggio currentMessage = new Messaggio(null, null);
                String linea = in.readLine();
                String[] parti = linea.split(" ", 2);
                switch (parti[0]) {
                    case "LOGIN":
                        out.println("ERR LOGINREQUIRED");
                        break;

                    case "ADD":
                        if (parti[1].equals(null)) {
                            out.println("ERR SYNTAX");
                        } else {
                            currentMessage.autore = autore;
                            currentMessage.testo = parti[1];
                            messageLog.add(currentMessage);
                            out.println("OK ADDED " + currentMessage.id);
                        }
                        break;
                    case "LIST":
                        for (int i = 0; i < messageLog.size(); i++) {
                            out.println("[" + messageLog.get(i).id + "] " + messageLog.get(i).autore + ": "
                                    + messageLog.get(i).testo);
                        }
                        out.println("END");
                        break;
                    case "DEL":
                        try {
                            int id = Integer.parseInt(parti[1]);

                            int target = findMessage(messageLog, id, autore);
                            if (target == -1) {
                                out.println("ERR NOT FOUND");
                            } else {
                                messageLog.remove(target);
                            }
                        } catch (NumberFormatException ex) {
                            out.println("ERR SYNTAX");
                        }
                        break;
                    case "QUIT":
                        out.println("BYE");
                        socket.close();
                        break;

                    default:
                        out.println("ERR UNKNOWNCMD");
                        break;
                }
            }
        } catch (
        Exception e) {
        }
    }

    private static int findMessage(List<Messaggio> messageLog, int id, String eliminatore) {
        for (int i = 0; i < messageLog.size(); i++) {
            if (messageLog.get(i).id == id && messageLog.get(i).autore == eliminatore) {
                return i;
            }
        }
        return -1;
    }
}
