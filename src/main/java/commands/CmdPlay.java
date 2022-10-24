package commands;


import ca.tristan.jdacommands.ICommand;
import com.jagrosh.jdautilities.command.CommandEvent;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

public class CmdPlay implements ICommand{

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        if (!Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState()).inAudioChannel()) {
            event.getTextChannel().sendMessage("недостойний").queue();
            return;
        }

        if (!event.getMember().getVoiceState().inAudioChannel()) {
            final AudioManager audioManager = event.getGuild().getAudioManager();
            final VoiceChannel memberChanel = (VoiceChannel) event.getMember().getVoiceState().getChannel();

        audioManager.openAudioConnection(memberChanel);
        }

        String link = String.join(" ", event.getTextChannel().getName());

        if (!isUrl(link)) {
            link = "шукаю:" + link + "музику";
        }

        PlayerManager.getINSTANCE().loadAndPlay(event.getTextChannel(), link);
    }

    public boolean isUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }

    }




}
