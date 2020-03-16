package allseeingeye;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.user.User;

public class Logger implements IChatCommand {
	private String parentCommand = "Logger";
	private ChatCommand saveLogs = new ChatCommand(parentCommand, "save", "Save data to ASEconfig.");
	private ChatCommand loadData = new ChatCommand(parentCommand, "load", "load data from ASEconfig.");
	private ChatCommand uploadLogs = new ChatCommand(parentCommand, "upload", "upload logs to discord.");

	private String log = "";

	private Calendar calendar;
	private SimpleDateFormat simpleDateFormat;
	private PrintWriter logFile;

	private String filePath;

	public Logger(String filePath) {
		this.simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd|HH:mm:ss");
		this.filePath = filePath;
		addLog("Logger initialized.");
	}

	public void addLog(String strin) {
		this.calendar = Calendar.getInstance();
		String timestamp = simpleDateFormat.format(calendar.getTime());
		String newLog = timestamp + " => " + strin + "\n";

		this.log += newLog;
		System.out.print(newLog);
	}

	private void saveToFile() {
		try {
			this.logFile = new PrintWriter(new FileWriter(filePath, true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		addLog("Logger: saved to file.");
		logFile.print(this.log);
		logFile.close();
		this.log = "";
	}

	private void uploadLog(User user) {
		this.saveToFile();
		MessageBuilder uploadMessage = new MessageBuilder();
		File file = new File(filePath);
		uploadMessage.append("Logs").addAttachment(file);
		uploadMessage.send(user);
		addLog("Logger: " + user.getName() + " wanted bot logs.");
	}

	public void execute(TextChannel textChannel, User userThatCalledCommand, Message message, String[] values) {
		if (values[1].contentEquals(parentCommand) == true) {
			if (values[2].contentEquals(saveLogs.command) == true) {
				CommandListener.commandListeningChannel = textChannel;
				packData();
				Main.logger.saveToFile();
			}

			else if (values[2].contentEquals(uploadLogs.command) == true) {
				Main.logger.uploadLog(userThatCalledCommand);
			}

			else if (values[2].contentEquals(loadData.command) == true) {
				loadData();
			}
		}
	}

	private void packData() {
		ConfigurationData configurationData = new ConfigurationData();

		Main.roleAssigner.save(configurationData);
		Main.newUserHandler.save(configurationData);
		Main.commanderList.save(configurationData);
		Main.commandListener.save(configurationData);
		InputOutputData.saveObject(configurationData, "ASEconfig");
	}

	public void loadData() {
		ConfigurationData configurationData = (ConfigurationData) InputOutputData.loadObject("ASEconfig");
		if (configurationData == null) {
			addLog("Logger: ASEconfig not found.");
			return;
		}
		Main.commanderList.load(configurationData);
		Main.commandListener.load(configurationData);
		Main.roleAssigner.load(configurationData);
		Main.newUserHandler.load(configurationData);
	}
}
