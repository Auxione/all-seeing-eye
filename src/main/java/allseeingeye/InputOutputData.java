package allseeingeye;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class InputOutputData {
	public static Object loadObject(String path) {
		FileInputStream fileInput = null;
		Object returnedObject = null;
		ObjectInputStream objectInput = null;

		try {
			fileInput = new FileInputStream(new File(path));
			objectInput = new ObjectInputStream(fileInput);
			returnedObject = objectInput.readObject();
			objectInput.close();
			fileInput.close();

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return returnedObject;
	}

	public static void saveObject(Object object, String path) {
		try {
			FileOutputStream fileOutput = new FileOutputStream(new File(path));
			ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput);
			objectOutput.writeObject(object);
			objectOutput.close();
			fileOutput.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
