import ca.tristan.jdacommands.JDACommands;
import commands.CmdPlay;
import events.OnJoinAndOnLeave;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.lang.reflect.Array;
import java.util.Arrays;

public class DiscordBot {

    public static GatewayIntent[] INTENTS = {   GatewayIntent.DIRECT_MESSAGES,
                                                GatewayIntent.GUILD_MEMBERS,
                                                GatewayIntent.GUILD_MEMBERS,
                                                GatewayIntent.GUILD_BANS,
                                                GatewayIntent.GUILD_WEBHOOKS,
                                                GatewayIntent.GUILD_INVITES,
                                                GatewayIntent.GUILD_VOICE_STATES,
                                                GatewayIntent.GUILD_PRESENCES,
                                                GatewayIntent.GUILD_MESSAGES,
                                                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                                                GatewayIntent.GUILD_MESSAGE_TYPING,
                                                GatewayIntent.DIRECT_MESSAGES,
                                                GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                                                GatewayIntent.DIRECT_MESSAGE_TYPING};


    public static void main(String[] args) throws LoginException {

        JDACommands jdaCommands = new JDACommands("!");
        jdaCommands.registerCommand(new CmdPlay());

        JDA bot = JDABuilder.create("MTAwNjY4NTg0MTczMDU4ODc3Ng.GIaA29._BB6zRP_Jt_UE4LNyEQP5kjmi4zpCfsarbB88c", Arrays.asList(INTENTS))
                .enableCache(CacheFlag.VOICE_STATE)
                .setActivity(Activity.listening("маму твою"))
                .setStatus(OnlineStatus.ONLINE)
                .addEventListeners(new OnJoinAndOnLeave())
                .addEventListeners(new Hello())
                .build();

    }
}
