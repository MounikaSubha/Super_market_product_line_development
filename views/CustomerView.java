package com.supermarket.views;

import com.supermarket.models.BulkItem;
import com.supermarket.models.Item;

import java.io.IOException;
import java.text.DecimalFormat;

public class CustomerView {
    private StringBuilder displayString;
    private DecimalFormat df = new DecimalFormat("###.##");

    public void setDisplay(){
        clearScreen();
        displayString = new StringBuilder();
        displayString.append("***********CUSTOMER DISPLAY***********\n");
        displayString.append("Item Desc    Weight   Price   Discount\n");
    }

    public  void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public void printDisplay(){
        System.out.println(displayString.toString());
        System.out.println("**********************************\n");
    }

    public void addItemToDisplay(Item item) throws IOException, InterruptedException {
        displayString.append(item.getDescription()+"         "+(item instanceof BulkItem ? ((BulkItem) item).getWeight() : "N/A") +"      "+item.getPrice()+"    "+item.getDiscount()+"\n");
        clearScreen();
        printDisplay();
    }

    public void showCashAndChange(Double cash, Double change){
        displayString.append("Cash : "+df.format(cash)+"\n");
        displayString.append("Change : "+df.format(change));
        clearScreen();
        printDisplay();
    }

    public void showTotal(double totalPrice) throws IOException, InterruptedException {
        displayString.append("Total Price incl. 2% tax: "+df.format(1.02*totalPrice)+"\n");
        clearScreen();
        printDisplay();
    }

}
