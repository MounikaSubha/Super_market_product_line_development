package com.supermarket.servers.inventory;

import java.io.IOException;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;

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
        String received;
        String toreturn;
        while (true) {
            try {

                // Ask user what he wants

                // receive the answer from client
                received = dis.readUTF();

//                if (received.equals("exit")) {
//                    System.out.println("Client " + this.s + " sends 1234...");
//                    System.out.println("Closing this connection.");
//                    this.s.close();
//                    System.out.println("Connection closed");
//                    break;
//                }

                // creating Date object
//                Date date = new Date();

                // write on output stream based on the
                // answer from the client
                switch (received) {

                    case "1234":
                        toreturn = "1234,Book,12.99,5.0,N";
                        System.out.println("Sending "+toreturn+" to client..");
                        dos.writeUTF(toreturn);
                        break;

                    case "2345":
                        toreturn = "2345,Pen,5.99,5.0,Y";
                        System.out.println("Sending "+toreturn+" to client..");
                        dos.writeUTF(toreturn);
                        break;

                    default:
                        dos.writeUTF("Invalid input");
                        break;
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
