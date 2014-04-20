package com.qwerty.data;

/**
 * @author vinitnayak
 * Class only used for sorting in priority queue.
 */
public class Line {
	private int mPosition = -1;
	private String mFilePath = null;
	private String mActor = null;
	
	public Line(int position, String filePath, String actor) {
		mPosition = position;
		mFilePath = filePath;
		mActor = actor;
	}

	public String getmActor() {
		return mActor;
	}

	public void setmActor(String mActor) {
		this.mActor = mActor;
	}

	public int getmPosition() {
		return mPosition;
	}

	public void setmPosition(int mPosition) {
		this.mPosition = mPosition;
	}

	public String getmFilePath() {
		return mFilePath;
	}

	public void setmFilePath(String mFilePath) {
		this.mFilePath = mFilePath;
	}
}
