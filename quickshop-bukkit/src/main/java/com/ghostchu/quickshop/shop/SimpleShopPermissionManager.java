package com.ghostchu.quickshop.shop;

import com.ghostchu.quickshop.QuickShop;
import com.ghostchu.quickshop.api.shop.ShopPermissionManager;
import com.ghostchu.quickshop.shop.permission.BuiltInShopPermission;
import com.ghostchu.quickshop.shop.permission.BuiltInShopPermissionGroup;
import com.ghostchu.quickshop.util.Util;
import com.ghostchu.quickshop.util.logger.Log;
import com.ghostchu.simplereloadlib.ReloadResult;
import com.ghostchu.simplereloadlib.Reloadable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.MapMaker;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class SimpleShopPermissionManager implements ShopPermissionManager, Reloadable {
    private final Map<String, Set<String>> permissionMapping = new MapMaker().makeMap();
    private final QuickShop plugin;

    public SimpleShopPermissionManager(@NotNull QuickShop plugin) {
        this.plugin = plugin;
        loadConfiguration();
        plugin.getReloadManager().register(this);
    }


    private void loadConfiguration() {
        Log.permission("Loading group configuration...");
        permissionMapping.clear();
        File file = new File(plugin.getDataFolder(), "group.yml");
        if (!file.exists()) {
            initDefaultConfiguration(file);
        }
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        if (!yamlConfiguration.isSet(QuickShop.getInstance().getName().toLowerCase(Locale.ROOT) + ".everyone") || !yamlConfiguration.isSet(QuickShop.getInstance().getName().toLowerCase(Locale.ROOT) + ".staff")) {
            plugin.getLogger().warning("Corrupted group configuration file, creating new one...");
            try {
                Files.move(file.toPath(), file.toPath().resolveSibling(file.getName() + ".corrupted." + UUID.randomUUID().toString().replace("-", "")));
                loadConfiguration();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        yamlConfiguration.getKeys(true).forEach(group -> {
            List<String> perms = yamlConfiguration.getStringList(group);
            this.permissionMapping.put(group, new HashSet<>(perms));
            Log.permission("Permission loaded for group " + group + ": " + Util.list2String(perms));
        });
    }

    private void initDefaultConfiguration(@NotNull File file) {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("version", 1);
        for (BuiltInShopPermissionGroup group : BuiltInShopPermissionGroup.values()) {
            yamlConfiguration.set(group.getNamespacedNode(), group.getPermissions().stream().map(BuiltInShopPermission::getNamespacedNode).collect(Collectors.toList()));
        }
        try {
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
            yamlConfiguration.save(file);
        } catch (Exception e) {
            Log.permission(Level.SEVERE, "Failed to create default group configuration file");
            plugin.getLogger().log(Level.SEVERE, "Failed to create default group configuration", e);
        }
    }

    public void registerPermission(@NotNull String group, @NotNull Plugin namespace, @NotNull String permission) {
        if (!permissionMapping.containsKey(group)) {
            throw new IllegalArgumentException("Group " + group + " does not exist.");
        }
        Log.permission("Register permission " + permission + " to group " + group);
        permissionMapping.get(group).add(namespace.getName().toLowerCase(Locale.ROOT) + "." + permission);
    }

    public void unregisterPermission(@NotNull String group, @NotNull Plugin namespace, @NotNull String permission) {
        if (!permissionMapping.containsKey(group)) {
            return;
        }
        Log.permission("Unregister permission " + permission + " from group " + group);
        permissionMapping.get(group).remove(namespace.getName().toLowerCase(Locale.ROOT) + "." + permission);
    }

    public boolean hasGroup(@NotNull String group) {
        return permissionMapping.containsKey(group);
    }

    public void registerGroup(@NotNull String group, @NotNull Collection<String> permissions) {
        if (permissionMapping.containsKey(group)) {
            throw new IllegalArgumentException("Group " + group + " already exists.");
        }
        Log.permission("Register group " + group);
        permissionMapping.put(group, new CopyOnWriteArraySet<>(permissions));
    }

    public void unregisterGroup(@NotNull String group) {
        if (!permissionMapping.containsKey(group)) {
            return;
        }
        Log.permission("Unregister group " + group);
        permissionMapping.remove(group);
    }

    public boolean hasPermission(@NotNull String group, @NotNull Plugin namespace, @NotNull String permission) {
        if (!permissionMapping.containsKey(group)) {
            return false;
        }
        boolean result = permissionMapping.get(group).contains(namespace.getName().toLowerCase(Locale.ROOT) + "." + permission);
        Log.permission("Check permission " + permission + " for group " + group + ": " + result);
        return result;
    }

    @NotNull
    public List<String> getGroups() {
        return ImmutableList.copyOf(this.permissionMapping.keySet());
    }

    public boolean hasPermission(@NotNull String group, @NotNull BuiltInShopPermission permission) {
        return hasPermission(group, QuickShop.getInstance(), permission.getRawNode());
    }

    @Override
    public ReloadResult reloadModule() throws Exception {
        this.loadConfiguration();
        return Reloadable.super.reloadModule();
    }
}