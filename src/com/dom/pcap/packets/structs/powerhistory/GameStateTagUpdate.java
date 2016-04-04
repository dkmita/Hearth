package com.dom.pcap.packets.structs.powerhistory;

import com.dom.game.GameStateTracker;
import com.dom.pcap.GameEnums;
import com.dom.pcap.packets.CaptureStruct;
import com.dom.pcap.packets.encoding.FieldNumber;
import com.dom.pcap.packets.encoding.FieldType;

/**
 * A changed tag property.
 *
 * @author Vincent Zhang
 */
public class GameStateTagUpdate
        extends CaptureStruct {

    @FieldNumber(1)
    @FieldType(GameEnums.DataType.INT)
    private int entity;

    @FieldNumber(2)
    @FieldType(GameEnums.DataType.ENUM)
    private GameEnums.GameTag tag;

    @FieldNumber(3)
    @FieldType(GameEnums.DataType.INT)
    private int value;

    /**
     * The target entity of this change.
     */
    public int getEntity() {
        return entity;
    }

    /**
     * Get the tag that changed.
     */
    public GameEnums.GameTag getTag() {
        return tag;
    }

    /**
     * Get the new value of the tag that changed.
     */
    public int getValue() {
        return value;
    }
    
    public void process() {
    	if( getTag() != null ) {
    		GameStateTracker.getCurrentGame().updateEntityTagValue( entity, tag.id, value );
    	}
    }
    
    @Override
    public String toString() {
        return "Entity:" + entity + " Tag:" + ( tag != null ? tag.name() : "UNKNOWN" ) + " Value:" + value;
    }
}
