package allseeingeye;

import java.util.HashMap;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.event.message.reaction.ReactionRemoveEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.listener.message.reaction.ReactionAddListener;
import org.javacord.api.listener.message.reaction.ReactionRemoveListener;

public class MessageListener implements MessageCreateListener, ReactionAddListener, ReactionRemoveListener {
	public String selfName;
	public String botAuthorName = "Crouzer";

	public HashMap<Emoji, Role> roleBook = new HashMap<Emoji, Role>();

	public Message roleSelectionMessage;
	private CommandList commandList;

	public MessageListener(DiscordApi api) {
		this.selfName = api.getYourself().getName();
		this.commandList = new CommandList(this);

		// roleBook.put(csgoEmoji, api.getRoleById("685419899278852112").get());
		// roleBook.put(lolEmoji, api.getRoleById("686525818201309249").get());
	}

	public void onMessageCreate(MessageCreateEvent event) {
		parseCommands(event);
	}

	public void onReactionAdd(ReactionAddEvent event) {
		addRoleToUserFromRoleMessage(event);
		this.commandList.addRole.onReactionAdd(event);
	}

	public void onReactionRemove(ReactionRemoveEvent event) {
		removeRoleToUserFromRoleMessage(event);
	}

	private void parseCommands(MessageCreateEvent event) {
		Message commandMessage = event.getMessage();
		User userThatCalledCommand = event.getMessage().getAuthor().asUser().get();

		String authorname = userThatCalledCommand.getName();
		String messageContent = commandMessage.getContent();

		System.out.println(messageContent);
		if (authorname.contentEquals(botAuthorName) && messageContent.startsWith("!order")) {
			String[] values = messageContent.split(" ");

			if (values.length == 2) {
				if (values[1].contentEquals("setListenChannel") == true) {
					this.commandList.setListenChannel(event.getChannel());
				}

				else if (values[1].contentEquals("createRoleMessage") == true) {
					this.commandList.createRoleMessage(event.getMessage());
				}
			}

			else if (values.length == 3) {
				if (values[1].contentEquals("addRole") == true) {
					this.commandList.addRole.add(commandMessage, userThatCalledCommand, values[2]);
				}

				else if (values[1].contentEquals("removeRole") == true) {
					this.commandList.addRole.remove(commandMessage, userThatCalledCommand, values[2]);
				}
			}
			event.getMessage().delete();
		}
	}

	private void addRoleToUserFromRoleMessage(ReactionAddEvent event) {
		Message reactedMessage = event.getMessage().get();
		if (this.roleSelectionMessage != null) {
			if (reactedMessage.getId() == this.roleSelectionMessage.getId()) {
				User reactedUser = event.getUser();
				Emoji emoji = event.getEmoji();
				if (roleBook.containsKey(emoji) == true) {
					Role selectedRole = roleBook.get(emoji);
					reactedUser.addRole(selectedRole);
					System.out.println(selectedRole.getName() + " added to " + reactedUser.getName());
				}
			}
		}
	}

	private void removeRoleToUserFromRoleMessage(ReactionRemoveEvent event) {
		Message reactedMessage = event.getMessage().get();
		if (this.roleSelectionMessage != null) {
			if (reactedMessage.getId() == this.roleSelectionMessage.getId()) {
				User reactedUser = event.getUser();
				Emoji emoji = event.getEmoji();
				if (roleBook.containsKey(emoji) == true) {
					Role selectedRole = roleBook.get(emoji);
					reactedUser.removeRole(selectedRole);
					System.out.println(selectedRole.getName() + " removed from " + reactedUser.getName());
				}
			}
		}
	}

}
