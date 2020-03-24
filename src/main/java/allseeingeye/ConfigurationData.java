package allseeingeye;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ConfigurationData implements Serializable{
	public HashMap<String, Long> roleBook;
	public ArrayList<Long> commandersID;
	public long welcomingMessageID;
	public long welcomingRoleID;
	public long commandListeningChannelID;
	public long roleSelectionMessageID;
	public long roleSelectionTextChannelID;
	public boolean roleMessageDeleteWhenReactBool;
	
}
