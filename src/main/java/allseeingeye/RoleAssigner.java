package allseeingeye;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.listener.message.reaction.ReactionAddListener;

public class RoleAssigner implements ReactionAddListener, IChatCommand {
	private Message addRoleMessage;
	private Role addRole;
	private User addRoleUser;

	private String parentCommand = "RoleAssigner";
	private ChatCommand addRoleToBook = new ChatCommand(parentCommand, "addRole",
			"Add new role To book. Usage: addRole ROLEID");
	private ChatCommand removeRoleFromBook = new ChatCommand(parentCommand, "removeRole",
			"Remove role from book. Usage: removeRole ROLEID");
	private ChatCommand createRoleMessage = new ChatCommand(parentCommand, "createRoleMessage",
			"Create role message for automation.");

	public HashMap<Emoji, Role> roleBook = new HashMap<Emoji, Role>();

	public Message roleSelectionMessage;

	public RoleAssigner() {
		Main.logger.addLog("RoleAssiger initialized.");
	}

	public void onReactionAdd(ReactionAddEvent event) {
		addRoleToUserFromRoleMessage(event);
		messageforReactionEvent(event);
	}

	private void addToBook(TextChannel textChannel, User user, String roleID) {
		addRole = Main.api.getRoleById(roleID).get();
		MessageBuilder mbuilder = new MessageBuilder()
				.append(user.getMentionTag() + " react with emoji to use with " + addRole.getName());
		this.addRoleMessage = mbuilder.send(textChannel).join();
		this.addRoleUser = user;
	}

	private void messageforReactionEvent(ReactionAddEvent event) {
		Message reactedMessage = event.getMessage().get();
		User user = event.getUser();

		if (this.addRoleMessage == null && this.addRoleUser == null) {
			return;
		}

		else if (reactedMessage.getId() == this.addRoleMessage.getId() && user.getId() == this.addRoleUser.getId()) {
			Emoji reactedEmoji = event.getEmoji();

			this.roleBook.put(reactedEmoji, addRole);

			if (this.roleSelectionMessage != null) {
				this.roleSelectionMessage.addReaction(reactedEmoji.asUnicodeEmoji().get());
			}

			Main.logger.addLog("RoleAssigner: " + addRole.getName() + " added to book with key of "
					+ reactedEmoji.getMentionTag() + " by " + user.getName());
			this.addRoleMessage.delete();
			clean();
			if (this.roleSelectionMessage != null) {
				this.roleSelectionMessage.addReaction(reactedEmoji);
			}
		}
	}

	private void clean() {
		this.addRoleMessage = null;
		this.addRole = null;
		this.addRoleUser = null;
	}

	private void addRoleToUserFromRoleMessage(ReactionAddEvent event) {
		Message reactedMessage = event.getMessage().get();
		if (this.roleSelectionMessage == null) {
			return;
		}

		else if (reactedMessage.getId() == this.roleSelectionMessage.getId()) {
			User reactedUser = event.getUser();
			if (reactedUser.getId() == Main.api.getYourself().getId()) {
				return;
			}
			Emoji emoji = event.getEmoji();
			if (roleBook.containsKey(emoji) == true) {
				Role selectedRole = roleBook.get(emoji);
				if (hasRole(reactedUser, selectedRole) == false) {
					reactedUser.addRole(selectedRole);
					Main.logger
							.addLog("RoleAssigner: " + selectedRole.getName() + " added to " + reactedUser.getName());
				}

				else if (hasRole(reactedUser, selectedRole) == true) {
					reactedUser.removeRole(selectedRole);
					Main.logger.addLog(
							"RoleAssigner: " + selectedRole.getName() + " removed from " + reactedUser.getName());
				}
				reactedMessage.removeReactionByEmoji(reactedUser, emoji);
			}
		}
	}

	private boolean hasRole(User user, Role role) {
		List<Role> roles = user.getRoles(role.getServer());
		for (Role r : roles) {
			if (r.getId() == role.getId()) {
				return true;
			}
		}
		return false;
	}

	private void removeRoleFromb(User userThatCalledCommand, String id) {
		Long longID = Long.parseLong(id);
		Emoji emojiToRemove = null;
		for (Emoji emoji : roleBook.keySet()) {
			if (roleBook.get(emoji).getId() == longID) {
				emojiToRemove = emoji;
			}
		}
		if (emojiToRemove != null) {
			this.roleBook.remove(emojiToRemove);
			Main.logger.addLog(
					"RoleAssigner: " + userThatCalledCommand.getName() + " removed roleID " + id + " from book");
			if (this.roleSelectionMessage != null) {
				this.roleSelectionMessage.removeReactionByEmoji(emojiToRemove);
			}
			return;
		}
		Main.logger.addLog("RoleAssigner: " + userThatCalledCommand.getName() + "wanted to remove roleID " + id
				+ " from book but failed successfully.");
	}

	private void createRoleMessage(Message message) {
		MessageBuilder m = new MessageBuilder();
		m.append("Role Select Message").appendNewLine();
		for (Emoji emoji : this.roleBook.keySet()) {
			m.append(">").append(this.roleBook.get(emoji).getName()).append(" = ").append(emoji).appendNewLine();
		}
		this.roleSelectionMessage = m.send(message.getChannel()).join();
		for (Emoji emoji : this.roleBook.keySet()) {
			this.roleSelectionMessage.addReaction(emoji);
		}
	}

	public void execute(TextChannel textChannel, User userThatCalledCommand, Message message, String[] values) {
		if (values[1].contentEquals(this.parentCommand) == true) {
			if (values[2].contentEquals(createRoleMessage.command) == true) {
				this.createRoleMessage(message);
			}

			else if (values[2].contentEquals(addRoleToBook.command) == true) {
				this.addToBook(textChannel, userThatCalledCommand, values[3]);
			}

			else if (values[2].contentEquals(removeRoleFromBook.command) == true) {
				this.removeRoleFromb(userThatCalledCommand, values[3]);
			}
		}
	}
}
