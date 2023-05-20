package com.ghostchu.quickshop.addon.jsongenerator;

import com.ghostchu.quickshop.addon.jsongenerator.ShopList;

import java.util.List;
// A wrapper class that holds generation time, top level info and then the buying/selling objects(which in turn hold an array of info)
// Only really here to make json generation easier
public class ShopInfo {
    
    long generationtime;
    ShopList buying;
    ShopList selling;
    

    
    public ShopInfo() {
        this.generationtime = System.currentTimeMillis() / 1000L;
        this.buying = new ShopList("BUYING");
        this.selling = new ShopList("SELLING");

    }
    
    public void addSelling(ShopData shop) {
        selling.addShop(shop);
    }
    
    public void addBuying(ShopData shop) {
        buying.addShop(shop);
    }
}
