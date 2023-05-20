package com.ghostchu.quickshop.addon.jsongenerator;

public class ShopData {
    String owner;
    
    String item;
    int stackAmount; 
    String price;
    double X;
    double Y;
    double Z;
    String worldname;
    
    int remainingstock;
    int remainingspace;
    
    public ShopData(String owner,String item, int stackAmount, String price, double X, double Y, double Z, String worldname, int remainingstock, int remainingspace) {
        this.owner = owner;
        this.item = item;
        this.stackAmount = stackAmount;
        this.price = price;
        this.X = X;
        this.Y = Y;
        this.Z = Z;
        this.worldname = worldname;
        this.remainingstock = remainingstock;
        this.remainingspace = remainingspace;
    }
}
