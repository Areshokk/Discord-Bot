package areshok;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class TrackHandler extends AudioEventAdapter {

    private final AudioPlayer player;
    private AudioTrack currentTrack;
    private BlockingQueue<AudioTrack> queue;
    private BlockingQueue<AudioTrack> loopedQueue;
    private ScheduledExecutorService pauseService;
    private Map.Entry<Boolean, Boolean> looped = Map.entry(true, false);

    public TrackHandler(AudioPlayer player) {

        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
        this.loopedQueue = new LinkedBlockingQueue<>();

    }

    public boolean queue(TextChannel infoCardChannel, AudioTrack track) {

        if (!player.startTrack(track.makeClone(), true)) {

            boolean value = queue.offer(track);
            if (queue.size() == 1) updateInfoCard();
            loopedQueue.add(track);
            return value;
        } else {
            loopedQueue.add(track);
            currentTrack = track;
            Card.setChannel(infoCardChannel);
            updateInfoCard();
            DiscordBot.jda.getPresence().setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.playing(track.getInfo().title));
        }

        return true;

    }

    public boolean jump(int secs) {

        long delta = player.getPlayingTrack().getPosition() + secs * 1000L;
        if (delta >= player.getPlayingTrack().getDuration() && delta <= 0) {
            return false;
        } else {
            player.getPlayingTrack().setPosition(delta);
            return true;
        }

    }

    public List<AudioTrack> getQueue() {
        List<AudioTrack> list = new ArrayList<>(queue.stream().toList());
        list.add(0, currentTrack);
        return list;
    }

    public boolean skip(int num) {

        currentTrack = null;
        if (num < 1 && !next()) return false;
        if (num >= queue.size() && loopNotPossible()) return false;

        for (int i = 1; num > i; i++) {
            queue.poll();
        }

        next();

        return true;

    }

    public void shuffle() {

        List<AudioTrack> temp = new ArrayList<>(queue.stream().toList());
        Collections.shuffle(temp);
        queue = new LinkedBlockingQueue<>(temp);


        temp = new ArrayList<>(loopedQueue.stream().toList());
        Collections.shuffle(temp);
        loopedQueue = new LinkedBlockingQueue<>(temp);

        updateInfoCard();

    }

    public void setLooped(boolean looped, boolean single) {

        this.looped = Map.entry(looped, single);
        updateInfoCard();

    }

    public void stop() {

        queue.clear();
        loopedQueue.clear();
        player.stopTrack();
        currentTrack = null;
        if (player.isPaused()) player.setPaused(false);

    }

    public boolean pause() {

        boolean paused;
        player.setPaused(paused = !player.isPaused());
        DiscordBot.jda.getPresence().setPresence(paused ? OnlineStatus.IDLE : OnlineStatus.DO_NOT_DISTURB, Activity.playing(paused ? "break" : currentTrack.getInfo().title));
        return paused;

    }

    public boolean pause(int duration, TimeUnit time) {

        boolean setPaused = !player.isPaused();
        DiscordBot.jda.getPresence().setPresence(setPaused ? OnlineStatus.DO_NOT_DISTURB : OnlineStatus.IDLE, Activity.playing(setPaused ? currentTrack.getInfo().title : "break" + " for " + duration + " " + time.name()));
        pauseService = Executors.newSingleThreadScheduledExecutor();
        pauseService.schedule((Runnable) this::pause, duration, time);
        return setPaused;

    }

    public void volume(int percent) {
        player.setVolume(percent);
    }

    public Map.Entry<Boolean, Boolean> isLooped() {
        return looped;
    }

    // returns false if no tracks to play and looping wasn't successful (e.g. due to looped not being true)
    public boolean next() {

        if (queue.isEmpty() && loopNotPossible()) return false;

        if (looped.getKey() && !looped.getValue() || !looped.getKey() || currentTrack == null)
            currentTrack = queue.poll();
        if (currentTrack == null) return false;
        player.playTrack(currentTrack.makeClone());
        DiscordBot.jda.getPresence().setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.playing(currentTrack.getInfo().title));
        updateInfoCard();
        return true;

    }

    // Returns false, if successfully looped
    private boolean loopNotPossible() {
        if (looped.getKey() && (looped.getValue() || !loopedQueue.isEmpty())) {
            queue = new LinkedBlockingQueue<>(loopedQueue);
            return false;
        }
        return true;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {

        if (endReason.mayStartNext) {
            if (!next()) {
                Card.destroy();
                DiscordBot.jda.getPresence().setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.listening("/help"));
            }
        } else {
            Card.destroy();
            DiscordBot.jda.getPresence().setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.listening("/help"));
        }

    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        Card.setPaused(true);
        if (pauseService != null && !pauseService.isShutdown()) pauseService.shutdownNow();
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        Card.setPaused(false);
        updateInfoCard();
        if (pauseService != null && !pauseService.isShutdown()) pauseService.shutdownNow();
    }

    private void updateInfoCard() {
        Card.updateMessage(currentTrack,
                looped.getKey() && looped.getValue() ?
                        currentTrack.getInfo().title
                        : (queue.peek() == null ? (looped.getKey() && !loopedQueue.isEmpty() ? loopedQueue.peek().getInfo().title : "ніц нема")
                        : queue.peek().getInfo().title));
    }
}
