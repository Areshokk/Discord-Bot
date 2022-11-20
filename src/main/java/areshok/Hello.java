package areshok;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.lang.reflect.Member;
import java.util.Locale;
import java.util.Objects;

public class Hello extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        System.out.println("log - " + event.getMessage());

        if (!event.getAuthor().isBot()) {
            String messageSent = event.getMessage().getContentRaw();

            if ((messageSent.equals("2")) || (messageSent.equals("два"))){
                event.getGuild().addRoleToMember(UserSnowflake.fromId(Objects.requireNonNull(event.getMember()).getId()), Objects.requireNonNull(event.getGuild().getRoleById(1043658196184727614L))).queue();
                event.getGuild().removeRoleFromMember(event.getMember(), Objects.requireNonNull(event.getGuild().getRoleById(1043657866730537040L))).queue();
                event.getChannel().sendMessage("Хуй на").queue();
            }
        }

    }
}
