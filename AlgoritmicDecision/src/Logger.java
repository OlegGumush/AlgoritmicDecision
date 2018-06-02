

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Logger {

	static Logger logger = null;
	PrintWriter logFile;
	
	public Logger(){
		try {
			File f = new File("log.txt");
			f.exists();
			logFile = new PrintWriter("log.txt", "UTF-8");
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			System.out.println("Failed to open log.txt file");
			e.printStackTrace();
		}
	}
	
	public static Logger getInstance(){
		
		if(logger == null){
			logger =  new Logger();
		}
		return logger;
	}
	
	public void writeLog(String s){
		logFile.print(s + "\n");
		logFile.flush();
	}

	public static Logger getLogger() {
		return logger;
	}

	public static void setLogger(Logger logger) {
		Logger.logger = logger;
	}

	public PrintWriter getLogFile() {
		return logFile;
	}

	public void setLogFile(PrintWriter logFile) {
		this.logFile = logFile;
	}

	@Override
	public String toString() {
		return "Logger [logFile=" + logFile + "]";
	}
	
	
	
}
