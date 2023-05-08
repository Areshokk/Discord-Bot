package areshok.events;


import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;


import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class OnJoinAndOnLeave extends ListenerAdapter {




    @Override
    public void onGuildMemberJoin(final GuildMemberJoinEvent event) {

//        if (event.getMember().isTimedOut()) {
//            // event.getGuild().getMember(user).removeTimeout().queue();
//            event.getMember().removeTimeout().queue();
//        }


        event.getGuild().addRoleToMember(UserSnowflake.fromId(event.getMember().getId()), Objects.requireNonNull(event.getGuild().getRoleById(1043657866730537040L))).queue();

        System.out.println(event.getMember());
       //final List<TextChannel> dontDoThis = event.getGuild().getTextChannelsByName("chat",true);
       final List<TextChannel> dontDoThis = event.getGuild().getTextChannelsByName("welcum",true);


       if (dontDoThis.isEmpty()) {
           return;
       }

       final TextChannel pleaseDontDoThisAtAll = dontDoThis.get(0);

       final String userGuildSpecificSettingInstead = String.format("Привіт, друже, я гадаю ти помилився дверима, гетероклуб двома поверхами вище. А якщо ні тоді\n<@%s> Загадка від Жака Фреско \nБуло 2 козла \nСкільки? ",
               event.getMember().getUser().getId());

       pleaseDontDoThisAtAll.sendMessage(userGuildSpecificSettingInstead).queue();

    }


    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        System.out.println(event.getMember());
        //final List<TextChannel> dontDoThis = event.getGuild().getTextChannelsByName("chat",true);
        final List<TextChannel> dontDoThis = event.getGuild().getTextChannelsByName("welcum", true);

        if (dontDoThis.isEmpty()) {
            return;
        }

        final TextChannel pleaseDontDoThisAtAll = dontDoThis.get(0);

        final String userGuildSpecificSettingInstead = String.format("<@%s> Не витримав морального натиску \nі лівнув з життя",
                Objects.requireNonNull(event.getUser().getId()));

        pleaseDontDoThisAtAll.sendMessage(userGuildSpecificSettingInstead).queue();
    }

}








//    private HashMap<String, String> userCaptcha = new HashMap<>();
//
//
//
//    @Override
//    public void onGuildMemberJoin(final GuildMemberJoinEvent event) {
//
//
//
//
//        Captcha captcha = new Captcha();
//
//        if (userCaptcha.containsKey(event.getUser().getId())) { // Checking to see if user is already in the database
//            userCaptcha.remove(event.getUser().getId());
//        }
//
//        event.getUser().openPrivateChannel().queue((channel) -> {
//            File captchaFile = captcha.generateCaptcha();
//
//            channel.sendFile(captchaFile).queue();
//            channel.sendMessage("**Answer the CAPTCHA to gain permissions**").queue();
//
//            userCaptcha.put(event.getUser().getId(), captchaFile.getName().substring(0, 5));
//        });
//
//        //final List<TextChannel> dontDoThis = event.getGuild().getTextChannelsByName("chat",true);
//        final List<TextChannel> dontDoThis = event.getGuild().getTextChannelsByName("висоцький-підар",true);
//
//
//
//
//
//        if (dontDoThis.isEmpty()) {
//            return;
//        }
//
//        final TextChannel pleaseDontDoThisAtAll = dontDoThis.get(0);
//
//        final String userGuildSpecificSettingInstead = String.format("<@%s> Загадка від Жака Фреско \nБуло 2 козла \nСкільки? ",
//                event.getMember().getUser().getId());
//
//        pleaseDontDoThisAtAll.sendMessage(userGuildSpecificSettingInstead).queue();
//
//    }
//
//    /**
//     * Listens for correct answers -> if correct, the bot will grant the user something... (Most likely a role)
//     *
//     * @param event Default event
//     */
//    @Override
//    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
//        if (userCaptcha.containsKey(event.getAuthor().getId())) { // Checking to see if the user has received a captcha
//            if (event.getMessage().getContentRaw().equals(userCaptcha.get(event.getAuthor().getId()))) { // The user answered the captcha correctly
//                event.getAuthor().openPrivateChannel().queue((channel -> {
//                    channel.sendMessage("Answered Correctly.").queue();
//                    userCaptcha.remove(event.getAuthor().getId());
//                }));
//            }
//        }
//    }