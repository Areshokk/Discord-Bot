package areshok;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.util.Scanner;

import static areshok.Secret.botToken;

//MTAwNjY4NTg0MTczMDU4ODc3Ng.GIaA29._BB6zRP_Jt_UE4LNyEQP5kjmi4zpCfsarbB88c

public class DiscordBot {

    public static JDA jda;

    public static void main(String[] args) throws LoginException, InterruptedException {

        new DiscordBot();

    }

    private DiscordBot() throws LoginException, InterruptedException {

        Scanner token = new Scanner(System.in);
        jda = JDABuilder.createDefault(token.nextLine().trim())
                .enableCache(CacheFlag.VOICE_STATE)
                .setActivity(Activity.listening("/help"))
                .addEventListeners(new Listener())
                .build().awaitReady();
        for(Guild g : jda.getGuilds()) addSlashCommands(g);
        Scanner sc = new Scanner(System.in);
        while(true) {
            //String str = botToken;
            String str = sc.nextLine();
            if(str.toLowerCase().matches("^в[ідключення]*")) {
                jda.getPresence().setPresence(OnlineStatus.IDLE, Activity.playing("відключення..."));
                break;
            }
        }

        sc.close();
        jda.cancelRequests();
        jda.shutdown();
        System.exit(0);

    }

    private void addSlashCommands(Guild g) {

        if(g == null) return;

        g.upsertCommand("join", "Він приєднається до вашого каналу, якщо він не зайнятий.").queue();
        g.upsertCommand("quit", "Він покине ваш канал.").queue();
        g.upsertCommand("play","Він відтворюватиме задану музику")
                .addOption(OptionType.STRING,"title","Приймає посилання або заголовок", false).queue(); // req
        g.upsertCommand("pause","Пауза")
                .addOption(OptionType.INTEGER, "duration", "Протягом якого часу бот повинен припинити гру", false)
                .addOption(OptionType.STRING, "timeunit", "Тривалість у секундах, хвилинах, годинах, днях...?", false).queue();
        g.upsertCommand("queue", "Відобразиться список відтворення").queue();
        g.upsertCommand("skip", "Він пропустить музику, що відтворюється.")
                .addOption(OptionType.INTEGER, "amount", "Кількість треків для пропуску", false).queue();
        g.upsertCommand("volume","Він регулюватиме гучність")
                .addOption(OptionType.INTEGER, "volume", "Гучність в відсотках", false).queue(); // req
        g.upsertCommand("stop","Він перестане грати").queue();
        g.upsertCommand("jump","Він перелистне на задану кількість секунд.")
                .addOption(OptionType.INTEGER, "seconds", "Кількість секунд для перелистування вперед", false).queue(); /// req
        g.upsertCommand("shuffle","Перемішує список відтворення").queue();
        g.upsertCommand("loop","Повторює поточну запис або список відтворення")
                .addOption(OptionType.BOOLEAN,"track","Чи повинен він повторювати список відтворення або тільки трек, що відтворюється",false).queue();
        g.upsertCommand("bass", "Підсилює баси треку на задану величину")
                .addOption(OptionType.INTEGER, "amount", "Відсоток посилення низьких частот", false).queue(); //req
        g.upsertCommand("info","Показує інформацію про поточний трек").queue();
        g.upsertCommand("help","Він надішле вам інструкції про те, як ви маєте його використовувати.").queue();

    }
}



//    public static GatewayIntent[] INTENTS = {   GatewayIntent.DIRECT_MESSAGES,
//            GatewayIntent.GUILD_MEMBERS,
//            GatewayIntent.GUILD_MEMBERS,
//            GatewayIntent.GUILD_BANS,
//            GatewayIntent.GUILD_WEBHOOKS,
//            GatewayIntent.GUILD_INVITES,
//            GatewayIntent.GUILD_VOICE_STATES,
//            GatewayIntent.GUILD_PRESENCES,
//            GatewayIntent.GUILD_MESSAGES,
//            GatewayIntent.GUILD_MESSAGE_REACTIONS,
//            GatewayIntent.GUILD_MESSAGE_TYPING,
//            GatewayIntent.DIRECT_MESSAGES,
//            GatewayIntent.DIRECT_MESSAGE_REACTIONS,
//            GatewayIntent.DIRECT_MESSAGE_TYPING};
//
//
//    public static void main(String[] args) throws LoginException {
//
//
//        JDA bot = JDABuilder.create("MTAwNjY4NTg0MTczMDU4ODc3Ng.GIaA29._BB6zRP_Jt_UE4LNyEQP5kjmi4zpCfsarbB88c", Arrays.asList(INTENTS))
//                .enableCache(CacheFlag.VOICE_STATE)
//                .setActivity(Activity.listening("маму твою"))
//                .setStatus(OnlineStatus.ONLINE)
//                .addEventListeners(new OnJoinAndOnLeave())
//                .addEventListeners(new Hello())
//                .build();
//
//    }