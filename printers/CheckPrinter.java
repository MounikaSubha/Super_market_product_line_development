package com.supermarket.printers;



import java.text.SimpleDateFormat;
import java.util.Date;

public class CheckPrinter {

    public void printCheck(){
        System.out.println("**************CHECK*****************\n");
        System.out.println("Date and Time : "+ new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Date()) + "\n");
        System.out.println("Store ID      : 4721387419 \n" );
        System.out.println("Cashier ID    : 12345 \n" );
        System.out.println("Order Number  : 56342534\n");
        System.out.println("************************************\n");
    }

}
