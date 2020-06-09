package com.idoklein.WatchDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.io.FilenameUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WatchDirectoryApplication {

//	static {
//		System.setProperty("java.util.logging.config.file", "d:\\test-app\\logging.properties");
//	}

	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static final int SPEECH_TO_TEXT_RESOURCES_NUMBER = 4;

	public static void main(String[] args) {
		new WatchDirectoryApplication();
		if (args.length < 2) {
			System.out.println(
					"2 arguments should be provided: \n\t1. watch directory path \n\t2. output directory path");
			System.exit(1);
		}
		String watchDirectory = args[0];
		String outputDirectory = args[1];
		Thread springThread = new Thread() {
			public void run() {
				SpringApplication.run(WatchDirectoryApplication.class, args);
			}
		};
		springThread.run();
//		try {
//			Thread.sleep(3 * 1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

		ExecutorService pool = Executors.newFixedThreadPool(SPEECH_TO_TEXT_RESOURCES_NUMBER);

		Path watchDir = Paths.get(watchDirectory);
		// Path outputDir = Paths.get(outputDirectory);
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(watchDir)) {
			for (Path filePath : stream) {
				File file = filePath.toFile();
				String fileName = file.getName();
				String extension = FilenameUtils.getExtension(fileName);
				if (extension.equalsIgnoreCase("wav") || extension.equalsIgnoreCase("wave")) {
					LOGGER.warning(fileName + " is processed");
					try {
						AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
						AudioFormat format = audioInputStream.getFormat();
						long frames = audioInputStream.getFrameLength();
						double durationInSeconds = (frames + 0.0) / format.getFrameRate();
						//System.out.println(filePath.getFileName() + " duration is " + durationInSeconds);
						FileTranscriber fileTranscriber = new FileTranscriber(filePath, durationInSeconds,
								outputDirectory);
						pool.execute(fileTranscriber);
					} catch (UnsupportedAudioFileException e) {
						e.printStackTrace();
						LOGGER.warning(e.toString());
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.severe(e.toString());
		}
		pool.shutdown();
	}

}
