package com.idoklein.WatchDirectory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;

public class FileTranscriber implements Runnable {

	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private Path filePath;
	private double durationInSeconds;
	private String outputDirectoryPath;

	public FileTranscriber(Path filePath, double durationInSeconds, String outputDirectoryPath) {
		this.filePath = filePath;
		this.durationInSeconds = durationInSeconds;
		this.outputDirectoryPath = outputDirectoryPath;

	}

	@Override
	public void run() {		
		StringBuffer transcribedText = HttpClient.sendRequest(filePath.getFileName().toString(), durationInSeconds);
		
		Thread manageFilesThread = new Thread() {
			public void run() {
				JSONObject jsonObject = new JSONObject(transcribedText.toString());
				String content = jsonObject.getString("content");
				// System.out.println(content);
				File sourceFile = filePath.toFile();
				String fileName = sourceFile.getName();
				String fileNameWithoutExtension = FilenameUtils.removeExtension(fileName);
				Path outputDir = Paths.get(outputDirectoryPath);
				Path outputAudioFile = outputDir.resolve(fileName);
				if(outputAudioFile.toFile().exists()) {
					LOGGER.info("the file name " + fileName + " already exists in the output directory. adding a timestamp.");
					String timestamp = "_time" + System.currentTimeMillis();
					fileNameWithoutExtension += timestamp;
					outputAudioFile = (new File(outputDirectoryPath, fileNameWithoutExtension + "." + FilenameUtils.getExtension(outputAudioFile.toString()))).toPath();  
				}
				File outputTextFile = new File(outputDirectoryPath, fileNameWithoutExtension + ".txt");
				String outputTextFileName = outputTextFile.getName();
				// System.out.println(outputFile);
				FileWriter myWriter = null;
				Path temp;
				if(!sourceFile.exists()) {
					LOGGER.info(fileName + " doesn't exist anymore. Conversion is canceled.");
					return;
				}
				try {
					//move audio file to the output directory
					LOGGER.info(fileName + " is going to be moved to " + outputDirectoryPath);
					temp = Files.move(filePath, outputAudioFile);
					if (temp != null) {
						LOGGER.info(fileName + " file was moved successfully");
						//System.out.println(fileName + " File moved successfully");
					} else {
						//System.out.println("Failed to move the file " + fileName);
						LOGGER.info("Failed to move the file " + fileName);
					}

					//create the corresponding text file
					LOGGER.info(outputTextFileName + " is going to be written");
					outputTextFile.createNewFile();
					myWriter = new FileWriter(outputTextFile);
					myWriter.write(content);
					myWriter.close();
					LOGGER.info(fileNameWithoutExtension + " was converted to text file successfully");

				} catch (IOException e) {
					LOGGER.warning(e.toString());
					e.printStackTrace();
				}
			}
		};

		manageFilesThread.run();
	}
}
