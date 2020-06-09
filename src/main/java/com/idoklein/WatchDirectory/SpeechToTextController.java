package com.idoklein.WatchDirectory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpeechToTextController {
	
	@PostMapping("/transcribe")
	@GetMapping("/transcribe")
	public TranscribedText transcribe(@RequestParam(value = "filename"/*, defaultValue = "filename.txt"*/) String filename, @RequestParam(value = "duration") Double duration) throws InterruptedException {
		Thread.sleep(Math.round(duration * 1000 / 4));
		return new TranscribedText(filename);
	}
}