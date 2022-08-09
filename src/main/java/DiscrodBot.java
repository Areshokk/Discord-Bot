import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.security.auth.login.LoginException;

public class DiscrodBot {

    public static void main(String[] args) throws LoginException {
        JDA bot = JDABuilder.createDefault("MTAwNjY4NTg0MTczMDU4ODc3Ng.GthR6U.4Hp0F5WjqWuby2MRL8PCCm-VClBd8TAQNqSfb0")
                .setActivity(Activity.playing("Jebanii GenshiT"))
                .build();
        bot.addEventListener(new Hello());
    }
}
