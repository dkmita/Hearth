package com.dom.game;


class GameStateEntity { 
	
	public static int MAX_GAMESTATETAG_ID = 1000;
	
	private int id;
	private GameStateTag[] gameTags = new GameStateTag[MAX_GAMESTATETAG_ID];
	
	GameStateEntity( int id ) {
		this.id = id;
	}
	
	public void updateTagValue( int tagId, int value ) {
		validateTagId( tagId );
		GameStateTag tag = gameTags[tagId];
		if( tag == null ) {
			tag = new GameStateTag( tagId, value );
		}
		else {
			tag.setValue( value );
		}
	}
	
	public int getTagValue( int tagId ) {
		validateTagId( tagId );
		GameStateTag tag = gameTags[tagId];
		return tag == null ? 0 : tag.getValue();
	}
	
	private void validateTagId( int tagId ) {
		if( tagId > MAX_GAMESTATETAG_ID ) {
			throw new IllegalArgumentException( "tagId requested higher than MAX_GAMESTATETAG_ID" );
		}
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return String.valueOf( id );
	}
}
