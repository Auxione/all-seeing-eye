package allseeingeye;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class Main {
	static String Token = "NjQzMTAxODIxNzY1OTQzMjk2.XcglSA.tmppYWpbmkptPqSVlKFIOpCE0Vk";
	static String bot_Name = "All Seeing Eye";
	static DiscordApi api;
	static Logger logger;

	public static void main(String[] args) {
		api = new DiscordApiBuilder().setToken(Token).login().join();
		logger = new Logger("logs.txt");
		
		CommanderList commanderList = new CommanderList();
		RoleAssigner roleAssigner = new RoleAssigner();
		NewUserHandler newUserHandler = new NewUserHandler();
		CommandListener commandListener = new CommandListener();
		
		commandListener.setChatCommands(roleAssigner, newUserHandler, commanderList);
		
		api.addListener(commandListener);
		api.addListener(roleAssigner);
		api.addListener(newUserHandler);

		Main.logger.addLog("Ready.");
	}
}