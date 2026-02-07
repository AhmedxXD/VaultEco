package org.com.VaultEco;

import net.dv8tion.jda.api.JDA;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.YamlConfiguration;


public final class VaultEconomy extends JavaPlugin {
    FileConfiguration config = getConfig();
    private static Economy economy = null;
    public FileConfiguration playerData;
    public File data;
    @Override
    public void onEnable() {
        JDA jda;
        this.saveDefaultConfig();
        config = this.getConfig();
        if (!setupEconomy()) {
            getLogger().severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        Bukkit.getServer().getConsoleSender().sendMessage("Plugin enabled!");
        try {
            new Bot(); // Pass the plugin instance to Bot
            if(Bot.jda.getStatus() != JDA.Status.CONNECTED){
                Bukkit.getServer().getConsoleSender().sendMessage("Pls Configure Token!");
                Bukkit.getPluginManager().disablePlugin((Plugin) this);
            };
        } catch (Exception e) {
            Bukkit.getServer().getConsoleSender().sendMessage(String.valueOf(e));
        }
        dataconfig();
        Objects.requireNonNull(this.getCommand("link-account")).setExecutor(new Linking());
        Objects.requireNonNull(this.getCommand("unlink")).setExecutor(new Unlinking());
    }

    private void dataconfig(){
        saveDefaultConfig();
        data = new File(getDataFolder() + File.separator + "data.yml");
        if (!data.exists()) {
            this.saveResource("data.yml", false);
        }
        playerData = new YamlConfiguration();
        try {
            playerData.load(data);
            playerData.save(data);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        if (playerData.getConfigurationSection("DATA") == null) {
            getLogger().info("DATA section not found. Creating default DATA section...");
            playerData.createSection("DATA");
            try{
                playerData.save(data);
            }catch (Exception e){
                e.printStackTrace();
            } // Save the changes
        } else {
            getLogger().info("DATA section found.");
        }


    }
    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        return economy != null;
    }

    public static Economy getEconomy() {
        return economy;
    }
    public String getToken() {
        return config.getString("settings.token"); // Method to get the token
    }
    // Get the balance of a player by their username
    public Double getPlayerBalance(String username) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);
        if (economy != null) {
            return economy.getBalance(offlinePlayer);
        }
        return null; // Return null if player not found or economy is not available
    }
    public void depositPlayer(OfflinePlayer player, double amount) {
        if (economy != null) {
            economy.depositPlayer(player, amount);
        }
    }
    public Object withdrawPlayer(OfflinePlayer player, double amount) {
        if (economy != null) {
            if (economy.getBalance(player) > amount) {
                economy.withdrawPlayer(player, amount);
            } else {
                return null;
            }
        }
        return true;
    }
    public List<String> getBaltop(int limit) {

        // Retrieve all player balances
        List<String> baltop = Arrays.stream(Bukkit.getOfflinePlayers())
                .filter(player -> economy.hasAccount(player))
                .map(player -> new AbstractMap.SimpleEntry<>(player.getName(), economy.getBalance(player)))
                .sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue())) // Sort descending
                .limit(limit) // Limit to top 'n' players
                .map(entry -> entry.getKey() + ": " + entry.getValue()) // Format output
                .collect(Collectors.toList());

        return baltop;
    }

}
