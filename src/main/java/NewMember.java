import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.thread.member.ThreadMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class NewMember extends ListenerAdapter {


    @Override
    public void onGuildMemberJoin(final GuildMemberJoinEvent event)
    {

        System.out.println(event.getMember());
       //final List<TextChannel> dontDoThis = event.getGuild().getTextChannelsByName("chat",true);
       final List<TextChannel> dontDoThis = event.getGuild().getTextChannelsByName("висоцький-підар",true);

       if (dontDoThis.isEmpty()) {
           return;
       }

       final TextChannel pleaseDontDoThisAtAll = dontDoThis.get(0);

       final String userGuildSpecificSettingInstead = String.format("<@%s> Загадка від Жака Фреско \nБуло 2 козла \nСкільки? ",
               event.getMember().getUser().getId());

       pleaseDontDoThisAtAll.sendMessage(userGuildSpecificSettingInstead).queue();

    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        System.out.println(event.getMember());
        //final List<TextChannel> dontDoThis = event.getGuild().getTextChannelsByName("chat",true);
        final List<TextChannel> dontDoThis = event.getGuild().getTextChannelsByName("висоцький-підар",true);

        if (dontDoThis.isEmpty()) {
            return;
        }

        final TextChannel pleaseDontDoThisAtAll = dontDoThis.get(0);

        final String userGuildSpecificSettingInstead = String.format("<@%s> Не витримав морального натиску \nі лівнув з життя",
                Objects.requireNonNull(event.getUser().getId()));

        pleaseDontDoThisAtAll.sendMessage(userGuildSpecificSettingInstead).queue();
    }



}
