package config;

// Singleton per ottenere le var di configurazione dal file 'config.properties'

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class ReadConfigFile {
	static ReadConfigFile instance = null;
	static Properties prop = new Properties();
	static InputStream input = null;
	
	private ReadConfigFile() {
		try {
			input = new FileInputStream("config.properties");

			// load a properties file
			prop.load(input);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static ReadConfigFile getInstance() {
		if (instance == null) {
			instance = new ReadConfigFile();
		}
		
		return instance;
	}
	
	public String getHostname() {
		return prop.getProperty("hostname");
	}
	
	public String getDBUser() {
		return prop.getProperty("dbuser");
	}
	
	public String getDBPwd() {
		return prop.getProperty("dbpassword");
	}
	
	public int getAnswerTime() {
		return Integer.parseInt(prop.getProperty("answertime"));
	}
	
	public String getTestToken1() {
		return prop.getProperty("token1");
	}
	
	public String getTestToken2() {
		return prop.getProperty("token2");
	}
}
