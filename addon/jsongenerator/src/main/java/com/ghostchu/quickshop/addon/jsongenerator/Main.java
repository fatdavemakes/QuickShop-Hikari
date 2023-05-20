package com.ghostchu.quickshop.addon.jsongenerator;


import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

// Json generation
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.Location;
import org.bukkit.World;

import com.ghostchu.quickshop.QuickShop;
import com.ghostchu.quickshop.api.shop.ShopManager;
import com.ghostchu.quickshop.api.shop.Shop;
import com.ghostchu.quickshop.common.util.CommonUtil;

import com.ghostchu.quickshop.addon.jsongenerator.ShopData;
import com.ghostchu.quickshop.addon.jsongenerator.ShopInfo;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

//TODO make it listen for changes to shops and only regenerate if needed
//TODO add option to generate everytime a shop changes so data is "real time"

public final class Main extends JavaPlugin implements Listener {
    private QuickShop quickshop;
    private BukkitTask generator = null;
    private long updateinterval;
    private String savePath = null;
    private boolean prettyjson = false;;
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        quickshop = QuickShop.getInstance();
        
        
        updateinterval = getConfig().getLong("periodic-update-tick-interval");
        
        savePath = getConfig().getString("storage-location", System.getProperty("java.io.tmpdir") + "/") + 
                   getConfig().getString("storage-filename", "quickshop.json");
                   
        prettyjson = getConfig().getBoolean("pretty-json");
        
        
        generator = Bukkit.getScheduler().runTaskTimer(this, () -> this.generateJSON(), 1L, updateinterval);

    }
    
    @Override
    public void onDisable() {
        //disable the scheduled task
        generator.cancel();
    }

    public QuickShop getQuickShop() {
        return quickshop;
    }
    
    
    /**
    * Generate default configuration
    */  
     @Override
    public void saveDefaultConfig() {
        super.saveDefaultConfig();
        // perform configuration upgrade
        if (getConfig().getInt("config-version") != 2) {
            // default of regenerating ever 1 minute
            getConfig().set("periodic-update-tick-interval", 1200);
            getConfig().set("storage-location", getDataFolder().getAbsolutePath()  + "/"); // USe plugin directory unless told otherwise
            getConfig().set("storage-filename", "quickshop.json");
            getConfig().set("pretty-json", false);
            getConfig().set("config-version", 2);
            saveConfig();
        }
    }

    /**
    * Simple wrapper around logger for ease of use
    */    
    public void logInfo(String msg) {
	    getLogger().info(msg);
    }
    
    /**
    * Simple wrapper around logger- warning for ease of use
    */    
    public void logWarning(String msg) {
	    getLogger().warning(msg);
    }
    
    
    /**
    * Generates the JSON file by obtaining all shop info from the
    * quickshop plugin.
    * <p>
    * Method relies on data from config file for output locations
    */
    public void generateJSON() {
    
        // generate a temporary file for data
        // load data from quickshop into small objects to represent the JSON data
        // USe Gson to take the data and convert to JSON
        // Write data
        // Move temporary file into place overriding any existing verion
        
        File tempfile;
    	try {
            tempfile = File.createTempFile("quickshop_jsongenerator_", ".tmp");
            String absolutePath = tempfile.getAbsolutePath();
            String tempFilePath = absolutePath
                  .substring(0, absolutePath.lastIndexOf(File.separator));

            logInfo("Temp file path : " + tempFilePath);
            
    	    FileWriter jsonWriter = new FileWriter(tempfile);
	        
            ShopManager shopman = quickshop.getShopManager();
            ShopInfo shopinfo = new ShopInfo();
  
            for (Shop shop : shopman.getAllShops()) {
                ShopData shopd;
                
                int stackamount;
                String price;
                
                Location loc = shop.getLocation();
		        String owner = PlainTextComponentSerializer.plainText().serialize(shop.ownerName());
		        String itemname = CommonUtil.prettifyText(shop.getItem().getType().name());
		        
		        stackamount = shop.getShopStackingAmount();
		        price = String.valueOf(shop.getPrice());
		        if (quickshop.getEconomy() != null) {
		            price = quickshop.getEconomy().format(shop.getPrice(), loc.getWorld(), shop.getCurrency());
		        }
               
                 shopd = new ShopData(owner, itemname, shop.getShopStackingAmount(), price, loc.getX(), loc.getY(), loc.getZ(), loc.getWorld().getName(), shop.getRemainingStock(),shop.getRemainingSpace());
                 
 		         switch (shop.getShopType()) {
		            case BUYING:
		                shopinfo.addBuying(shopd);
                        break;
                    case SELLING:
		                shopinfo.addSelling(shopd);
                        break;
                    default:
                        logWarning("SHOP TYPE NOT KNOWN");
                        break;
		        };
                 
                 
                GsonBuilder gb = new GsonBuilder().serializeNulls();
                // Pretty json disabled by default in config but user configurable
                
                if (this.prettyjson == true) {
                    gb = gb.setPrettyPrinting();
                }
                Gson gson = gb.create();
     		    
     		    		    
		        String json = gson.toJson(shopinfo);
		        jsonWriter.write(json);
		        
		       
                 
           }

	       jsonWriter.close();	
	       
	       
	       // perform the move
	       File from = new File(tempfile.getAbsolutePath());
	       File to = new File(savePath);
	       Files.move(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
	        


		
        } catch (IOException e) {
	       logInfo("JSON generation failed");
            e.printStackTrace();
        }
        
    }
}





