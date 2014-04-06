package com.qwerty.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Environment;
import android.util.Log;

/**
 * @author vinitnayak
 * Class that handles all persistent data storage for the application.
 * Uses JSON objects saved to text files in pulic external storage
 */
public class DataStorage {

	/**
	 * Add a new play to the storage Returns 0 if the save was successful, 1
	 * otherwise
	 * 
	 * @param play
	 *            Name of the play to be saved
	 * @throws JSONException
	 * @throws FileNotFoundException
	 */
	public static int addPlay(String play) throws JSONException,
			FileNotFoundException {
		String fileDirPath = getJsonDirectory();
		File f = new File(fileDirPath + "/play.txt");
		JSONObject jsonObject = new JSONObject();

		// If play file has already been created
		if (f.exists()) {
			String content = new Scanner(new File(f.getAbsolutePath()))
					.useDelimiter("\\Z").next();

			jsonObject = new JSONObject(content);
			if (jsonObject.has(play)) {
				return 1;
			}
		} else {
			File file = new File(Environment.getExternalStoragePublicDirectory(
					"/CurtainCall").getAbsolutePath());
			file.mkdirs();
		}

		try {
			File nextPlay = new File(getJsonDirectory() + "/plays/");
			nextPlay.mkdirs();
			jsonObject.put(play, nextPlay.getAbsolutePath() + "/play_" + play
					+ ".txt");
			writeToFile("Play", jsonObject.toString());
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		return 0;
	}

	/**
	 * Returns a list of all plays the user has added.
	 * 
	 * @throws JSONException
	 * @throws FileNotFoundException
	 */
	public static ArrayList<String> getAllPlays() throws JSONException,
			FileNotFoundException {
		ArrayList<String> playList = new ArrayList<String>();
		String fileDirPath = getJsonDirectory();
		File f = new File(fileDirPath + "/play.txt");
		JSONObject jsonObject = new JSONObject();

		// If play file has already been created
		if (f.exists()) {
			String content = new Scanner(new File(f.getAbsolutePath()))
					.useDelimiter("\\Z").next();

			jsonObject = new JSONObject(content);
			Iterator i = jsonObject.keys();
			while (i.hasNext()) {
				playList.add((String) i.next());
			}
		}
		for (String s : playList) {
			Log.d("TAGGIN", s);
		}
		return playList;
	}

	/**
	 * Gets the public directory for where all data is stored
	 */
	private static String getJsonDirectory() {
		File file = new File(Environment.getExternalStoragePublicDirectory(
				"/CurtainCall").getAbsolutePath());// +"/score.txt");
		return file.getAbsolutePath();
	}

	/**
	 * Writes the given content into filename (preceeded by whatever is returned
	 * from getJsonDirectory()). Note filename can have directories followed by
	 * filename
	 * 
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	private static void writeToFile(String fileName, String content)
			throws FileNotFoundException, UnsupportedEncodingException {
		File file = new File(Environment.getExternalStoragePublicDirectory(
				"/CurtainCall").getAbsolutePath());// +"/score.txt");

		PrintWriter writer = new PrintWriter(file.getAbsolutePath() + "/"
				+ fileName + ".txt", "UTF-8");
		writer.println(content);
		writer.close();
	}
}
