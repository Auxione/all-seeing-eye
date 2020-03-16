package allseeingeye;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ConfigurationData implements Serializable{
	public HashMap<String, Long> roleBook;
	public ArrayList<Long> commandersID;
	public Long roleSelectionMessageID;
	public Long welcomingMessageID;
	public Long welcomingRoleID;
	public Long commandListeningChannelID;
	public Long roleSelectionTextChannelID;
	public boolean roleMessageDeleteWhenReactBool;
}
