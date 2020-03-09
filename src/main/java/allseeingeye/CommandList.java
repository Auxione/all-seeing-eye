package allseeingeye;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;

public class CommandList {
	public MessageListener messageListener;
	public addRole addRole;
	public TextChannel commandListeningChannel;

	public CommandList(MessageListener messageListener) {
		this.messageListener = messageListener;
		this.addRole = new addRole(messageListener);
	}

	public void setListenChannel(TextChannel channel) {
		this.commandListeningChannel = channel;
		System.out.println("Listening channel set to " +this.commandListeningChannel);
	}

	public void createRoleMessage(Message message) {
		MessageBuilder m = new MessageBuilder().append("RoleSelectMessage");
		messageListener.roleSelectionMessage = m.send(this.commandListeningChannel).join();
		messageListener.roleSelectionMessage.pin();
		for (Emoji emoji : messageListener.roleBook.keySet()) {
			messageListener.roleSelectionMessage.addReaction(emoji);
		}
	}
}
