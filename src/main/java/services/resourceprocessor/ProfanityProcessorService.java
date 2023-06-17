package services.resourceprocessor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfanityProcessorService {

	public static List<String> loadBadWords(String filename) {
		List<String> badWords = new ArrayList<>();
		try (
			BufferedReader reader = new BufferedReader(new FileReader(filename))
		) {
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				badWords.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return badWords;
	}

	public static boolean checkForProfanity(
		String string,
		List<String> badWords
	) {
		for (String word : badWords) {
			if (string.contains(word)) {
				return true;
			}
		}
		return false;
	}

	public static void inspectString(String stringToCheck) {
		List<String> badWords = ProfanityProcessorService.loadBadWords(
			"../utils/blob/profanity.txt"
		);
		if (
			ProfanityProcessorService.checkForProfanity(stringToCheck, badWords)
		) {
			System.out.println("Profanity detected!");
		} else {
			System.out.println("No profanity found.");
		}
	}
}
