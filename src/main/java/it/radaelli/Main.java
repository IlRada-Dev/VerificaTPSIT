package it.radaelli;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(3000)) {
            System.out.println("Server avviato sulla porta 3000");
            List<Messaggio> messageLog = Collections.synchronizedList(new ArrayList<>());
            while (true) {
                Socket client = serverSocket.accept();
                new Thread(new MyThread(client, messageLog)).start();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}