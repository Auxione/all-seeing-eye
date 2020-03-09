package allseeingeye;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class Main {
	static String Token = "NjQzMTAxODIxNzY1OTQzMjk2.XcglSA.tmppYWpbmkptPqSVlKFIOpCE0Vk";
	static String bot_Name = "All Seeing Eye";
	static DiscordApi api;

	public static void main(String[] args) {
		api = new DiscordApiBuilder().setToken(Token).login().join();
		MessageListener ms = new MessageListener(api);
		api.addMessageCreateListener(ms);
		api.addReactionAddListener(ms);
		api.addReactionRemoveListener(ms);
		System.out.println("Ready.");
	}
}