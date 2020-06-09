package com.idoklein.WatchDirectory;

public class TranscribedText {
	
	private final String fileName;
	private final String content;

	public TranscribedText(String fileName) {
		this.fileName = fileName;
		this.content = "dummy text dummy text dummy text dummy text dummy text"
				+ " dummy text dummy text dummy text dummy text dummy text"
				+ " dummy text dummy text dummy text dummy text dummy text"
				+ " dummy text dummy text dummy text dummy text dummy text"
				+ " dummy text dummy text dummy text dummy text dummy text";
	}

	public String getFileName() {
		return fileName;
	}

	public String getContent() {
		return content;
	}
}
