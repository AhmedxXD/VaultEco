package org.com.VaultEco;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Linking extends ListenerAdapter implements CommandExecutor {
    public static Map<String, String> codes = new ConcurrentHashMap<>();
    public static Map<String, String> VerifiedUser = new ConcurrentHashMap<>();
    private static final String ALPHANUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int OTP_LENGTH = 5;
      VaultEconomy vaultEconomy = (VaultEconomy) Bukkit.getPluginManager().getPlugin("VaultEco");
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        User user;
        if (command.getName().equalsIgnoreCase("link-account")) {
            if (!(sender instanceof Player)){
                sender.sendMessage("&cOnly players can use this command&c&l!");
            }
            else {
                Player player = (Player) sender;
                String uuid = player.getUniqueId().toString();

                if(vaultEconomy.playerData.getConfigurationSection("DATA") == null) {
                    vaultEconomy.playerData.createSection("DATA");
                }
                if (Objects.requireNonNull(vaultEconomy.playerData.getConfigurationSection("DATA")).getValues(false).containsValue(uuid)) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&4❌ &f| ʏᴏᴜʀ ᴀᴄᴄᴏᴜɴᴛ ɪꜱ &4ᴀʟʀᴇᴀᴅʏ ʟɪɴᴋᴇᴅ"));

                }
                else {

                    try{
                        Bot bot = new Bot();
                        String otp = new SecureRandom().ints(OTP_LENGTH, 0, ALPHANUMERIC_STRING.length())
                                .mapToObj(ALPHANUMERIC_STRING::charAt)
                                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                                .toString();
                        codes.put(otp,uuid);


                        player.sendMessage(codes.get(otp)+ ","+ codes.get(uuid));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&e❗ &f| ᴄᴏᴅᴇ ᴛᴏ ʟɪɴᴋ ᴅɪꜱᴄᴏʀᴅ ɪꜱ: &e" + otp));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&f[ &eꜱᴇɴᴅ ᴛʜɪꜱ ᴄᴏᴅᴇ ᴛᴏ "+ "\"" + bot.jda.getSelfUser().getName().toUpperCase() + "\"" +" &f]"));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&f[ &4ᴅᴏɴᴛ ꜱʜᴀʀᴇ ɪᴛ ᴡɪᴛʜ ᴀɴʏᴏɴᴇ &f]"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

            }


        }
        return true;
    }
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        System.out.println(codes);
        System.out.println(codes.keySet());
        System.out.println(event.getMessage().getContentRaw());
        if(event.getChannelType() == ChannelType.PRIVATE) {
            for (String key : codes.keySet()) {
                System.out.println(key);
                if (event.getMessage().getContentRaw().trim().equalsIgnoreCase(key)) {
                    String value = codes.get(key);
                    codes.remove(key);
                    String id = event.getAuthor().getId();
                    vaultEconomy.playerData.set("DATA." + id, value);
                    try {
                        vaultEconomy.playerData.save(vaultEconomy.data);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    UUID uuid = UUID.fromString(value);
                    event.getChannel().sendMessage("<:Check2:1262807820940742778>**`Account Succesfully Connected With Mc Acoount`  [`" + Bukkit.getOfflinePlayer(uuid).getName() + "`]**").queue();
                    Player Temp = (Player) Bukkit.getOfflinePlayer(uuid);
                    if (Temp != null) {
                        Temp.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2✔ &f| ʏᴏᴜʀ ᴀᴄᴄᴏᴜɴᴛ ꜱᴜᴄᴄᴇꜱꜰᴜʟʟʏ ʟɪɴᴋᴇᴅ ᴛᴏ &2ᴅɪꜱᴄᴏʀᴅ ᴀᴄᴄᴏᴜɴᴛ \n&f[ " + event.getAuthor().getName() + " ]"));
                    }
                    break;

                }

            }
        }
    }
    public OfflinePlayer linkchecker(String id1) {
        FileConfiguration verifieddata = vaultEconomy.playerData;
        if (verifieddata.contains("DATA." + id1)) {
            String uuid2 = Objects.requireNonNull(verifieddata.getString("DATA." + id1));
                UUID uuidverified = UUID.fromString(uuid2);
                return Bukkit.getOfflinePlayer(uuidverified);
        }
        return null;
    }
}
