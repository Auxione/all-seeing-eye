
import org.javacord.api.event.connection.LostConnectionEvent;
import org.javacord.api.event.connection.ReconnectEvent;
import org.javacord.api.event.connection.ResumeEvent;
import org.javacord.api.listener.connection.LostConnectionListener;
import org.javacord.api.listener.connection.ReconnectListener;
import org.javacord.api.listener.connection.ResumeListener;

public class ReconnectManager implements ReconnectListener,ResumeListener,LostConnectionListener{

	@Override
	public void onReconnect(ReconnectEvent event) {
		Main.logger.addLog("Reconnected to server.");
	}

	@Override
	public void onResume(ResumeEvent event) {
		Main.logger.addLog("Resumed Connection");
	}

	@Override
	public void onLostConnection(LostConnectionEvent event) {
		Main.logger.addLog("Connection Lost to server.");
	}

}
