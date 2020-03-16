package allseeingeye;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class CommandListener implements MessageCreateListener, IChatCommand {
	public static TextChannel commandListeningChannel;

	private String parentCommand = "CommandListener";
	private ChatCommand help = new ChatCommand("help", "Display help message");
	private ChatCommand setListenChannel = new ChatCommand(parentCommand, "setChannel",
			"Sets listening textchannel for bot commands");

	private NewUserHandler newUserHandler;
	private RoleAssigner roleAssigner;
	private CommanderList commanderList;

	public CommandListener() {
		Main.logger.addLog("CommandListener initialized.");
	}

	public void setChatCommands(RoleAssigner roleAssigner, NewUserHandler newUserHandler, CommanderList commanderList) {
		this.roleAssigner = roleAssigner;
		this.newUserHandler = newUserHandler;
		this.commanderList = commanderList;
	}

	public void onMessageCreate(MessageCreateEvent event) {
		parseCommands(event);
	}

	private void parseCommands(MessageCreateEvent event) {
		Message commandMessage = event.getMessage();
		User userThatCalledCommand = event.getMessage().getAuthor().asUser().get();
		TextChannel commandchannel = event.getChannel();

		if (commanderList.isCommander(userThatCalledCommand)) {
			String messageContent = commandMessage.getContent();
			if (messageContent.startsWith("!order")) {
				String[] values = messageContent.split(" ");

				execute(commandchannel, userThatCalledCommand, commandMessage, values);
				newUserHandler.execute(commandchannel, userThatCalledCommand, commandMessage, values);
				roleAssigner.execute(commandchannel, userThatCalledCommand, commandMessage, values);
				commanderList.execute(commandchannel, userThatCalledCommand, commandMessage, values);
				Main.logger.execute(commandchannel, userThatCalledCommand, commandMessage, values);

				event.getMessage().delete();
			}
		}
	}

	public void execute(TextChannel textChannel, User userThatCalledCommand, Message message, String[] values) {
		if (values[1].contentEquals(help.command) == true) {
			this.createAndSendHelpMessage(textChannel);
		}

		if (values[1].contentEquals(this.parentCommand) == true) {
			if (values[2].contentEquals(setListenChannel.command) == true) {
				this.setListenChannel(textChannel);
			}
		}
	}

	private void createAndSendHelpMessage(TextChannel channel) {
		MessageBuilder h = new MessageBuilder();
		for (ChatCommand cc : ChatCommand.commandList) {
			h.append("> ");
			if (cc.parentCommand != null) {
				h.append(cc.parentCommand).append(" ");
			}
			h.append(cc.command).append(" : ").append(cc.description).appendNewLine();
		}
		h.appendNewLine();
		h.append("Author: Crouzer#2959").appendNewLine();
		h.send(channel);
	}

	private void setListenChannel(TextChannel channel) {
		CommandListener.commandListeningChannel = channel;
		Main.logger.addLog("CommandListener: channel set to " + CommandListener.commandListeningChannel);
	}

	public void load(ConfigurationData configurationData) {
		if (configurationData.commandListeningChannelID != null) {
			CommandListener.commandListeningChannel = Main.api
					.getTextChannelById(configurationData.commandListeningChannelID).get();
			Main.logger.addLog("CommandListener: commandListeningChannel loaded from ASEconfig");
		}
	}

	public void save(ConfigurationData configurationData) {
		if (CommandListener.commandListeningChannel != null) {
			configurationData.commandListeningChannelID = CommandListener.commandListeningChannel.getId();
		}
		Main.logger.addLog("CommandListener: commandListeningChannel saved to ASEconfig");
	}
}
