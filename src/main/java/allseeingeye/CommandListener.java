package allseeingeye;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class CommandListener implements MessageCreateListener {
	public TextChannel commandListeningChannel;
	private RoleAssigner roleAssigner;

	public CommandListener(RoleAssigner roleAssigner) {
		this.roleAssigner = roleAssigner;
	}

	public void onMessageCreate(MessageCreateEvent event) {
		parseCommands(event);
	}

	private void parseCommands(MessageCreateEvent event) {
		Message commandMessage = event.getMessage();
		User userThatCalledCommand = event.getMessage().getAuthor().asUser().get();
		TextChannel commandchannel = event.getChannel();

		String authorname = userThatCalledCommand.getName();
		String messageContent = commandMessage.getContent();

		System.out.println(messageContent);
		if (authorname.contentEquals(Main.Bot_Author) && messageContent.startsWith("!order")) {
			String[] values = messageContent.split(" ");

			if (values[1].contentEquals("setListenChannel") == true) {
				this.setListenChannel(commandchannel);
			}

			if (this.commandListeningChannel != null) {
				if (values[1].contentEquals("createRoleMessage") == true) {
					this.roleAssigner.createRoleMessage(event.getMessage());
				}

				else if (values[1].contentEquals("addRole") == true) {
					this.roleAssigner.addToBook(commandMessage, userThatCalledCommand, values[2]);
				}

				else if (values[1].contentEquals("removeRole") == true) {
					this.roleAssigner.removeFromBook(commandMessage, userThatCalledCommand, values[2]);
				}
			}
			event.getMessage().delete();
		}
	}

	private void setListenChannel(TextChannel channel) {
		this.commandListeningChannel = channel;
		System.out.println("Listening channel set to " + this.commandListeningChannel);
	}
}
