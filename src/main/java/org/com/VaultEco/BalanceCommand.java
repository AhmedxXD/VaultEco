    package org.com.VaultEco;

    import net.dv8tion.jda.api.EmbedBuilder;
    import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
    import net.dv8tion.jda.api.hooks.ListenerAdapter;
    import org.bukkit.Bukkit;
    import org.bukkit.OfflinePlayer;

    import java.awt.Color;
    import java.text.NumberFormat;
    import java.util.Locale;
    import java.util.Objects;

    public class BalanceCommand extends ListenerAdapter {


        @Override
        public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
            event.deferReply(true).queue();
            VaultEconomy vaultEconomy = (VaultEconomy) Bukkit.getPluginManager().getPlugin("VaultEco");
            String username1;
            try {
                if (event.getName().equalsIgnoreCase("balance")) {
                    System.out.println(event.getOption("username"));
                    if ((event.getOption("username")) != null) {
                        username1 = Objects.requireNonNull(event.getOption("username")).getAsString();
                    } else {
                        username1 = "default";
                    }
                    if (username1.equals("default")) {
                        System.out.println(username1);
                        Linking linking = new Linking();
                        System.out.println(event.getOption("username"));
                        System.out.println(event.getOption(event.getUser().getName()));
                        OfflinePlayer user2 = linking.linkchecker(event.getUser().getId());
                        System.out.println(user2);
                        if (user2 != null) {
                            username1 = user2.getName();
                        } else {
                            event.getHook().editOriginal("<:Exc:1262813164584833077>**`Your Account is not Linked` Or `If You Want to check Balance Of Another Player Pls Provide Mc Name`**").queue();
                            return;
                        }
                    }

                    double balance = vaultEconomy.getPlayerBalance(username1);
                    String formattedBalance = NumberFormat.getNumberInstance(Locale.US).format(balance);// Use plugin instance
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setColor(Color.GREEN);
                    eb.setTitle("<a:c:1289666516513460245> Player Balance");
                    eb.setDescription("**Name:** `" + username1 + "`\n**Balance:** `" + formattedBalance + "`");
                    eb.setThumbnail("https://mc-heads.net/avatar/" + username1);
                    event.getHook().editOriginalEmbeds(eb.build()).queue();
                }
            }
            catch(Exception e){
              System.out.println(e);
            }
        }
    }
