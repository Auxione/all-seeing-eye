package allseeingeye;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class Main {
	static String Token = "NjQzMTAxODIxNzY1OTQzMjk2.XcglSA.tmppYWpbmkptPqSVlKFIOpCE0Vk";
	static String bot_Name = "All Seeing Eye";
	static String Bot_Author = "Crouzer";
	static DiscordApi api;

	public static void main(String[] args) {
		api = new DiscordApiBuilder().setToken(Token).login().join();
		RoleAssigner ra = new RoleAssigner();
		CommandListener cl = new CommandListener(ra);
		
		api.addMessageCreateListener(cl);
		api.addReactionAddListener(ra);
		api.addReactionRemoveListener(ra);

		System.out.println("Ready.");
	}
}