package allseeingeye;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.server.member.ServerMemberJoinEvent;
import org.javacord.api.listener.server.member.ServerMemberJoinListener;

public class NewUserHandler implements ServerMemberJoinListener, IChatCommand {
	private MessageBuilder welcomingMessage;
	private Message welcomingMessageOriginal;
	private Role welcomingRole;

	private String parentCommand = "NewUserHandler";

	private ChatCommand setRole = new ChatCommand(parentCommand, "setRole",
			"Set the welcoming role when member joined. Usage: setRole ROLEID");
	private ChatCommand setMessage = new ChatCommand(parentCommand, "setMessage",
			"Set the welcoming message when member joined. Usage: setMessage MESSAGEID");
	private ChatCommand status = new ChatCommand(parentCommand, "status", "display the status.");

	public NewUserHandler() {
		Main.logger.addLog("NewUserHandler initialized.");
	}

	private void setWelcomingMessage(User user, Message message) {
		if (message == null) {
			Main.logger.addLog(
					"NewUserHandler: Welcome message wanted to set by " + user.getName() + " but message not found");
			return;
		}
		this.welcomingMessageOriginal = message;
		this.welcomingMessage = MessageBuilder.fromMessage(welcomingMessageOriginal);
		Main.logger.addLog("NewUserHandler: Welcome message set by " + user.getName());
	}

	private void setWelcomingRole(User user, Role role) {
		if (role == null) {
			Main.logger
					.addLog("NewUserHandler: Welcome role wanted to set by " + user.getName() + " but role not found");
			return;
		}
		this.welcomingRole = role;
		Main.logger.addLog("NewUserHandler: Welcome role set to " + role.getName() + " by " + user.getName());
	}

	public void execute(TextChannel textChannel, User userThatCalledCommand, Message message, String[] values) {
		if (values[1].contentEquals(this.parentCommand) == true) {
			if (values[2].contentEquals(setRole.command) == true) {
				String roleID = values[3];
				Role r = Main.api.getRoleById(roleID).get();
				this.setWelcomingRole(userThatCalledCommand, r);
			}

			else if (values[2].contentEquals(setMessage.command) == true) {
				String messageID = values[3];
				this.welcomingMessageOriginal = Main.api.getMessageById(messageID, textChannel).join();
				this.setWelcomingMessage(userThatCalledCommand, welcomingMessageOriginal);
			}

			else if (values[2].contentEquals(status.command) == true) {
				createStatusMessage(userThatCalledCommand, textChannel);
			}
		}
	}

	private void createStatusMessage(User user, TextChannel textChannel) {
		MessageBuilder mm = new MessageBuilder();
		mm.append("NewUserHandler Status").appendNewLine();
		mm.append(">Role: ");
		if (this.welcomingRole != null) {
			mm.append(this.welcomingRole.getName());
		}
		mm.appendNewLine();
		mm.append(">Message: ");
		if (this.welcomingMessageOriginal != null) {
			mm.append(this.welcomingMessageOriginal.getIdAsString());
		}
		mm.send(textChannel);
		Main.logger.addLog("NewUserHandler: " + user.getName() + " wanted to see status.");
	}

	public void onServerMemberJoin(ServerMemberJoinEvent event) {
		User newUser = event.getUser();
		if (this.welcomingMessage != null) {
			welcomingMessage.send(newUser);
		}

		if (this.welcomingRole != null) {
			newUser.addRole(welcomingRole);
		}
	}

	public void load(ConfigurationData configurationData) {
		if (configurationData.welcomingMessageID != null && CommandListener.commandListeningChannel != null) {
			this.welcomingMessage = MessageBuilder.fromMessage(Main.api
					.getMessageById(configurationData.welcomingMessageID, CommandListener.commandListeningChannel)
					.join());
			Main.logger.addLog("NewUserHandler: welcomingMessage loaded from ASEconfig");
		}
		
		if (configurationData.welcomingRoleID != null) {
			this.welcomingRole = Main.api.getRoleById(configurationData.welcomingRoleID).get();
			Main.logger.addLog("NewUserHandler: welcomingRole loaded from ASEconfig");
		}
	}

	public void save(ConfigurationData configurationData) {
		if (this.welcomingMessageOriginal != null) {
			configurationData.welcomingMessageID = this.welcomingMessageOriginal.getId();
		}

		if (this.welcomingRole != null) {
			configurationData.welcomingRoleID = this.welcomingRole.getId();
		}
		Main.logger.addLog("NewUserHandler: data saved to ASEconfig");
	}
}
