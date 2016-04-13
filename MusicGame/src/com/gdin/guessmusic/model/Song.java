package com.gdin.guessmusic.model;

public class Song {
	private int id;	//歌曲id
	private String mSongName;	//歌曲名字
	private String mSongFileName; //歌曲文件名
	private int mNameLength;	//歌曲长度
	private String distractor;  //干扰项
	private String type;		//歌曲类型
	
	public char[] getNameCharacters(){
		
		return mSongName.toCharArray();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDistractor() {
		return distractor;
	}

	public void setDistractor(String distractor) {
		this.distractor = distractor;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSongName() {
		return mSongName;
	}
	public void setSongName(String songName) {
		this.mSongName = songName;
		this.mNameLength = songName.length();
	}
	public String getSongFileName() {
		return mSongFileName;
	}
	public void setSongFileName(String songFileName) {
		this.mSongFileName = songFileName;
	}
	public int getNameLength() {
		
		return mNameLength;
	}
	
	
}
