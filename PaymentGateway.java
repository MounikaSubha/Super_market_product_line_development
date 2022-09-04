package com.supermarket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class PaymentGateway implements IPaymentGateway {

    private InetAddress ip = null;
    private Socket inventoryServerSocket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public PaymentGateway(){
        try {
            ip = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            inventoryServerSocket = new Socket(ip, 5058);
            // obtaining input and out streams
            dis = new DataInputStream(inventoryServerSocket.getInputStream());
            dos = new DataOutputStream(inventoryServerSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String payUsingCredit(String cardNumber) throws IOException {
        dos.writeUTF("credit");
        dos.writeUTF(cardNumber);
        String received = dis.readUTF();
        return received;
    }

    @Override
    public String payUsingDebit(String cardNumber, String pin) throws IOException {
        dos.writeUTF("debit");
        dos.writeUTF(cardNumber);
        dos.writeUTF(pin);
        String received = dis.readUTF();
        return received;
    }

    @Override
    public boolean payUsingCash() {
        System.out.println("Payment successful..");
        return true;
    }

    @Override
    public boolean payUsingCheck() {
        return false;
    }

}
