package org.com.VaultEco;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
public class CoinFlip extends ListenerAdapter {
    Linking linking = new Linking();
    VaultEconomy vaultEconomy = (VaultEconomy) Bukkit.getPluginManager().getPlugin("VaultEco");
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equalsIgnoreCase("coinflip")) {

            String userid = event.getUser().getId();
            User opponentUser = Objects.requireNonNull(event.getOption("opponent")).getAsUser();
            if(opponentUser.isBot()){
                event.reply("You cannot bet against a bot!").queue();
                return;
            }
            String opponentid = opponentUser.getId();
            if(userid.equals(opponentid)){
                event.reply("You cannot bet Yourself").queue();
                return;
            }

            OfflinePlayer user1 = linking.linkchecker(userid);
            if(user1 == null) {
                event.reply("Your Account Is Not Linked!").queue();
                return;
            }
            OfflinePlayer oppon = linking.linkchecker(opponentid);
            if(oppon == null) {
                event.reply("Your Opponent Account Is Not Linked!").queue();
                return;
            };
            String bete = Objects.requireNonNull(event.getOption("bet")).getAsString();
            double bet1;
            try {
                bet1 = Double.parseDouble(bete);
                if (bet1 < 100) {
                    event.reply("Bet should be atleast 100!").queue();
                    return;
                } else if (bet1 == 100000) {
                    event.reply("Bet should be less than 100000!").queue();
                    return;
                }

            } catch (NumberFormatException e) {
                event.reply("Please enter a valid number for the bet!").queue();
                return;
            }
            if(Objects.equals(vaultEconomy.withdrawPlayer(user1, bet1), null)){
                event.reply("You dont have enough money to bet!").queue();
                return;
            }
            if(Objects.equals(vaultEconomy.withdrawPlayer(oppon, bet1), null)){
                event.reply("Your Opponent dont have enough money to bet!").queue();
                return;
            }
            event.deferReply(false).queue();
            EmbedBuilder confirmation = createEmbed("Confirmation!",null,"<@"+opponentid+">**, `Are you sure you want to accept the bet of` **<@"+userid+">",Color.ORANGE);
            event.getHook().sendMessageEmbeds(confirmation.build()).queue(sentMessage -> {
                long messageid = sentMessage.getIdLong();
                Button confirmbtn = Button.success("confirm:"+userid+":"+opponentid+":"+messageid+":"+bet1,"Confirm").withEmoji(Emoji.fromCustom("check",1262807820940742778L,false));
                Button cancelbtn = Button.danger("cancel:"+userid+":"+opponentid+":"+messageid+":"+bet1,"Cancel").withEmoji(Emoji.fromCustom("X",1262814280730804317L,false));
                sentMessage.editMessageEmbeds(confirmation.build()).setActionRow(confirmbtn, cancelbtn).queue();
            });

        }
    }
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String[] ids1 = Objects.requireNonNull(event.getButton().getId()).split(":");
        String action = ids1[0];
        String usid = ids1[1];
        String oid = ids1[2];
        String messageid = ids1[3];
        double bet32 = Double.parseDouble(ids1[4]);
        if(event.getMessage().getId().equals(messageid) && event.getUser().getId().equals(oid)){
           if(action.equals("confirm")){
               event.getMessage().delete().queue();
               Coinflipstarter(event,usid,oid,bet32);
           }
           if(action.equals("cancel")){
               event.getMessage().delete().queue();
               event.reply("You cancelled the bet!").queue();
               return;
           }
        }
        else{
            event.reply("Not For You").setEphemeral(true).queue();
        }

    }

    // Helper method to create an embed message
    public EmbedBuilder createEmbed(String title, String image_url,String description,Color color) {
        EmbedBuilder create = new EmbedBuilder();
        if(title != null) {
            create.setTitle(title);
        }
        if(image_url != null) {
            create.setImage(image_url);
        }
        if(color != null) {
            create.setColor(color);
        }
        if(description != null) {
            create.setDescription(description);
        }
        return create;
    }

    // Randomly choose the winner between USER 1 and USER 2
 private EmbedBuilder Winner(OfflinePlayer USER1, OfflinePlayer USER2, double bet5) {
      Random random = new Random();
      int num = random.nextInt(2);
      OfflinePlayer winner1 = num == 1 ? USER1 : USER2;
      vaultEconomy.depositPlayer(winner1,bet5+bet5);
      return createEmbed("<a:congo1:1287820962959593635>`"+ winner1.getName() +" Wins`", "https://mc-heads.net/avatar/"+winner1.getUniqueId(),null,Color.BLACK);
  }
  private void Coinflipstarter(@NotNull ButtonInteractionEvent event,String user64 ,String oppon64 ,double bet32){
        event.deferReply().queue();
      OfflinePlayer user32 = linking.linkchecker(user64);
      OfflinePlayer oppon32 = linking.linkchecker(oppon64);
      EmbedBuilder[] coin_animation = {
              createEmbed("<:think:1262858160407052439> `Who Will Win!`", "https://mc-heads.net/avatar/"+user32.getUniqueId(),null,Color.BLACK),
              createEmbed("<:think:1262858160407052439> `Who Will Win!`", "https://mc-heads.net/avatar/"+oppon32.getUniqueId(),null,Color.BLACK)
      };
      var executor = Executors.newSingleThreadScheduledExecutor();

      for (int i = 0; i < 7; i++) {

          final int index = i;
          executor.schedule(() -> event.getHook().editOriginalEmbeds(coin_animation[index % 2].build()).queue(), index, TimeUnit.SECONDS);
      }
      executor.schedule(() -> {
          EmbedBuilder winner = Winner(user32,oppon32,bet32);
          event.getHook().editOriginalEmbeds(winner.build()).queue();
          executor.shutdown();
      }, 7, TimeUnit.SECONDS);
      event.getMessage().getId();


  }

}
