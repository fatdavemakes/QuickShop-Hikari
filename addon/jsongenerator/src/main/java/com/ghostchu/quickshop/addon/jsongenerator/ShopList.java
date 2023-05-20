package com.ghostchu.quickshop.addon.jsongenerator;

import com.ghostchu.quickshop.addon.jsongenerator.ShopData;

import java.util.List;
import java.util.ArrayList;
public class ShopList {
    
    String type;
    ArrayList<ShopData> shops;
    

    
    public ShopList(String type) {
        this.type = type;
        shops = new ArrayList<>();  

    }
    
    public void addShop(ShopData shop) {
        shops.add(shop);
        
    }
}
