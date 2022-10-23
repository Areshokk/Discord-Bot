import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Locale;

public class Hello extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {


        System.out.println("log - " + event.getMessage());

        if (!event.getAuthor().isBot()) {
            String messageSent = event.getMessage().getContentRaw();

            if (messageSent.equals("2"))
                event.getChannel().sendMessage("Не блять три \nзвичайно що два").queue();
        }




    }
}
