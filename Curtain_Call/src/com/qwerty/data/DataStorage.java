package com.qwerty.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.PriorityQueue;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Environment;
import android.util.Log;

/**
 * @author vinitnayak Class that handles all persistent data storage for the
 *         application. Uses JSON objects saved to text files in public external
 *         storage
 */
public class DataStorage {

	public static final int EXISTS = -1;
	public static final int EXCEPTION = -1;
	
	/**
	 * Add a new play to the storage Returns 0 if the save was successful, -1
	 * otherwise
	 * 
	 * @param play
	 *            Name of the play to be saved
	 * @throws JSONException
	 * @throws IOException
	 */
	public static int addPlay(String play) {
		String fileDirPath = getJsonDirectory();
		File f = new File(fileDirPath + "/play.txt");
		JSONObject jsonObject = new JSONObject();

		// If play file has already been created
		if (f.exists()) {
			String content;
			try {
				content = new Scanner(new File(f.getAbsolutePath()))
						.useDelimiter("\\Z").next();
				jsonObject = new JSONObject(content);
				if (jsonObject.has(play)) {
					return EXISTS;
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return EXCEPTION;
			} catch (JSONException e) {
				e.printStackTrace();
				return EXCEPTION;
			}

		} else {
			File file = new File(Environment.getExternalStoragePublicDirectory(
					"/CurtainCall").getAbsolutePath());
			file.mkdirs();
		}

		try {
			File nextPlay = new File(getJsonDirectory() + "/plays/");
			nextPlay.mkdirs();
			nextPlay = new File(nextPlay.getAbsolutePath() + "/play_" + play
					+ ".txt");
			nextPlay.createNewFile();
			jsonObject.put(play, nextPlay.getAbsolutePath() + "/play_" + play
					+ ".txt");
			writeToFile("Play", jsonObject.toString());
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return EXCEPTION;
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return EXCEPTION;
		} catch (JSONException e) {
			e.printStackTrace();
			return EXCEPTION;
		} catch (IOException e) {
			e.printStackTrace();
			return EXCEPTION;
		}

		return 0;
	}

	/**
	 * Add a chunk to the given parent play at given position Cannot have
	 * duplicate chunk names. Returns 0 if successful, -1 otherwise.
	 */
	public static int addChunk(String chunk, String parent, int position) {
		String dir = getJsonDirectory();
		dir += "/plays/play_" + parent + ".txt";

		File f = new File(dir);
		if (!f.exists()) {
			return -1;
		}
		
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(f.getAbsolutePath()))
					.useDelimiter("\\Z");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return EXCEPTION;
		}

		JSONObject chunks = new JSONObject();

		// Check if file is empty
		if (scanner.hasNext()) {
			try {
				chunks = new JSONObject(scanner.next());
			} catch (JSONException e) {
				e.printStackTrace();
				return EXCEPTION;
			}

			if (chunks.has(chunk)) {
				return -1;
			}

		}

		JSONObject lineObject = new JSONObject();
		try {
			lineObject.put("position", position);
			lineObject.put("lines", new JSONObject());
			chunks.put(chunk, lineObject);
			writeToFile("plays/play_" + parent, chunks.toString());
		} catch (JSONException e) {
			e.printStackTrace();
			return EXCEPTION;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return EXCEPTION;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return EXCEPTION;
		}


		return 0;
	}

	/**
	 * Adds a specific recording/line to the play and chunk specified.
	 * 
	 * @param play
	 *            of line to be added to
	 * @param chunk
	 *            of line to be added to
	 * @param filePath
	 *            of recorded audio file
	 * @param actor
	 *            Literally the string "me" or "them"
	 */
	public static int addLine(String play, String chunk, String filePath,
			String actor) {
		String dir = getJsonDirectory();
		dir += "/plays/play_" + play + ".txt";

		File f = new File(dir);
		if (!f.exists()) {
			return -1;
		}

		Scanner scanner;
		try {
			scanner = new Scanner(new File(f.getAbsolutePath()))
					.useDelimiter("\\Z");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return EXCEPTION;
		}

		JSONObject chunks = new JSONObject();

		if (scanner.hasNext()) {
			try {
				chunks = new JSONObject(scanner.next());
				JSONObject specificChunk = ((JSONObject) chunks.get(chunk));
				int counter = specificChunk.getInt("counter");
				String actorKey = actor + "_" + counter;
				counter++;
				
				JSONObject lineObject = ((JSONObject) chunks.get(chunk))
						.getJSONObject("lines");
				lineObject.put(actorKey, filePath);
				specificChunk.put("lines", lineObject);
				specificChunk.put("counter", counter);
				chunks.put(chunk, specificChunk);
				writeToFile("plays/play_" + play, chunks.toString());
			} catch (JSONException e) {
				e.printStackTrace();
				return EXCEPTION;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return EXCEPTION;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return EXCEPTION;
			}
			return 0;
		} else {
			return -1;
		}

	}

	/**
	 * Returns a list of all plays the user has added.
	 * 
	 * @throws JSONException
	 * @throws FileNotFoundException
	 */
	public static ArrayList<String> getAllPlays() {
		ArrayList<String> playList = new ArrayList<String>();
		String fileDirPath = getJsonDirectory();
		File f = new File(fileDirPath + "/play.txt");
		JSONObject jsonObject = new JSONObject();

		// If play file has already been created
		if (f.exists()) {
			String content;
			try {
				content = new Scanner(new File(f.getAbsolutePath()))
						.useDelimiter("\\Z").next();
				jsonObject = new JSONObject(content);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}

			Iterator i = jsonObject.keys();
			while (i.hasNext()) {
				playList.add((String) i.next());
			}
		}
		
		return playList;
	}

	/**
	 * Deletes the current play and all information associated with it.
	 * 
	 * @return
	 * @throws JSONException
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public static int deletePlay(String play) {
		String dir = getJsonDirectory();
		dir += "/plays/play_" + play + ".txt";

		File f = new File(dir);
		if (!f.exists()) {
			return -1;
		}

		Scanner scanner;
		try {
			scanner = new Scanner(new File(f.getAbsolutePath()))
					.useDelimiter("\\Z");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return EXCEPTION;
		}

		JSONObject chunks = new JSONObject();

		if (scanner.hasNext()) {

			try {
				chunks = new JSONObject(scanner.next());
			} catch (JSONException e) {
				e.printStackTrace();
				return EXCEPTION;
			}
			Iterator<String> chunkList = chunks.keys();
			// For each chunk in the play, we iterate over each line for that
			// chunk,
			// deleting each audio file associated with a line
			while (chunkList.hasNext()) {
				String currentChunk = chunkList.next();
				JSONObject obj;
				try {
					
					obj = chunks.getJSONObject(currentChunk);
					Iterator<String> lineIterator = ((JSONObject) obj.get("lines"))
							.keys();
					while (lineIterator.hasNext()) {
						// Get each line audio file and delete it from the device.
					}
					
					
				} catch (JSONException e) {
					e.printStackTrace();
					return EXCEPTION;
				}


			}
			scanner.close();
			boolean deleteSuccess = f.delete();

			if (!deleteSuccess) {
				return -1;
			}
		}

		// Now we need to delete entry from Play.txt
		ArrayList<String> playList = new ArrayList<String>();
		String fileDirPath = getJsonDirectory();
		f = new File(fileDirPath + "/play.txt");
		JSONObject jsonObject = new JSONObject();

		jsonObject.remove(play);
		try {
			writeToFile("Play", jsonObject.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return EXCEPTION;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return EXCEPTION;
		}
		return 0;
	}

	/**
	 * Deletes the chunk passed in as a parameter and all lines (audio files)
	 * associated with it.
	 */
	public static int deleteChunk(String play, String chunk)
			throws FileNotFoundException, JSONException,
			UnsupportedEncodingException {
		String dir = getJsonDirectory();
		dir += "/plays/play_" + play + ".txt";

		File f = new File(dir);
		if (!f.exists()) {
			return -1;
		}

		Scanner scanner = new Scanner(new File(f.getAbsolutePath()))
				.useDelimiter("\\Z");

		JSONObject chunks = new JSONObject();

		if (scanner.hasNext()) {
			chunks = new JSONObject(scanner.next());

			if (chunks.has(chunk) == false) {
				return -1;
			}

			JSONObject specificChunk = ((JSONObject) chunks.get(chunk));

			Iterator<String> lineIterator = ((JSONObject) specificChunk
					.get("lines")).keys();

			while (lineIterator.hasNext()) {
				String audioFilePath = lineIterator.next();
				// Add code here to remove file
			}

		} else {
			return -1;
		}

		chunks.remove(chunk);
		writeToFile("plays/play_" + play, chunks.toString());
		return 0;
	}

	/**
	 * Delets a line from the given play and chunk, at the position the
	 * user selected to delete. For example, if there are 4 lines total in 
	 * the chunk and the user chooses to delete the first one, @param position
	 * would be 0, regardless of whether it was truly the first recorded line or not.
	 */
	public static int deleteLine(String play, String chunk, int position)
			throws FileNotFoundException, JSONException,
			UnsupportedEncodingException {
		// Input sanitization
		if (position < 0) {
			return -1;
		}

		String dir = getJsonDirectory();
		dir += "/plays/play_" + play + ".txt";

		File f = new File(dir);
		if (!f.exists()) {
			return -1;
		}

		Scanner scanner = new Scanner(new File(f.getAbsolutePath()))
				.useDelimiter("\\Z");

		JSONObject chunks = new JSONObject();

		if (scanner.hasNext()) {
			chunks = new JSONObject(scanner.next());
			JSONObject specificChunk = ((JSONObject) chunks.get(chunk));
			JSONObject lineObject = specificChunk.getJSONObject("lines");

			PriorityQueue<Line> h = new PriorityQueue<Line>(10,
					new LineComparator());
			Iterator<String> i = lineObject.keys();
			
			// Populate priority queue
			while (i.hasNext()) {
				String s = i.next();
				Line l = new Line(Integer.parseInt(s.split("_")[1]),
						lineObject.getString(s), s.split("_")[0]);
				h.add(l);
			}

			int currentLine = 0;
			boolean positionFound = false;
			
			// Delete the item at index POSITION from priority queue
			while (h.isEmpty() == false) {
				Line l = h.poll();
				String s = l.getmActor() + "_"
						+ Integer.toString(l.getmPosition());
				if (currentLine == position) {
					lineObject.remove(s);
					positionFound = true;
					break;
				}
				currentLine++;
			}
			// Ensure file was deleted, otherwise bad input
			if (positionFound == false) {
				return -1;
			}

			specificChunk.put("lines", lineObject);
			chunks.put(chunk, specificChunk);
			writeToFile("plays/play_" + play, chunks.toString());
			return 0;
		} else {
			return -1;
		}

	}

	/**
	 * Returns a list of all chunks of the given playName.
	 * 
	 * @throws JSONException
	 * @throws FileNotFoundException
	 */
	public static ArrayList<String> getAllChunks(String play) {
		ArrayList<String> chunkList = new ArrayList<String>();
		String dir = getJsonDirectory();
		dir += "/plays/play_" + play + ".txt";

		File f = new File(dir);
		if (f.exists()) {
			Scanner scanner;
			try {
				scanner = new Scanner(new File(f.getAbsolutePath()))
						.useDelimiter("\\Z");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			}

			JSONObject chunks = new JSONObject();

			// Check if file is empty
			if (scanner.hasNext()) {
				try {
					chunks = new JSONObject(scanner.next());
				} catch (JSONException e) {
					e.printStackTrace();
					return null;
				}
				Iterator<String> i = (Iterator<String>) chunks.keys();

				while (i.hasNext()) {
					chunkList.add(i.next());
				}
			}
		}
		return chunkList;
	}

	/**
	 * Returns all the chunks in the form of a hashmap where they key is either
	 * "me_#" or "them_#" where "#" is some number which can be ignored. The
	 * value is the path to the audio file that was given when the line was
	 * added
	 * 
	 * @param play
	 * @param chunk
	 */
	public static LinkedHashMap<String, String> getAllLines(String parent,
			String chunk) {
		String dir = getJsonDirectory();
		dir += "/plays/play_" + parent + ".txt";

		File f = new File(dir);
		if (!f.exists()) {
			return null;
		}

		Scanner scanner;
		try {
			scanner = new Scanner(new File(f.getAbsolutePath()))
					.useDelimiter("\\Z");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}

		JSONObject chunks = new JSONObject();
		Comparator<Line> c = new LineComparator();
		PriorityQueue<Line> h = new PriorityQueue<Line>(10,
				new LineComparator());
		LinkedHashMap<String, String> linesMap = null;

		if (scanner.hasNext()) {
			linesMap = new LinkedHashMap<String, String>();
			
			JSONObject lineObject = null;
			try {
				chunks = new JSONObject(scanner.next());
				JSONObject specificChunk = ((JSONObject) chunks.get(chunk));
				lineObject = ((JSONObject) chunks.get(chunk))
						.getJSONObject("lines");
				Iterator<String> i = lineObject.keys();
				while (i.hasNext()) {
					String s = i.next();
					Line l = new Line(Integer.parseInt(s.split("_")[1]),
							lineObject.getString(s), s.split("_")[0]);
					h.add(l);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}

			int counter = 0;
			while (h.isEmpty() == false) {
				Line l = h.poll();
				String s = l.getmActor() + "_" + Integer.toString(counter);
				try {
					linesMap.put(l.getmActor() + "_" + Integer.toString(counter),
							lineObject.getString(s));
				} catch (JSONException e) {
					e.printStackTrace();
					return null;
				}
				counter++;
			}
		}
		return linesMap;
	}

	/**
	 * Gets the public directory for where all data is stored
	 */
	private static String getJsonDirectory() {
		File file = new File(Environment.getExternalStoragePublicDirectory(
				"/CurtainCall").getAbsolutePath());
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
				"/CurtainCall").getAbsolutePath());

		PrintWriter writer = new PrintWriter(file.getAbsolutePath() + "/"
				+ fileName + ".txt", "UTF-8");
		writer.println(content);
		writer.close();
	}
}
