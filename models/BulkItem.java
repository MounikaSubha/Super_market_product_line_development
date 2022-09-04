package com.supermarket.models;

public class BulkItem extends Item{

    private double weight;

    public BulkItem(String id, String description, double price, double discount, double weight) {
        super(id, description, price, discount);
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
