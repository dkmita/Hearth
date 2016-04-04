package com.dom.game;

class GameStateTag {
	
	@SuppressWarnings("unused")
	private int tagId;
	private int value;
	
	public GameStateTag( int id, int value ) {
		this.tagId = id;
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue( int value ) {
		this.value = value;
	}
}
