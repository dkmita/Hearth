package com.dom.game;

import com.dom.pcap.GameEnums;
import com.dom.pcap.GameEnums.GameTag;
import com.dom.pcap.GameEnums.Zone;
import com.dom.pcap.packets.structs.StructTag;

public class GameStateTracker {

	static final int MAX_GAMESTATEENTITY_ID = 1000;
	static GameStateTracker currentGame = null;
	static GameStateZone[] gameStateZones = new GameStateZone[Zone.values().length];
	static GameStateEntity[] gameEntities;

	
	static public void startGame() {
		currentGame = new GameStateTracker();
		gameStateZones[Zone.HAND.id] = new GameStateZone( Zone.HAND, 10 );
		gameStateZones[Zone.PLAY.id] = new GameStateZone( Zone.PLAY, 14 );
		gameEntities = new GameStateEntity[MAX_GAMESTATEENTITY_ID];
	}
	
	static public GameStateTracker getCurrentGame() {
		return currentGame;
	}
	
	public void printGameState() {
		System.out.println( gameStateZones[Zone.PLAY.id] );
		System.out.println( gameStateZones[Zone.HAND.id] );
	}
	
	public void createGameEntity( int entityId, StructTag[] tags ) {
		validateEntityId( entityId, false );
		GameStateEntity entity = gameEntities[entityId];
		if( entity == null ) {
			entity = new GameStateEntity( entityId );
			gameEntities[entityId] = entity;
		}
		for( StructTag tag : tags ) { 
			updateEntityTagValue( entityId, tag );
		}
	}
	
	public void updateEntityTagValue( int entityId, StructTag tag ) {
		if( tag.getPropertyName() == null ) return;
		updateEntityTagValue( entityId, tag.getPropertyName().id, tag.getValue() );
	}
		
		
	public void updateEntityTagValue( int entityId, int gameTagId, int value ) {
		validateEntityId( entityId, true );
		GameStateEntity entity = gameEntities[entityId];
		if( entity == null ) return; // TODO(dkmita): fix after we get player parsing
		updateEntityTagValuePreprocess( entity, gameTagId, value );
		entity.updateTagValue( gameTagId, value );
		updateEntityTagValuePostprocess( entity, gameTagId, value );
	}
	
	private void updateEntityTagValuePreprocess( GameStateEntity entity, int gameTagId, int value ) {
		if( gameTagId == GameEnums.GameTag.ZONE.id || 
			gameTagId == GameEnums.GameTag.ZONE_POSITION.id ) {
			int currentZoneId = entity.getTagValue( GameTag.ZONE.id );
			if( careAboutZone( currentZoneId ) ) {
				GameStateZone zone = gameStateZones[currentZoneId];
				zone.removeEntity( entity, entity.getTagValue( GameTag.ZONE_POSITION.id ) );
			}
		}
	}
	
	private void updateEntityTagValuePostprocess( GameStateEntity entity, int gameTagId, int value ) {
		if( gameTagId == GameTag.ZONE.id ) {
			if( careAboutZone( value ) ) {
				GameStateZone zone = gameStateZones[value];
				zone.addEntity( entity, entity.getTagValue( GameTag.ZONE_POSITION.id ) );
			}
		}
		if( gameTagId == GameTag.ZONE_POSITION.id ) {
			int currentZone = entity.getTagValue( GameTag.ZONE.id );
			if( careAboutZone( currentZone ) ) {
				GameStateZone zone = gameStateZones[currentZone];
				zone.addEntity( entity, value );
			}
		}
	}
	
	private boolean careAboutZone( int zoneId ) {
		return zoneId == Zone.HAND.id || zoneId == Zone.PLAY.id;
	}
	
	private void validateEntityId( int entityId, boolean checkEntityExists ) {
		if( entityId > MAX_GAMESTATEENTITY_ID || entityId < 0 ) {
			throw new IllegalArgumentException( "Invalid entityId:" + entityId );
		}
		if( checkEntityExists && gameEntities[entityId] == null ) {
			//if( entityId == 2 || entityId == 1 || entityId == 3 ) return; // TODO(dkmita) need to figure out how to instantiate ourselves
			//throw new IllegalArgumentException( "No entity with id: " + entityId );
		}
	}
}
