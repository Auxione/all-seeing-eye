

import java.util.ArrayList;

public class ChatCommand {
	public static ArrayList<ChatCommand> commandList = new ArrayList<ChatCommand>();
	public String command;
	public String description;
	public String parentCommand;

	public ChatCommand(String command, String description) {
		this.command = command;
		this.description = description;
		ChatCommand.commandList.add(this);
	}

	public ChatCommand(String parentCommand, String command, String description) {
		this.parentCommand = parentCommand;
		this.command = command;
		this.description = description;
		ChatCommand.commandList.add(this);
	}
}
