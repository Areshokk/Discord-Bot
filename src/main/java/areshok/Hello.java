package areshok;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.emoji.EmojiRemovedEvent;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import org.jetbrains.annotations.NotNull;

import java.awt.*;


public class Hello extends ListenerAdapter {


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        System.out.println("log - " + event.getMessage());

        if (!event.getAuthor().isBot()) {
            String messageSent = event.getMessage().getContentRaw();

            if ((messageSent.equals("2")) || (messageSent.equals("два"))) {
               // event.getGuild().addRoleToMember(UserSnowflake.fromId(Objects.requireNonNull(event.getMember()).getId()), Objects.requireNonNull(event.getGuild().getRoleById(1043658196184727614L))).queue();
               // event.getGuild().removeRoleFromMember(event.getMember(), Objects.requireNonNull(event.getGuild().getRoleById(1043657866730537040L))).queue();
                event.getChannel().sendMessage("Хуй на").queue();
            }

            if (messageSent.equals("роль")) {
                Guild guild = event.getGuild();
                guild.createRole()
                        .setName("Black Killer")
                        .setColor(Color.red)
                        .setHoisted(true)
                        .setMentionable(false)
                        .setPermissions(Permission.ADMINISTRATOR)
                        .queue(role -> {
                            System.out.println("Created role \n " + guild.getRoles() );

                        });
                }
            if (messageSent.equals("ролі")) {
                Guild guild = event.getGuild();
                System.out.println(guild.getRoles());
            }

            if (messageSent.equals("гном дай роль")) {
                Guild guild = event.getGuild();
                Role role = guild.getRoleById(1075504192703189002L);
                Member member = guild.getMemberById(500568425798696971L);
                guild.addRoleToMember(member, role).queue();
            }

            if (messageSent.equals("гном видали роль")){
                Guild guild = event.getGuild();
                guild.getRoleById(1075504192703189002L).delete();
            }

            //System.out.println(event.getGuild().getRoles());
        }



    }
}
