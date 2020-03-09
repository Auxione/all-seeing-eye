package allseeingeye;

import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.reaction.ReactionAddEvent;

public class addRole {
	private MessageListener messageListener;

	public Message addRoleMessage;
	public Role addRole;
	public User addRoleUser;

	public addRole(MessageListener messageListener) {
		this.messageListener = messageListener;
	}

	public void add(Message message, User user, String roleID) {
		addRole = Main.api.getRoleById(roleID).get();
		MessageBuilder mbuilder = new MessageBuilder().append("react with emoji to use with " + addRole.getName());
		this.addRoleMessage = mbuilder.send(message.getChannel()).join();
		this.addRoleUser = user;
	}

	public void onReactionAdd(ReactionAddEvent event) {
		Message reactedMessage = event.getMessage().get();
		if (this.addRoleMessage != null && this.addRoleUser != null) {
			if (reactedMessage.getId() == this.addRoleMessage.getId()
					&& event.getUser().getId() == addRoleUser.getId()) {
				Emoji reactedEmoji = event.getEmoji();

				messageListener.roleBook.put(reactedEmoji, addRole);

				if (messageListener.roleSelectionMessage != null) {
					messageListener.roleSelectionMessage.addReaction(reactedEmoji.asUnicodeEmoji().get());
				}

				System.out.println(
						addRole.getName() + " added to book with key of " + reactedEmoji.asUnicodeEmoji().get());
				this.addRoleMessage.delete();
				clean();
			}
		}
	}

	private void clean() {
		this.addRoleMessage = null;
		this.addRole = null;
		this.addRoleUser = null;
	}

	public void remove(Message message, User user, String emojiUnicode) {
		this.messageListener.roleBook.remove(emojiUnicode);
		if (messageListener.roleSelectionMessage != null) {
			messageListener.roleSelectionMessage.removeReactionByEmoji(emojiUnicode);
		}
	}

}
