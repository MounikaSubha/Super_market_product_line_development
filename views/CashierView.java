package com.supermarket.views;

import com.supermarket.models.BulkItem;
import com.supermarket.models.Item;

import java.text.DecimalFormat;

public class CashierView {
    private StringBuilder displayString;
    private DecimalFormat df = new DecimalFormat("###.##");

    public void setDisplay(){
        displayString = new StringBuilder();
        displayString.append("***********CASHIER DISPLAY***********\n");
        displayString.append("Item Desc    Weight   Price   Discount\n");

    }


    public void printDisplay(){
        System.out.println(displayString.toString());
        System.out.println("**********************************\n");
    }

    public void showDisplay(){
        System.out.println("Please select from below options.");
        System.out.println("1.Add item using barcode scanner.");
        System.out.println("2.Add item by entering item ID.");
        System.out.println("3.SCALE");
        System.out.println("4.TOTAL");
        System.out.println("5.EXIT");
    }


    public void getItemPrompt(){
        System.out.println("Please enter the item id.");
    }


    public void promptCashGiven(){
        System.out.println("Please enter the cash given:");
    }

    public void showCashAndChange(Double cash, Double change){
        displayString.append("Cash : "+df.format(cash)+"\n");
        displayString.append("Change : "+df.format(change));
        printDisplay();
    }

    public void showPaymentOptions(){
        System.out.println("Please select from below payment options.");
        System.out.println("1.Pay using Cash.");
        System.out.println("2.Pay using Credit card.");
        System.out.println("3.Pay using Debit card");
        System.out.println("4.Pay using cheque");
    }

    public void displayScalePrompt(){
        System.out.println("Please place the item on the scale and Enter 3 for SCALE button.");
    }


    public void addItemToDisplay(Item item) {
        displayString.append(item.getDescription()+"         "+(item instanceof BulkItem ? ((BulkItem) item).getWeight() : "N/A") +"      "+item.getPrice()+"    "+item.getDiscount()+"\n");
        printDisplay();
        showDisplay();
    }


    public void showTotal(double totalPrice){
        displayString.append("Total Price incl. 2% tax: "+df.format(1.02*totalPrice)+"\n");
        printDisplay();
    }

}
