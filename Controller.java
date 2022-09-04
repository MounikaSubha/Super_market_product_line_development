package com.supermarket;

import com.supermarket.models.BulkItem;
import com.supermarket.models.Item;
import com.supermarket.printers.CheckPrinter;
import com.supermarket.printers.ReceiptPrinter;
import com.supermarket.views.CashierView;
import com.supermarket.views.CustomerView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Scanner;

public class Controller implements Runnable {

    InetAddress ip = null;
    // establish the connection with server port 5056
    Socket inventoryServerSocket;
    DataInputStream dis;
    DataOutputStream dos;
    double totalPrice;
    double totalDiscount;
    double totalTax;
    DecimalFormat df = new DecimalFormat("###.##");


    public String scanBarCode() {
        return "";
    }

    public Cart createCart() {
        return new Cart();
    }

    Cart newCart = createCart();
    CustomerView customerView;
    CashierView cashierView;

    public void createNewSession() {
        newCart = createCart();
        setDisplays();
        totalPrice = 0.0;
        totalDiscount = 0.0;
        totalTax = 0.0;
    }

    public void setDisplays() {
        customerView = new CustomerView();
        cashierView = new CashierView();
        customerView.setDisplay();
        cashierView.setDisplay();
        customerView.printDisplay();
        cashierView.printDisplay();
        cashierView.showDisplay();
    }

    public void refreshDisplays() throws IOException, InterruptedException {
        customerView.clearScreen();
        customerView.printDisplay();
        cashierView.printDisplay();
        cashierView.showDisplay();
    }

    @Override
    public void run() {
        createCart();
        setDisplays();

        // getting localhost ip
        try {
            ip = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


        try {
            inventoryServerSocket = new Socket(ip, 5056);
            // obtaining input and out streams
            dis = new DataInputStream(inventoryServerSocket.getInputStream());
            dos = new DataOutputStream(inventoryServerSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            switch (scanner.nextInt()) {
                case 1:
                    addItemUsingBarcode();
                    break;
                case 2:
                    try {
                        addItemUsingScreen();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    System.out.println("You can use this button only when a BulkItem is added to cart");
                    cashierView.showDisplay();
                    break;
                case 4:
                    try {
                        if(!totalButton(newCart.getItemsInCart())){
                            return;
                        }
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 5:
                    System.out.println("Exiting..Bye");
                    return;
                default:
                    break;
            }
        }
    }


    public void addItemUsingBarcode() {

        // Fetch ID from Barcode Scanner
        // gets Item from DB based on ID
        // updates both screens
        System.out.println("Barcode reader not available now :( Please use touch screen.\n");
        cashierView.showDisplay();;

    }

    public void addItemUsingScreen() throws IOException, InterruptedException {

        Scanner scanner = new Scanner(System.in);
        String received = "Invalid input";
        while(true){
            cashierView.getItemPrompt();
            String code = scanner.next(); // Fetch ID from Touch screen

            dos.writeUTF(code);
            received = dis.readUTF();
            if (received.equals("Invalid input")) {
                System.out.println("Item with id is not found in inventory, please try again!\n");
            }else{
                break;
            }
        }

        String[] itemDetails = received.split(",");
        Item item;
        if (itemDetails[4].equals("Y")) {
            item = new BulkItem(itemDetails[0], itemDetails[1], Double.parseDouble(itemDetails[2]), Double.parseDouble(itemDetails[3]), 0.0);
        } else {
            item = new Item(itemDetails[0], itemDetails[1], Double.parseDouble(itemDetails[2]), Double.parseDouble(itemDetails[3]));
        }

        if (item instanceof BulkItem) {
            // prompt to put on scale
            cashierView.displayScalePrompt();
            if (scanner.nextInt() != 3) {
                refreshDisplays();
                return;
            }
            ((BulkItem) item).setWeight(4.5); //assuming we have some sort of api that gives weight from scale
        }


        newCart.addItem(item);
        //updates both screens
        customerView.addItemToDisplay(item);
        cashierView.addItemToDisplay(item);
    }

    public void printReceipt(StringBuilder sb) {
        Map<Item, Integer> itemQuantityMap = newCart.getItemsInCart();

        sb.append("ItemDesc      Qty     Weight    Price\n");


        for (Item i : itemQuantityMap.keySet()) {
            sb.append(i.getDescription() + "      " + itemQuantityMap.get(i) + "       " + (i instanceof BulkItem ? ((BulkItem) i).getWeight() : "N/A") + "   " + i.getPrice() * (1 - i.getDiscount() / 100) * itemQuantityMap.get(i) + "\n");
        }
        sb.append("Total price:" + df.format(totalPrice) + "\n");
        sb.append("Total discount:" + df.format(totalDiscount) + "\n");
        sb.append("Price incl. 2% tax :" + df.format((totalPrice + totalTax)) + "\n");
        ReceiptPrinter receiptPr = new ReceiptPrinter();
        receiptPr.printReceipt(sb.toString());

    }

    public boolean totalButton(Map<Item, Integer> itemQuantityMap) throws IOException, InterruptedException {
        // updates display


        StringBuilder sb = new StringBuilder();
        for (Item i : itemQuantityMap.keySet()) {

            totalPrice += i.getPrice() * itemQuantityMap.get(i);
            totalDiscount += itemQuantityMap.get(i) * (i.getPrice() - i.getPrice() * (1 - i.getDiscount() / 100));
        }
        totalTax = (0.02) * totalPrice;
        customerView.showTotal(totalPrice);
        cashierView.showTotal(totalPrice);
        //initiates payment flow
        cashierView.showPaymentOptions();
        PaymentGateway paymentGateway = new PaymentGateway();
        Scanner scanner = new Scanner(System.in);
        int numAttempts = 2;
        while (numAttempts > 0) {
            numAttempts--;
            if (numAttempts == 0) {
                System.out.println("This is your last attempt. Order will be cancelled on unsuccessful verification.\n");
                cashierView.showPaymentOptions();
                sb = new StringBuilder();
            }
            String cardNumber = "";
            String pin = "";
            Scanner scanner1 = new Scanner(System.in);
            String paymentResponse;
            sb.append("********ORDER RECEIPT************\n");
            switch (scanner.nextInt()) {
                case 1:
                    Double cash;
                    while(true){
                        cashierView.promptCashGiven();
                        cash = Double.parseDouble(scanner.next());
                        if(cash > (totalPrice+totalTax)){
                            break;
                        }else{
                            System.out.println("Cash cannot be less than total price, please enter again..");
                        }
                    }

                    Double change = cash-(totalPrice+totalTax);
                    customerView.showCashAndChange(cash, change);
                    cashierView.showCashAndChange(cash, change);
                    paymentGateway.payUsingCash();
                    sb.append("Cash given : "+df.format(cash)+"\n");
                    sb.append("Change tendered : "+df.format(change)+"\n");
                    printReceipt(sb);
                    numAttempts = 0;
                    System.out.println("Would you like to create a new order? Press Y or N");
                    if((scanner.next()).equals("Y")){
                        createNewSession();
                        return true;
                    }else {
                        System.out.println("Bye!!");
                        return false;
                    }
                case 2:
                    System.out.println("Please enter 16 digit card number.");
                    cardNumber = scanner1.next();
                    paymentResponse = paymentGateway.payUsingCredit(cardNumber);
                    if (!paymentResponse.equals("false")) {
                        System.out.println("Payment successful.");
                        sb.append("Card Number : " + cardNumber + "\n");
                        sb.append("Authorization Code : " + paymentResponse + "\n");
                        printReceipt(sb);
                        numAttempts = 0;
                        System.out.println("Would you like to create a new order? Press Y or N");
                        if((scanner.next()).equals("Y")){
                            createNewSession();
                            return true;
                        }else {
                            System.out.println("Bye!!");
                            return false;
                        }
                    } else {
                        System.out.println("Payment failed. Please try again..");

                    }
                    break;
                case 3:
                    System.out.println("Please enter 16 digit card number.");
                    cardNumber = scanner1.next();
                    System.out.println("Please enter 4 digit PIN.");
                    pin = scanner1.next();
                    paymentResponse = paymentGateway.payUsingDebit(cardNumber, pin);
                    if (!paymentResponse.equals("false")) {
                        System.out.println("Payment successful.");
                        sb.append("Card Number : " + cardNumber + "\n");
                        sb.append("Authorization Code : " + paymentResponse + "\n");
                        printReceipt(sb);
                        numAttempts = 0;
                        System.out.println("Would you like to create a new order? Enter Y to continue");
                        if((scanner.next()).equals("Y")){
                            createNewSession();
                            return true;
                        }else {
                            System.out.println("Bye!!");
                            return false;
                        }
                    } else {
                        System.out.println("Payment failed. Please try again..");
                    }
                    break;
                case 4:
                    System.out.println("The check has been verified...");
                    CheckPrinter cp = new CheckPrinter();
                    printReceipt(sb);
                    numAttempts = 0;
                    cp.printCheck();
                    System.out.println("Would you like to create a new order? Enter Y to continue");
                    if((scanner.next()).equals("Y")){
                        createNewSession();
                        return true;
                    }else {
                        System.out.println("Bye!!");
                        return false;
                    }
                default:
                    break;
            }
        }
        System.out.println("Order Cancelled..");
        System.out.println("Would you like to create a new order? Enter Y to continue");
        if((scanner.next()).equals("Y")){
            createNewSession();
            return true;
        }else {
            System.out.println("Bye!!");
            return false;
        }
    }


}
