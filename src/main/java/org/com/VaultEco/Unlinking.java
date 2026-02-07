package org.com.VaultEco;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class Unlinking implements CommandExecutor {
    private final VaultEconomy vaultEconomy = (VaultEconomy) Bukkit.getPluginManager().getPlugin("VaultEco");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(command.getName().equalsIgnoreCase("unlink")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("&cOnly players can use this command&c&l!");
            } else {
                Player player = (Player) sender;

                String uuid21 = player.getUniqueId().toString();
                boolean unlinked = false;
                for (String Key12 : Objects.requireNonNull(vaultEconomy.playerData.getConfigurationSection("DATA")).getKeys(false)) {
                    String value = Objects.requireNonNull(vaultEconomy.playerData.getConfigurationSection("DATA")).getString(Key12);
                    if (uuid21.equals(value)) {
                        vaultEconomy.playerData.set("DATA." + Key12, null);
                        try {
                            vaultEconomy.playerData.save(vaultEconomy.data);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2✔ &f| ʏᴏᴜʀ ᴀᴄᴄᴏᴜɴᴛ ɪꜱ ꜱᴜᴄᴄᴇꜱꜰᴜʟʟʏ &2ᴜɴʟɪɴᴋᴇᴅ"));
                        unlinked = true;
                        break;
                    }
                }
                if(!unlinked){
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4❌ &f| ʏᴏᴜʀ ᴀᴄᴄᴏᴜɴᴛ ɪꜱ &4ᴀʟʀᴇᴀᴅʏ ᴜɴʟɪɴᴋᴇᴅ"));
                }
            }
        }

        return true;
    }
}
