package areshok;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Listener extends ListenerAdapter {

    AudioChannel activeVChannel;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent ev) {

        Member m = ev.getMember();
        Guild g = ev.getGuild();

        if(g == null || m == null) {
            sendMessage(ev, "Не пиши мені!  падло", "Використовуйте ці команди на серверах. Тільки ви можете бачити мої відповіді.", Color.RED);
            return;
        }

        MusicManager musicManager = PlayerManager.getINSTANCE().getMusicManager(g);

        HashMap<String, String> args = new HashMap<>();
        for(OptionMapping opt : ev.getOptions()) args.put(opt.getName(), opt.getAsString());

        String name = ev.getName().toLowerCase();

        switch (name) {
            case "join" -> {

                if(m.getVoiceState() == null || m.getVoiceState().getChannel() == null) {
                    sendMessage(ev, "Де?", "Зайди в канал 🙃", Color.RED);
                    return;
                }

                if (activeVChannel != null && !m.getVoiceState().getChannel().equals(activeVChannel) && activeVChannel.getMembers().size() > 1) {
                    sendMessage(ev, "Вибачаюсь", "Я граю не для тебе! 💔", Color.RED);
                    return;
                }

                sendMessage(ev, "ку мяу", "шо хочеш? 😃", Color.GREEN);
                g.getAudioManager().openAudioConnection(activeVChannel = m.getVoiceState().getChannel());
                DiscordBot.jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);

            }
            case "quit" -> quit(ev, g, m, musicManager);
            case "help" -> {
                Member mem = g.getMemberById(500568425798696971L);
                String var;
                if(mem == null) var = User.fromId(500568425798696971L).getAsMention();
                else var = mem.getAsMention();
                ev.replyEmbeds(new EmbedBuilder()
                                .setColor(Color.MAGENTA)
                                .setTitle("Інфа")
                                .addField("/join", "Приєднається до вашого каналу", false)
                                .addField("/quit", "Покине ваш канал", false)
                                .addField("/play [Назва/Лінка]", "Відтворення заданого треку(ів)", false)
                                .addField("/pause {Тривалість}", "Робить паузу", false)
                                .addField("/queue", "Показує список відтворення", false)
                                .addField("/skip", "Пропускає поточний трек", false)
                                .addField("/volume [Відсоток]", "Регулює гучність", false)
                                .addField("/jump [Секунд]", "Пропускає кількість секунд поточного треку", false)
                                .addField("/shuffle", "Перемішує список відтворення", false)
                                .addField("/loop {трек}", "Повторює поточний трек або список відтворення", false)
                                .addField("/stop", "Закриє рот", false)
                                .addField("/info", "Показує детальну інформацію про поточний трек", false)
                                .addField("/bass [Відсоток]", "Підсилює бас", false)
                                .addField("/help", "Показує цей список", false)
                                .setDescription("Якщо вас щось не влаштовує або у вас є пропозиції щодо покращення, будь ласка, зв'яжіться зі мною за адресою " + var).build())
                        .addActionRows(ActionRow.of(Button.success("support", "Допомога"))).setEphemeral(true).queue();
            }
            case "play" -> {

                if(args.containsKey("title")) play(ev, g, m, args.get("title"));
                else {
                    TextInput input = TextInput.create("title", "Назва або посилання", TextInputStyle.SHORT)
                            .setMinLength(1)
                            .setRequired(true)
                            .setPlaceholder("напр. https://www.youtube.com/watch?v=dQw4w9WgXcQ")
                            .build();

                    Modal modal = Modal.create("title-ask", "Вкажіть трек[и]:")
                            .addActionRows(ActionRow.of(input)).build();

                    ev.replyModal(modal).queue();
                }

            }
            case "stop" -> stop(ev, musicManager);
            case "pause" -> {

                if(!args.containsKey("duration")) {

                    TextInput when = TextInput.create("when", "Коли", TextInputStyle.SHORT)
                            .setRequired(false)
                            .setPlaceholder("наприклад, 10").build();

                    TextInput unit = TextInput.create("unit", "Одиниця часу", TextInputStyle.SHORT)
                            .setPlaceholder("наприклад, (s,m,h,d)")
                            .setRequired(false)
                            .build();

                    boolean paused = musicManager.player.isPaused();

                    Modal modal = Modal.create("pause", (paused ? "Продовжити" : "Пауза") + " Контроль").addActionRows(ActionRow.of(when), ActionRow.of(unit)).build();
                    ev.replyModal(modal).queue();

                } else pause(ev, musicManager, args.get("duration"), args.get("timeunit"));

            }
            case "shuffle" -> shuffle(ev, musicManager);
            case "loop" -> loop(ev, musicManager, args.get("track"));
            case "volume" -> {

                if(!args.containsKey("volume")) {
                    TextInput input = TextInput.create("vol", "Гучність", TextInputStyle.SHORT)
                            .setPlaceholder("наприклад, 50")
                            .setRequired(true)
                            .setMinLength(1)
                            .setMaxLength(3)
                            .build();

                    Modal modal = Modal.create("volume", "Регулювання гучності")
                            .addActionRows(ActionRow.of(input)).build();
                    ev.replyModal(modal).queue();
                } else volume(ev, musicManager, args.get("volume"));

            }
            case "skip" -> {
                if(args.get("amount") != null) skip(ev, musicManager, args.get("amount"));
                else {
                    TextInput input = TextInput.create("skip-num", "Кількість", TextInputStyle.SHORT)
                            .setRequired(true)
                            .setValue("1")
                            .setPlaceholder("наприклад, 1")
                            .setMinLength(1).build();
//                    Modal modal = Modal.create("skip", "Skip Control")
                    Modal modal = Modal.create("skip", "Скіпнути")
                            .addActionRows(ActionRow.of(input)).build();
                    ev.replyModal(modal).queue();
                }
            }
            case "jump" -> {
                if(!args.containsKey("seconds")) {
                    TextInput input = TextInput.create("jump-num", "Кількість", TextInputStyle.SHORT)
                            .setPlaceholder("в секундах")
                            .setRequired(true)
                            .build();
                    Modal modal = Modal.create("jump", "Скіп")
//                    Modal modal = Modal.create("jump", "Jump Control")
                            .addActionRows(ActionRow.of(input)).build();
                    ev.replyModal(modal).queue();
                } else jump(ev, musicManager, args.get("seconds"));
            }
            case "queue" -> queue(ev, musicManager);
            case "info" -> info(ev, musicManager);
            case "bass" -> {
                if(!args.containsKey("amount")) {
                    TextInput input = TextInput.create("bass-num", "Басс буст", TextInputStyle.SHORT)
                            .setRequired(true)
                            .setPlaceholder("у відсотках (за замовчуванням 0)")
                            .build();

                    Modal modal = Modal.create("bass", "Басс")
                            .addActionRows(ActionRow.of(input)).build();
                    ev.replyModal(modal).queue();
                } else bass(ev, musicManager, args.get("amount"));
            }
            default -> sendMessage(ev, "Ой!", "Я не знаю цю команду, хоча повинен ❓", Color.RED);
        }

    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent ev) {

        Member m = ev.getMember();
        Guild g;
        if((g = ev.getGuild()) == null) {
            sendMessage(ev, "П о м и л к а", "Не зміг знайти сервер", Color.RED);
            return;
        }

        MusicManager musicManager = PlayerManager.getINSTANCE().getMusicManager(g);

        switch(ev.getModalId()) {
            case "support" -> {
                String msg = ev.getValue("sup-msg").getAsString();
                User user = DiscordBot.jda.getUserById(500568425798696971L);
                assert user != null;
                PrivateChannel channel = user.openPrivateChannel().complete();
                channel.sendMessageEmbeds(new EmbedBuilder()
                        .setColor(Color.MAGENTA)
                        .setTitle("Підтримка " + ev.getGuild().getName())
                        .setDescription(ev.getUser().getAsTag() + " потребує допомогу!")
                        .addField("Надіслано наступний текст:", msg, false).build()).queue();
                sendMessage(ev, "Грац!", "Ваше повідомлення надіслано та наразі розглядається", Color.GREEN);
            }
            case "title-ask" -> play(ev, g, m, ev.getValue("title").getAsString());
            case "pause" -> pause(ev, musicManager, ev.getValue("when") == null ? null : ev.getValue("when").getAsString(),
                    ev.getValue("unit") == null ? null : ev.getValue("unit").getAsString());
            case "volume" -> volume(ev, musicManager, ev.getValue("vol").getAsString());
            case "skip" -> skip(ev, musicManager, ev.getValue("skip-num").getAsString());
            case "jump" -> jump(ev, musicManager, ev.getValue("jump-num").getAsString());
            case "bass" -> bass(ev, musicManager, ev.getValue("bass-num").getAsString());
        }

    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent ev) {

        if(Card.getMsg() == null) return;
        if(ev.getMessageIdLong() != Card.getMsg().getIdLong()) return;
        if(ev.getGuild().getSelfMember().equals(ev.getMember())) return;

        Guild g = ev.getGuild();
        Member m = ev.getMember();
        User u = ev.getUser();

        if(u == null) return;

        MusicManager musicManager = PlayerManager.getINSTANCE().getMusicManager(g);

        if(!Card.isPaused()) {
            if (ev.getEmoji().equals(Card.pause)) pause(null, musicManager, null, null);
            else if(ev.getEmoji().equals(Card.loop)) loop(null, musicManager, null);
            else if(ev.getEmoji().equals(Card.loop1)) loop(null, musicManager, Boolean.toString(!musicManager.handler.isLooped().getValue()));
            else if(ev.getEmoji().equals(Card.jump)) jump(null, musicManager, "10");
            else if(ev.getEmoji().equals(Card.shuffle)) shuffle(null, musicManager);
            else if(ev.getEmoji().equals(Card.skip)) skip(null, musicManager, "1");
            else if(ev.getEmoji().equals(Card.bass1)) bass(null, musicManager, Integer.toString(musicManager.bassboost - 10));
            else if(ev.getEmoji().equals(Card.bass2)) bass(null, musicManager, Integer.toString(musicManager.bassboost + 10));
            else if(ev.getEmoji().equals(Card.volume1)) volume(null, musicManager, Integer.toString(musicManager.player.getVolume() - 10));
            else if(ev.getEmoji().equals(Card.volume2)) volume(null, musicManager, Integer.toString(musicManager.player.getVolume() + 10));
        }
        if(ev.getEmoji().equals(Card.quit)) quit(null, g, m, musicManager);
        else if(ev.getEmoji().equals(Card.stop)) stop(null, musicManager);
        else if(ev.getEmoji().equals(Card.resume)) pause(null, musicManager, null, null);
        else if(ev.getEmoji().equals(Card.queue)) queue(null, musicManager);

        if(Card.getMsg() != null) Card.getMsg().removeReaction(ev.getEmoji(), u).queue();

    }

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent ev) {

        if(Card.getMsg() == null) return;
        if(ev.getMessageIdLong() != Card.getMsg().getIdLong()) return;
        Card.reset();

    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent ev) {

        String id;
        if((id = ev.getButton().getId()) == null) return;
        if ("support".equals(id)) {
            TextInput input = TextInput.create("sup-msg", "Message", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("якісь трабли?")
                    .setRequired(true)
                    .setMinLength(10).build();
            Modal modal = Modal.create("support", "Support")
                    .addActionRows(ActionRow.of(input)).build();
            ev.replyModal(modal).queue();
        }

    }

    private void sendMessage(IReplyCallback ev, String title, String description, Color color) {

        if(ev == null) return;
        ev.replyEmbeds(new EmbedBuilder()
                .setTitle(title)
                .setColor(color)
                .setDescription(description).build()).setEphemeral(true).queue();

    }

    private void quit(GenericCommandInteractionEvent ev, Guild g, Member m, MusicManager musicManager) {
        if (activeVChannel == null) {
            sendMessage(ev, "Ха ха ха", "Мене ніде нема! 👊", Color.RED);
            return;
        }
        sendMessage(ev, "бб", "нюхай бебру! 👋", Color.MAGENTA);
        activeVChannel = null;
        musicManager.handler.stop();
        g.getAudioManager().closeAudioConnection();
        DiscordBot.jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.listening("/help"));

    }

    private void play(IReplyCallback ev, Guild g, Member m, String arg) {

        if(activeVChannel == null) {
            if(m.getVoiceState() == null || m.getVoiceState().getChannel() == null) {
                sendMessage(ev, "Де?", "Зайди в канал падлюка 🙃", Color.RED);
                return;
            } else g.getAudioManager().openAudioConnection(activeVChannel = m.getVoiceState().getChannel());
        }

        PlayerManager.getINSTANCE().load((TextChannel) ev.getMessageChannel(), ev, arg);
    }

    private void stop(IReplyCallback ev, MusicManager musicManager) {

        musicManager.handler.stop();
        sendMessage(ev, "Мовчу", "нашо ти це зробив?", Color.GREEN);
        DiscordBot.jda.getPresence().setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.listening("/help"));

    }

    private void pause(IReplyCallback ev, MusicManager musicManager, String dur, String timeu) {

        int duration;

        if(dur != null) {
            try {
                duration = Integer.parseInt(dur);
                if (duration <= 0) sendMessage(ev, "Недійсна тривалість!", "Виберіть вище нуля!", Color.red);
            } catch (NumberFormatException ex) {
                sendMessage(ev, "Недійсна тривалість!", "`" + dur + "` не є дійсним цілим числом!", Color.RED);
                return;
            }

            TimeUnit time;
            String timeunit;
            if ((timeunit = timeu) != null) {
                if (timeunit.toLowerCase().matches("^se?[ckx]?s?")) time = TimeUnit.SECONDS;
                else if (timeunit.toLowerCase().matches("^m(ins?)?")) time = TimeUnit.MINUTES;
                else if (timeunit.toLowerCase().matches("^(h(ours?)?)|st")) time = TimeUnit.HOURS;
                else if (timeunit.toLowerCase().matches("^(d(ays?)?)|t")) time = TimeUnit.DAYS;
                else {
                    sendMessage(ev, "Число не підтримується", "Виберіть секунди, хвилини, години та дні! ❌", Color.RED);
                    return;
                }
            } else time = TimeUnit.SECONDS;

            boolean paused = musicManager.handler.pause(duration, time);
            if (paused) {
                sendMessage(ev, "Пауза", String.join(" ", "Бот зупинитися через ", String.valueOf(duration), time.name(), " ⏰"), Color.GREEN);
            } else {
                sendMessage(ev, "Продовження", String.join(" ", "Бот продовжить гру через ", String.valueOf(duration), time.name(), " ⏰"), Color.GREEN);
            }
        } else {
            boolean paused = musicManager.handler.pause();
            if (paused) {
                sendMessage(ev, "Пауза","Бот застопився", Color.GREEN);
            } else {
                sendMessage(ev, "Відновлення", "Бот продовжує", Color.GREEN);
            }
        }
    }

    private void shuffle(IReplyCallback ev, MusicManager musicManager) {

        musicManager.handler.shuffle();
        sendMessage(ev, "Перетасування", "Список успішно перемішано", Color.GREEN);

    }

    private void loop(IReplyCallback ev, MusicManager musicManager, String track) {

        Map.Entry<Boolean, Boolean> looped = musicManager.handler.isLooped();
        if (track == null) {
            musicManager.handler.setLooped(!looped.getKey(), looped.getValue());
        } else {
            musicManager.handler.setLooped(true, Boolean.parseBoolean(track.toLowerCase()));
        }
        looped = musicManager.handler.isLooped();
        if (looped.getKey()) {
            if (looped.getValue()) {
                sendMessage(ev, "Зациклення одного треку",
                        "Я збираюся зациклити `" + musicManager.player.getPlayingTrack().getInfo().title + "`!", Color.GREEN);
            } else {
                sendMessage(ev, "Зациклений плейлист", "Я збираюся зациклити цей список відтворення після завершення", Color.GREEN);
            }
        } else {
            sendMessage(ev, "Не зациклюється", "Я не буду повторювати треки, які вже грали", Color.GREEN);
        }

    }

    private void volume(IReplyCallback ev, MusicManager musicManager, String volume) {

        try {

            int percent = musicManager.player.getVolume();
            int set = Integer.parseInt(volume);
            if (set <= 0) {
                sendMessage(ev, "Шо за падло закрило мені рот", "Якщо ви не хочете слухати певну частину треку, використовуйте `/jump [секунди]`", Color.RED);
                return;
            } else if (set > 1000) {
                sendMessage(ev, "Це надто голосно", "Якщо ви неадекват, я рекомендую 1000%, що є максимумом", Color.RED);
                return;
            }
            musicManager.handler.volume(set);
            sendMessage(ev, "Гучність",
                    String.join("", "Гучність змінена від `", String.valueOf(percent), "%` до `", volume, "%`"), Color.GREEN);

        } catch(NumberFormatException ex) {

            sendMessage(ev, "Невірне число", volume + " не є цілим числом!", Color.RED);

        }
    }

    private void skip(IReplyCallback ev, MusicManager musicManager, String amount) {

        try {

            int skip = Integer.parseInt(amount);
            if (musicManager.handler.skip(skip)) {
                sendMessage(ev, String.join(" ", "Пропущено", String.valueOf(skip), skip <= 1 ? "трек" : "треків"), null, Color.GREEN);
            } else {
                if (skip >= 1) sendMessage(ev, "Число не приймається", "Список відтворення не такий великий", Color.RED);
                else sendMessage(ev, "Число не приймається", "Ви не можете перейти назад, вибачте(нюхай бебру)", Color.RED);
            }

        } catch(NumberFormatException ex) {

            sendMessage(ev, "Невірне число", amount + " не є цілим числом!", Color.RED);

        }

    }

    private void jump(IReplyCallback ev, MusicManager musicManager, String seconds) {

        try {
            int secs = Integer.parseInt(seconds);
            String msg = secs < 0 ? "Якщо ви бажаєте прослухати цей трек знову, будь ласка, використовуйте `/loop true`" : "Якщо ви хочете відтворити наступний трек, будь ласка, використовуйте `/skip`.";
            if (musicManager.handler.jump(secs)) {
                sendMessage(ev, "Перелистую...", "Пропущено `" + secs + "s`", Color.GREEN);
            } else {
                sendMessage(ev, "Пісня не така вже й довга", msg, Color.RED);
            }
        } catch(NumberFormatException ex) {
            sendMessage(ev, "Невірне число", seconds + " не є цілим числом!", Color.RED);
        }
    }

    private void queue(IReplyCallback ev, MusicManager musicManager) {

        List<AudioTrack> list = musicManager.handler.getQueue();
        EmbedBuilder embed = new EmbedBuilder().setTitle("Черга").setColor(Color.CYAN);
        embed.setDescription("Зараз грає: **" + list.get(0).getInfo().title + "** від автора **" + list.get(0).getInfo().author + "**");
        for (int i = 1; i <= Math.min(20, list.size() - 1); i++) {

            embed.appendDescription("\n" + i + ".  " + (i >= 10 ? "" : " ") + "`" + list.get(i).getInfo().title + "` by `" + list.get(i).getInfo().author + "`");

        }
        if (list.size() > 21) {
            embed.appendDescription("\n*+ " + (list.size() - 21) + " більше...*");
        }

        if(ev == null) Card.getChannel().sendMessageEmbeds(embed.build()).queue(msg -> msg.delete().queueAfter(15L, TimeUnit.SECONDS));
        else ev.replyEmbeds(embed.build()).setEphemeral(true).queue();

    }

    private void info(IReplyCallback ev, MusicManager musicManager) {
        AudioTrack track = musicManager.player.getPlayingTrack();
        AudioTrackInfo info = track.getInfo();

        long temp_duration = track.getDuration();
        int hours = (int) (temp_duration / 3600000);
        temp_duration -= hours * 3600000L;
        int mins = (int) (temp_duration / 60000);
        temp_duration -= mins * 60000L;
        int secs = (int) (temp_duration / 1000);

        temp_duration = track.getPosition();
        int poshours = (int) (temp_duration / 3600000);
        temp_duration -= poshours * 3600000L;
        int posmins = (int) (temp_duration / 60000);
        temp_duration -= posmins * 60000L;
        int possecs = (int) (temp_duration / 1000);

        String id = info.uri.replace("https://www.youtube.com/watch?v=","");
        boolean isYt = !id.equals(info.uri);

        ev.replyEmbeds(new EmbedBuilder().setTitle("Зараз грає:")
                .setDescription("Назва: `" + info.title + "`\n")
                .appendDescription("Автор: `" + info.author + "`\n")
                .appendDescription("Тривалість: `" + ((hours > 0) ? (hours + "h ") : "") + (mins >= 10 ? "" : "0") + mins + "min " + (secs >= 10 ? "" : "0") + secs + "s`\n")
                .appendDescription("Поточний час: `" + ((poshours > 0) ? (poshours + " : ") : "") + (posmins >= 10 ? "" : "0") + posmins + " : " + (possecs >= 10 ? "" : "0") + possecs + "`")
                .setThumbnail(isYt ? "https://img.youtube.com/vi/" + id + "/hqdefault.jpg" : null).build()).setEphemeral(true).queue();
    }

    private void bass(IReplyCallback ev, MusicManager musicManager, String amount) {

        try {

            int percentage = Integer.parseInt(amount);
            if (Math.abs(percentage - 250) > 250) {
                sendMessage(ev, "Невірне число", "Приймаються лише числа від 0 до 500!", Color.RED);
                return;
            }
            int from = musicManager.bassboost(percentage);
            sendMessage(ev, "Басс буст", "Бас змінений з " + from + " до " + percentage, Color.GREEN);

        }catch(NumberFormatException ex) {
            sendMessage(ev, "Число", amount + " не є файним!", Color.RED);
        }
    }

}
