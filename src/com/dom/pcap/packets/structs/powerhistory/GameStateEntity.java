package com.dom.pcap.packets.structs.powerhistory;

import com.dom.game.GameStateTracker;
import com.dom.pcap.GameEnums;
import com.dom.pcap.packets.CaptureStruct;
import com.dom.pcap.packets.encoding.FieldNumber;
import com.dom.pcap.packets.encoding.FieldType;
import com.dom.pcap.packets.structs.StructTag;

/**
 * State of an entity in game.
 *
 * @author Vincent Zhang
 */
public class GameStateEntity
        extends CaptureStruct {

    @FieldNumber(1)
    @FieldType(GameEnums.DataType.INT32)
    private int entity;

    @FieldNumber(2)
    @FieldType(GameEnums.DataType.STRING)
    private String name;

    @FieldNumber(3)
    @FieldType(GameEnums.DataType.STRUCT)
    private StructTag[] tags;


    public GameStateEntity() {
        tags = new StructTag[0];
        name = "";
    }

    /**
     * Get the id of the entity in question.
     */
    public int getEntity() {
        return entity;
    }

    /**
     * Get the internal name of the entity (eg CS2_123).
     */
    public String getInternalName() {
        return name;
    }

    /**
     * Get the tags describing the entity.
     */
    public StructTag[] getTags() {
        return tags;
    }
    
    public void process() {
    	GameStateTracker currentGame = GameStateTracker.getCurrentGame();
    	currentGame.createGameEntity( entity, tags);
    }
    
    
    @Override
    public String toString() {
        String ret = "Entity:" + entity + " Name:" + name + " Tags: ";
        for( StructTag tag : tags ) {
            ret += tag + "  ";
        }
        return ret;
    }
}
