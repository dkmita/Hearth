package com.dom.game;

import com.dom.pcap.GameEnums.Zone;

public class GameStateZone {

	private String name;
	private int id;
	private GameStateEntity[] entities;
	
	public GameStateZone( Zone zone, int capacity ) {
		this.name = zone.name();
		this.id = zone.id;
		this.entities = new GameStateEntity[capacity+1];
	}
	
	public void removeEntity( GameStateEntity entity, int position ) {
		validateZonePosition( position );
		if( !entity.equals(entities[position] ) ) {
			//throw new IllegalArgumentException( "entity " + entity.getId() );
			System.out.println( "entity" + entity.getId() + " no longer in position " + position );
			return;
		}
		entities[position] = null;
	}
	
	public void addEntity( GameStateEntity entity, int position ) {
		validateZonePosition( position );
		entities[position] = entity;
	}
	
	private void validateZonePosition( int position ) {
		if( position < 0 || position > entities.length ) {
			throw new IllegalArgumentException( "Invalid position " + position + " for Zone " + name + 
												" with capacity " + entities.length );
		}
	}
	
	@Override
	public String toString() {
		String returnString = name + "(" + id + "):";
		for( GameStateEntity entity : entities ) {
			if( entity != null ) {
				returnString += "(" + entity + "),";
			}
		}
		return returnString;
	}
}
