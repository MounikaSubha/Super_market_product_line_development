package com.supermarket;

import com.supermarket.models.Item;

import java.util.HashMap;
import java.util.Map;

public class Cart {

    private Map<Item, Integer> itemQuantityMap;

    public Cart() {
        itemQuantityMap = new HashMap<>();
    }

    public void addItem(Item item) {
        itemQuantityMap.put(item, itemQuantityMap.getOrDefault(item, 0) + 1);
    }

    public Map<Item, Integer> getItemsInCart() {
        return itemQuantityMap;
    }

}
