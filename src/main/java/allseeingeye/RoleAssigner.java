package allseeingeye;

import java.util.HashMap;

import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.event.message.reaction.ReactionRemoveEvent;
import org.javacord.api.listener.message.reaction.ReactionAddListener;
import org.javacord.api.listener.message.reaction.ReactionRemoveListener;

public class RoleAssigner implements ReactionAddListener, ReactionRemoveListener {
	private Message addRoleMessage;
	private Role addRole;
	private User addRoleUser;

	public HashMap<Emoji, Role> roleBook = new HashMap<Emoji, Role>();
	public Message roleSelectionMessage;

	public RoleAssigner() {
	}

	public void onReactionAdd(ReactionAddEvent event) {
		addRoleToUserFromRoleMessage(event);
		onReactionAadd(event);
	}

	public void onReactionRemove(ReactionRemoveEvent event) {
		removeRoleToUserFromRoleMessage(event);
	}

	public void addToBook(Message message, User user, String roleID) {
		addRole = Main.api.getRoleById(roleID).get();
		MessageBuilder mbuilder = new MessageBuilder().append("React with emoji to use with " + addRole.getName());
		this.addRoleMessage = mbuilder.send(message.getChannel()).join();
		this.addRoleUser = user;
	}

	public void onReactionAadd(ReactionAddEvent event) {
		Message reactedMessage = event.getMessage().get();
		User user = event.getUser();

		if (this.addRoleMessage != null && this.addRoleUser != null) {
			if (reactedMessage.getId() == this.addRoleMessage.getId() && user.getId() == this.addRoleUser.getId()) {
				Emoji reactedEmoji = event.getEmoji();

				this.roleBook.put(reactedEmoji, addRole);

				if (this.roleSelectionMessage != null) {
					this.roleSelectionMessage.addReaction(reactedEmoji.asUnicodeEmoji().get());
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

	public void removeFromBook(Message message, User user, String emojiUnicode) {
		this.roleBook.remove(emojiUnicode);
		if (this.roleSelectionMessage != null) {
			this.roleSelectionMessage.removeReactionByEmoji(emojiUnicode);
		}
	}

	private void addRoleToUserFromRoleMessage(ReactionAddEvent event) {
		Message reactedMessage = event.getMessage().get();
		if (this.roleSelectionMessage != null) {
			if (reactedMessage.getId() == this.roleSelectionMessage.getId()) {
				User reactedUser = event.getUser();
				Emoji emoji = event.getEmoji();
				if (roleBook.containsKey(emoji) == true) {
					Role selectedRole = roleBook.get(emoji);
					reactedUser.addRole(selectedRole);
					System.out.println(selectedRole.getName() + " added to " + reactedUser.getName());
				}
			}
		}
	}

	private void removeRoleToUserFromRoleMessage(ReactionRemoveEvent event) {
		Message reactedMessage = event.getMessage().get();
		if (this.roleSelectionMessage != null) {
			if (reactedMessage.getId() == this.roleSelectionMessage.getId()) {
				User reactedUser = event.getUser();
				Emoji emoji = event.getEmoji();
				if (roleBook.containsKey(emoji) == true) {
					Role selectedRole = roleBook.get(emoji);
					reactedUser.removeRole(selectedRole);
					System.out.println(selectedRole.getName() + " removed from " + reactedUser.getName());
				}
			}
		}
	}

	public void createRoleMessage(Message message) {
		MessageBuilder m = new MessageBuilder().append("RoleSelectMessage");
		this.roleSelectionMessage = m.send(message.getChannel()).join();
		this.roleSelectionMessage.pin();
		for (Emoji emoji : this.roleBook.keySet()) {
			this.roleSelectionMessage.addReaction(emoji);
		}
	}
}
