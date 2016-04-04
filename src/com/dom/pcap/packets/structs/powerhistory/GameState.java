package com.dom.pcap.packets.structs.powerhistory;

import java.lang.reflect.Field;

import com.dom.pcap.GameEnums;
import com.dom.pcap.packets.CaptureStruct;
import com.dom.pcap.packets.encoding.FieldNumber;
import com.dom.pcap.packets.encoding.FieldType;

/**
 * Represents some aspect of the game state, containing one or more of the various subfields (emtpy subfields are null).
 */
public class GameState
    extends CaptureStruct {

    @FieldNumber(1)
    @FieldType(GameEnums.DataType.STRUCT)
    private GameStateEntity fullEntity;

    @FieldNumber(2)
    @FieldType(GameEnums.DataType.STRUCT)
    private GameStateEntity showEntity;

    @FieldNumber(3)
    @FieldType(GameEnums.DataType.STRUCT)
    private GameStateHiddenEntity hideEntity;

    @FieldNumber(4)
    @FieldType(GameEnums.DataType.STRUCT)
    private GameStateTagUpdate tagChange;

    @FieldNumber(5)
    @FieldType(GameEnums.DataType.STRUCT)
    private CreateGameState createGame;

    @FieldNumber(6)
    @FieldType(GameEnums.DataType.STRUCT)
    private GameStateStart stateStart;

    @FieldNumber(7)
    @FieldType(GameEnums.DataType.STRUCT)
    private GameStateEnd stateEnd;

    @FieldNumber(8)
    @FieldType(GameEnums.DataType.STRUCT)
    private GameStateMetaData metaData;

    public GameState() {
    }

    /**
     * Gets a complete entity - usually when a new entity is created or revealed.
     */
    public GameStateEntity getFullEntity() {
        return fullEntity;
    }

    /**
     * Converts an entity to full visibility (ie when an enemy card is played, or when you draw a card (?)).
     */
    public GameStateEntity getShowEntity() {
        return showEntity;
    }

    /**
     * Hides an entity.
     */
    public GameStateHiddenEntity getHideEntity() {
        return hideEntity;
    }

    /**
     * Gets a tag change - a tag property of an entity has changed.
     */
    public GameStateTagUpdate getTagChange() {
        return tagChange;
    }

    /**
     * Creates the initial game state.
     */
    public CreateGameState getCreateGame() {
        return createGame;
    }

    /**
     * Starts the game state block.
     */
    public GameStateStart getStateStart() {
        return stateStart;
    }

    /**
     * Ends the game state block.
     */
    public GameStateEnd getStateEnd() {
        return stateEnd;
    }

    /**
     * Gets metadata about some item.
     */
    public GameStateMetaData getMetaData() {
        return metaData;
    }
    
    public void process() {
    	
        for( Field field : getClass().getDeclaredFields() ) {
            field.setAccessible( true );
            Object fieldValue = null;
            
            try {
                fieldValue = field.get( this );
            } 
            catch (IllegalArgumentException e ) {
                continue;
            } 
            catch (IllegalAccessException e ) {
                e.printStackTrace();
            }
            
            if( fieldValue != null && fieldValue instanceof CaptureStruct ) {
                ( (CaptureStruct) fieldValue ).process();
            }
        }
    }
    
    
    @Override
    public String toString() {
        
        for( Field field : getClass().getDeclaredFields() ) {
            field.setAccessible( true );
            Object fieldValue = null;
            
            try {
                fieldValue = field.get( this );
            } 
            catch (IllegalArgumentException e ) {
                continue;
            } 
            catch (IllegalAccessException e ) {
                e.printStackTrace();
            }
            
            if( fieldValue != null && fieldValue instanceof CaptureStruct ) {
                return String.format( "%-22s %s", fieldValue.getClass().getSimpleName(), fieldValue.toString() );
            }
        }
        
        return  "No game state found";
    }
}
