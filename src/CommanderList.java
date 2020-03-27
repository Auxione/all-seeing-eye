
import java.util.ArrayList;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.user.User;

public class CommanderList implements IChatCommand {
	public ArrayList<Long> commandersID;

	private String parentCommand = "CommanderList";
	private ChatCommand addCommander = new ChatCommand(parentCommand, "add", "Add new Commander. Usage: add USERID");
	private ChatCommand removeCommander = new ChatCommand(parentCommand, "remove",
			"Remove Commander. Usage: remove USERID");
	private ChatCommand showCommanders = new ChatCommand(parentCommand, "show",
			"Shows which users can use bot commands.");

	public CommanderList() {
		this.commandersID = new ArrayList<Long>();
		this.commandersID.add(139320137198075906L);
	}

	public void execute(TextChannel textChannel, User userThatCalledCommand, Message message, String[] values) {
		if (values[1].contentEquals(parentCommand) == true) {
			if (values[2].contentEquals(addCommander.command) == true) {
				addCommander(userThatCalledCommand, values[3]);
			}

			else if (values[2].contentEquals(removeCommander.command) == true) {
				removeCommander(userThatCalledCommand, values[3]);
			}

			else if (values[2].contentEquals(showCommanders.command) == true) {
				showCommanders(textChannel, userThatCalledCommand);
			}
		}
	}

	public boolean isCommander(User user) {
		for (Long commander : commandersID) {
			if (user.getId() == commander) {
				return true;
			}
		}
		return false;
	}

	private void addCommander(User user, String id) {
		Long longID = Long.parseLong(id);
		for (Long commander : commandersID) {
			if (commander == longID) {
				Main.logger.addLog("CommanderList: " + user.getName() + " wanted to add ID " + longID
						+ " but it already in list.");
				return;
			}
		}
		this.commandersID.add(longID);
		Main.logger.addLog("CommanderList: New commander added with ID " + longID + " by " + user.getName());
	}

//not working
	private void removeCommander(User user, String id) {
		Long longID = Long.parseLong(id);
		for (Long commander : commandersID) {
			if (commander == longID) {
				this.commandersID.remove(longID);
				Main.logger.addLog("CommanderList: Commander with ID " + longID + " removed by " + user.getName());
				return;
			}
		}
		Main.logger.addLog("CommanderList: " + user.getName() + " wanted to remove ID " + longID
				+ " but it does not exist in list.");
	}

	private void showCommanders(TextChannel textChannel, User user) {
		MessageBuilder h = new MessageBuilder();
		h.append("Commanders").appendNewLine();
		for (Long commander : commandersID) {
			String u = Main.api.getUserById(commander).join().getNicknameMentionTag();
			h.append("> ").append(u).appendNewLine();
		}
		h.send(textChannel);
		Main.logger.addLog("CommanderList: " + user.getName() + " wanted to see the commander list.");
	}

	public void load(ConfigurationData configurationData) {
		if (configurationData.commandersID != null) {
			this.commandersID = configurationData.commandersID;
			Main.logger.addLog("CommanderList: Loaded commanders from ASEconfig.");
		}
	}

	public void save(ConfigurationData configurationData) {
		configurationData.commandersID = this.commandersID;
		Main.logger.addLog("CommanderList: commanders saved to ASEconfig.");
	}
}
