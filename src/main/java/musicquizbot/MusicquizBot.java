package musicquizbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.security.auth.login.LoginException;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;


import musicquizbot.listener.CommandListener;
import musicquizbot.sound.PlayerManager;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;


public class MusicquizBot {
	public static MusicquizBot INSTANCE;

	public ShardManager shardMan;
	private CommandManager cmdMan;
	public AudioPlayerManager audioPlayerManager;
	public PlayerManager playerManager;
	
	public static void main(String[] args) {
		try {
			Constants.JDA_TOKEN = "MTAwNjY4NTg0MTczMDU4ODc3Ng.GIaA29._BB6zRP_Jt_UE4LNyEQP5kjmi4zpCfsarbB88c";
			new MusicquizBot();
		} catch (LoginException | IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	public MusicquizBot() throws LoginException, IllegalArgumentException {
		INSTANCE = this;

		ArrayList<GatewayIntent> intents = new ArrayList<>();
		intents.add(GatewayIntent.GUILD_EMOJIS);
		intents.add(GatewayIntent.GUILD_VOICE_STATES);
		intents.add(GatewayIntent.GUILD_PRESENCES);
		intents.add(GatewayIntent.GUILD_MEMBERS);
		intents.add(GatewayIntent.GUILD_MESSAGE_REACTIONS);
		intents.add(GatewayIntent.GUILD_MESSAGES);
		intents.add(GatewayIntent.DIRECT_MESSAGES);
		intents.add(GatewayIntent.DIRECT_MESSAGE_REACTIONS);
		
		DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.create(intents);
		builder.setToken(Constants.JDA_TOKEN);

		builder.setActivity(Activity.listening("маму твою"));
		builder.setStatus(OnlineStatus.ONLINE);

		this.audioPlayerManager = new DefaultAudioPlayerManager();
		this.playerManager = new PlayerManager();

		AudioSourceManagers.registerLocalSource(audioPlayerManager);
		AudioSourceManagers.registerRemoteSources(audioPlayerManager);

		this.cmdMan = new CommandManager();

		builder.addEventListeners(new CommandListener());

		this.shardMan = builder.build();
		
		
		System.out.println("Bot online");

	}


	public void shutdown() {
		new Thread(() -> {
			String line = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			try {
				while ((line = reader.readLine()) != null) {
					if (line.equalsIgnoreCase("exit")) {
						if (shardMan != null) {
							shardMan.setStatus(OnlineStatus.OFFLINE);
							shardMan.shutdown();
							System.out.println("Bot offline");
						}

						reader.close();
					} else {
						System.out.println("Use 'exit' to shutdown");
					}
				}
			} catch (IOException e) {
				// e.printStackTrace();
			}
		}).start();

	}

	public CommandManager getCmdMan() {
		return cmdMan;
	}
}
