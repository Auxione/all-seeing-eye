package allseeingeye;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class Main {
	static String Token = "NjQzMTAxODIxNzY1OTQzMjk2.XcglSA.tmppYWpbmkptPqSVlKFIOpCE0Vk";
	static String bot_Name = "All Seeing Eye";
	static DiscordApi api;

	static Logger logger;
	static CommanderList commanderList;
	static RoleAssigner roleAssigner;
	static NewUserHandler newUserHandler;
	static CommandListener commandListener;

	public static void main(String[] args) {
		logger = new Logger("logs.txt");
		Main.logger.addLog("all-seeing-eye started.");
		
		api = new DiscordApiBuilder().setToken(Token).login().join();
		Main.commanderList = new CommanderList();
		Main.roleAssigner = new RoleAssigner();
		Main.newUserHandler = new NewUserHandler();
		Main.commandListener = new CommandListener();

		commandListener.setChatCommands(roleAssigner, newUserHandler, commanderList);

		api.addListener(commandListener);
		api.addListener(roleAssigner);
		api.addListener(newUserHandler);
		argum(args);
		
		Main.logger.addLog("Ready.");
	}

	private static void argum(String[] args) {
		if (args.length == 1) {
			if (args[0].contentEquals("loadConfig") == true) {
				logger.loadData();
			}
		}
	}
}