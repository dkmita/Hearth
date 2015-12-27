package com.dom.pcap.packets.structs.powerhistory;

import com.dom.pcap.GameEnums;
import com.dom.pcap.packets.CaptureStruct;
import com.dom.pcap.packets.encoding.FieldNumber;
import com.dom.pcap.packets.encoding.FieldType;
import com.dom.pcap.packets.structs.StructEntity;
import com.dom.pcap.packets.structs.StructPlayer;

/**
 * Contains information about the initial game state.
 *
 * @author Vincent Zhang
 */
public class CreateGameState
        extends CaptureStruct {

    @FieldNumber(1)
    @FieldType(GameEnums.DataType.STRUCT)
    private StructEntity gameEntity;

    @FieldNumber(2)
    @FieldType(GameEnums.DataType.STRUCT)
    private StructPlayer[] players;

    public CreateGameState() {
        players = new StructPlayer[0];
    }

    /**
     * Gets the entity representing this game.
     */
    public StructEntity getGameEntity() {
        return gameEntity;
    }

    /**
     * Gets all the players in this game.
     */
    public StructPlayer[] getPlayers() {
        return players;
    }
    
    @Override
    public String toString() {
        
        String ret = "GameEntity:" + getGameEntity().toString() + " ";
        
        for( StructPlayer player : getPlayers() ) {
            
            ret += player.toString() + " ";
        }
        
        return ret;
    }
}
