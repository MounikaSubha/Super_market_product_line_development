package com.supermarket.servers.authorization;

import java.io.IOException;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.UUID;

public class ClientHandler extends Thread {
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;


    // Constructor
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos) {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
    }

    @Override
    public void run() {
        String cardNumber;
        String pin;
        String type;
        while (true) {



            try {
                type = dis.readUTF();
                cardNumber = dis.readUTF();

                if(type.equals("debit")){
                    pin = dis.readUTF();
                    if(cardNumber.equals("1234123412341234") && pin.equals("1234")){
                        System.out.println("Card is valid...");
                        dos.writeUTF(UUID.randomUUID().toString());
                    }else{
                        System.out.println("Card is invalid...");
                        dos.writeUTF("false");
                    }
                }else{
                    if(cardNumber.equals("4321432143214321")){
                        System.out.println("Card is valid...");
                        dos.writeUTF(UUID.randomUUID().toString());
                    }else{
                        System.out.println("Card is invalid...");
                        dos.writeUTF("false");
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        try {
//            // closing resources
//            this.dis.close();
//            this.dos.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}

