package soen691.jsonConvert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class App 
{
    @SuppressWarnings("unchecked")
	public static void main( String[] args )
    {
    	FileInputStream freader;
    	HashMap<String, HashMap<String, Integer>> map = new HashMap<String, HashMap<String, Integer>>();
		try {
			freader = new FileInputStream("D:\\Study\\Concordia\\SOEN691\\Project\\throwAndKitchenSinkMetric.txt");  // set to your map file path.
			ObjectInputStream objectInputStream = new ObjectInputStream(freader);
			map = (HashMap<String, HashMap<String, Integer>>) objectInputStream.readObject();
//			System.out.println("The name is " + map3.get("test2"));
			objectInputStream.close();
		} 
		catch (IOException e) {
		}
		catch (ClassNotFoundException e) {
		}
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			mapper.writeValue(new File("D:\\Study\\Concordia\\SOEN691\\Project\\throwAndKitchenSinkMetric.json"), map);
			System.out.println("json output finished.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    }
}
