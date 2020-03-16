package allseeingeye;

import java.util.HashMap;
import java.util.List;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.event.message.reaction.ReactionRemoveEvent;
import org.javacord.api.listener.message.reaction.ReactionAddListener;
import org.javacord.api.listener.message.reaction.ReactionRemoveListener;

public class RoleAssigner implements ReactionAddListener, ReactionRemoveListener, IChatCommand {
	private String parentCommand = "RoleAssigner";

	private ChatCommand addRoleToBook = new ChatCommand(parentCommand, "addRole",
			"Add new role To book. Usage: addRole ROLEID");
	private ChatCommand removeRoleFromBook = new ChatCommand(parentCommand, "removeRole",
			"Remove role from book. Usage: removeRole ROLEID");
	private ChatCommand createRoleMessage = new ChatCommand(parentCommand, "createRoleMessage",
			"Create role message for automation.");
	private ChatCommand roleMessageDeleteWhenReact = new ChatCommand(parentCommand, "deleteWhenReact",
			"Deletes the emoji when reacted.");

	private Message addRoleMessage;
	private Long addRoleID;
	private User addRoleUser;

	public HashMap<String, Long> roleBook = new HashMap<String, Long>();
	public Message roleSelectionMessage;

	public boolean roleMessageDeleteWhenReactBool = false;

	public RoleAssigner() {
		Main.logger.addLog("RoleAssiger initialized.");
	}

	public void onReactionAdd(ReactionAddEvent event) {
		addRoleToUserFromRoleMessage(event);
		messageforReactionEvent(event);
	}

	public void onReactionRemove(ReactionRemoveEvent event) {
		if (roleMessageDeleteWhenReactBool == false) {
			User reactedUser = event.getUser();
			if (reactedUser.isBot() == false && reactedUser.isYourself() == false) {
				String emojiMentionTag = event.getEmoji().getMentionTag();

				Long selectedRoleID = roleBook.get(emojiMentionTag);
				Role selectedRole = Main.api.getRoleById(selectedRoleID).get();

				if (hasRole(reactedUser, selectedRole) == true) {
					reactedUser.removeRole(selectedRole);
					Main.logger.addLog(
							"RoleAssigner: " + selectedRole.getName() + " removed from " + reactedUser.getName());
				}
			}
		}
	}

	private void addToBook(TextChannel textChannel, User user, String roleID) {
		this.addRoleID = Long.parseLong(roleID);
		Role addRole = Main.api.getRoleById(roleID).get();

		MessageBuilder mbuilder = new MessageBuilder()
				.append(user.getMentionTag() + " react with emoji to use with " + addRole.getName());
		this.addRoleMessage = mbuilder.send(textChannel).join();
		this.addRoleUser = user;
	}

	private void messageforReactionEvent(ReactionAddEvent event) {
		Message reactedMessage = event.getMessage().orElse(null);
		User user = event.getUser();

		if (this.addRoleMessage == null && this.addRoleUser == null) {
			return;
		}
		if (reactedMessage == null) {
			Main.logger.addLog("RoleAssigner: reactedMessage is null?");
			return;
		}

		else if (reactedMessage.getId() == this.addRoleMessage.getId() && user.getId() == this.addRoleUser.getId()) {
			String emojiMentionTag = event.getEmoji().getMentionTag();
			Emoji emoji = null;

			if (event.getEmoji().isKnownCustomEmoji() == true) {
				emoji = Main.api.getCustomEmojiById(event.getEmoji().asKnownCustomEmoji().get().getId()).get();
			}

			else if (event.getEmoji().isUnicodeEmoji() == true) {
				emoji = event.getEmoji();
			}

			if (emoji != null) {
				this.roleBook.put(emojiMentionTag, addRoleID);

				if (this.roleSelectionMessage != null) {
					this.roleSelectionMessage.addReaction(emojiMentionTag);
				}

				Main.logger.addLog("RoleAssigner: " + Main.api.getRoleById(addRoleID).get().getName()
						+ " added to book with key of " + emojiMentionTag + " by " + user.getName());
				this.addRoleMessage.delete();
				clean();
				if (this.roleSelectionMessage != null) {
					this.roleSelectionMessage.addReaction(emoji);
				}
			}
		}
	}

	private void clean() {
		this.addRoleMessage = null;
		this.addRoleID = null;
		this.addRoleUser = null;
	}

	private void addRoleToUserFromRoleMessage(ReactionAddEvent event) {
		Message reactedMessage = event.getMessage().orElse(null);
		if (this.roleSelectionMessage == null) {
			Main.logger.addLog("RoleAssigner: roleSelectionMessage is null");
			return;
		}

		if (reactedMessage == null) {
			Main.logger.addLog("RoleAssigner: reactedMessage is null?");
			return;
		}

		else if (reactedMessage.getId() == this.roleSelectionMessage.getId()) {
			User reactedUser = event.getUser();
			if (reactedUser.getId() == Main.api.getYourself().getId()) {
				return;
			}
			Emoji emoji = event.getEmoji();
			String emojiMentionTag = emoji.getMentionTag();

			if (roleBook.containsKey(emojiMentionTag) == true) {
				Long selectedRoleID = roleBook.get(emojiMentionTag);
				Role selectedRole = Main.api.getRoleById(selectedRoleID).get();

				if (roleMessageDeleteWhenReactBool == true) {
					if (hasRole(reactedUser, selectedRole) == false) {
						reactedUser.addRole(selectedRole);
						Main.logger.addLog(
								"RoleAssigner: " + selectedRole.getName() + " added to " + reactedUser.getName());
					}

					else if (hasRole(reactedUser, selectedRole) == true) {
						reactedUser.removeRole(selectedRole);
						Main.logger.addLog(
								"RoleAssigner: " + selectedRole.getName() + " removed from " + reactedUser.getName());
					}
					reactedMessage.removeReactionByEmoji(reactedUser, emoji);
				}

				else if (roleMessageDeleteWhenReactBool == false) {
					if (hasRole(reactedUser, selectedRole) == false) {
						reactedUser.addRole(selectedRole);
						Main.logger.addLog(
								"RoleAssigner: " + selectedRole.getName() + " added to " + reactedUser.getName());
					}
				}

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
		String emojiToRemove = null;
		for (String emoji : roleBook.keySet()) {
			Role selectedRole = Main.api.getRoleById(roleBook.get(emoji)).get();
			if (selectedRole.getId() == longID) {
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

	private void createRoleMessage(TextChannel textChannel, User userThatCalledCommand, Message message) {
		MessageBuilder m = new MessageBuilder();
		m.append("Role Select Message").appendNewLine();
		for (String emoji : this.roleBook.keySet()) {
			Role role = Main.api.getRoleById(roleBook.get(emoji)).get();
			m.append(">").append(role.getName()).append(" = ").append(emoji).appendNewLine();
		}
		this.roleSelectionMessage = m.send(message.getChannel()).join();

		for (String emoji : this.roleBook.keySet()) {
			this.roleSelectionMessage.addReaction(getID(emoji));
		}
		CommandListener.commandListeningChannel = this.roleSelectionMessage.getChannel().asTextChannel().get();
		Main.logger.addLog(
				"RoleAssigner: User " + userThatCalledCommand.getName() + " created new roleMessage at " + textChannel);
	}

	private String getID(String emojiMentionTag) {
		if (emojiMentionTag.contains("<")) {
			return emojiMentionTag.substring(2, emojiMentionTag.length() - 1);
		} else
			return emojiMentionTag;
	}

	private void addRolesToExistingMessage(TextChannel textChannel, User userThatCalledCommand, String messageID) {
		Message roleMessage = Main.api.getMessageById(messageID, textChannel).join();
		if (roleMessage != null) {
			this.roleSelectionMessage = roleMessage;
			for (String emoji : this.roleBook.keySet()) {
				this.roleSelectionMessage.addReaction(getID(emoji));
			}
			CommandListener.commandListeningChannel = this.roleSelectionMessage.getChannel().asTextChannel().get();
			Main.logger.addLog("RoleAssigner: User " + userThatCalledCommand.getName()
					+ " selected existing message (ID: " + messageID + ") for role automation.");
		}

		else {
			MessageBuilder m = new MessageBuilder();
			m.append("Message not found at " + textChannel);
			m.send(textChannel);
		}
	}

	public void execute(TextChannel textChannel, User userThatCalledCommand, Message message, String[] values) {
		if (values[1].contentEquals(this.parentCommand) == true) {
			if (values[2].contentEquals(createRoleMessage.command) == true) {
				if (values.length == 3) {
					this.createRoleMessage(textChannel, userThatCalledCommand, message);
				}

				else if (values.length == 4) {
					this.addRolesToExistingMessage(textChannel, userThatCalledCommand, values[3]);
				}
			}

			else if (values[2].contentEquals(addRoleToBook.command) == true) {
				this.addToBook(textChannel, userThatCalledCommand, values[3]);
			}

			else if (values[2].contentEquals(removeRoleFromBook.command) == true) {
				this.removeRoleFromb(userThatCalledCommand, values[3]);
			}

			else if (values[2].contentEquals(roleMessageDeleteWhenReact.command) == true) {
				this.roleMessageDeleteWhenReactBool = Boolean.parseBoolean(values[3]);
			}
		}
	}

	public void load(ConfigurationData configurationData) {
		if (configurationData.roleBook != null) {
			this.roleBook = configurationData.roleBook;
			Main.logger.addLog("RoleAssigner: roleBook loaded from ASEconfig");
		}
		if (configurationData.roleSelectionMessageID != null && CommandListener.commandListeningChannel != null) {
			this.roleSelectionMessage = Main.api
					.getMessageById(configurationData.roleSelectionMessageID, CommandListener.commandListeningChannel)
					.join();
			if (this.roleSelectionMessage != null) {
				configurationData.roleMessageDeleteWhenReactBool = this.roleMessageDeleteWhenReactBool;
				Main.logger.addLog("RoleAssigner: roleSelectionMessage loaded from ASEconfig");
			}

			else if (this.roleSelectionMessage == null) {
				Main.logger.addLog("RoleAssigner: roleSelectionMessage is null?");
			}
		}
		if (this.roleMessageDeleteWhenReactBool != configurationData.roleMessageDeleteWhenReactBool) {
			this.roleMessageDeleteWhenReactBool = configurationData.roleMessageDeleteWhenReactBool;
			Main.logger.addLog(
					"RoleAssigner: DeleteWhenReact changed to " + configurationData.roleMessageDeleteWhenReactBool);
		}
	}

	public void save(ConfigurationData configurationData) {
		configurationData.roleBook = this.roleBook;
		configurationData.roleMessageDeleteWhenReactBool = this.roleMessageDeleteWhenReactBool;
		if (this.roleSelectionMessage != null) {
			configurationData.roleSelectionMessageID = this.roleSelectionMessage.getId();
		}
	}
}
