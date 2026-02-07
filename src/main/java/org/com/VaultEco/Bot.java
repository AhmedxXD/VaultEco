package org.com.VaultEco;

import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class Bot {
    public static JDA jda;

    public Bot() throws Exception {
        StartBot();
    }

    private void StartBot() throws Exception {
        VaultEconomy vaultEconomy = (VaultEconomy) Bukkit.getPluginManager().getPlugin("VaultEco");
        if (vaultEconomy == null) {
            throw new IllegalStateException("VaultEconomy plugin not found");
        }

        String token = vaultEconomy.getToken();
        jda = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                .build();
        jda.awaitReady(); // Wait for JDA to be fully ready

        registerCommands();
        registerEventListeners();
    }

    private void registerCommands() {
        jda.upsertCommand("balance", "Show Your Balance")
                .addOption(OptionType.STRING, "username", "The username of the player", false)
                .queue();

        jda.upsertCommand("coinflip", "Try Your Luck 50/50% chance of winning")
                .addOption(OptionType.USER, "opponent", "The username of the player", true)
                .addOption(OptionType.STRING, "bet", "How much money you want to bet", true)
                .queue();

    }

    private void registerEventListeners() throws Exception {
        jda.addEventListener(new Linking());
        jda.addEventListener(new BalanceCommand());
        jda.addEventListener(new CoinFlip());
    }

    public User registerUser(String discordId) {
        try {
            return jda.retrieveUserById(Long.parseLong(discordId)).complete();
        } catch (NumberFormatException e) {
            System.err.println("Invalid Discord ID format: " + discordId);
            return null;
        } catch (Exception e) {
            System.err.println("Error retrieving user: " + e.getMessage());
            return null;
        }
    }

}
