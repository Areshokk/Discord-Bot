import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Hello extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event) {
        String messageSent = event.getMessage().getContentRaw();


        if (messageSent.equalsIgnoreCase("<@500568425798696971>"))
            event.getChannel().sendMessage("Fuck you").queue();

    }
}
