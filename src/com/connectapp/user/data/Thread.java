package com.connectapp.user.data;

import java.io.Serializable;
import java.util.ArrayList;

public class Thread implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4L;

	private ArrayList<Keyword> keywords = new ArrayList<Keyword>();

	private String threadID;

	private String threadName;

	public ArrayList<Keyword> getKeywords() {
		return keywords;
	}

	public void setKeywords(ArrayList<Keyword> keywords) {
		this.keywords = keywords;
	}

	public String getThreadID() {
		return threadID;
	}

	public void setThreadID(String threadID) {
		this.threadID = threadID;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

}
