package org.com.VaultEco;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class baltop extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        VaultEconomy pl = (VaultEconomy) Bukkit.getPluginManager().getPlugin("VaultEco");
        List<String> baltop = pl.getBaltop(10);
        for(int i = 0; i < baltop.size(); i++){
        }
        event.reply(baltop.get(0));
    }
}
