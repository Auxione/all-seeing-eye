package allseeingeye;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.user.User;

public interface IChatCommand {
	public void execute(TextChannel textChannel,User userThatCalledCommand,Message message,String[] values);
}
