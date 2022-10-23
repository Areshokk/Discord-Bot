import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class DiscordBot {

    public static void main(String[] args) throws LoginException {
        JDA bot = JDABuilder.createDefault("MTAwNjY4NTg0MTczMDU4ODc3Ng.GIaA29._BB6zRP_Jt_UE4LNyEQP5kjmi4zpCfsarbB88c")
                .setActivity(Activity.listening("маму твою"))
                .addEventListeners(new NewMember())
                .addEventListeners(new Hello())
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .build();

    }
}
