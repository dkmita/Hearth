package com.dom.pcap.packets.structs.powerhistory;

import com.dom.pcap.GameEnums;
import com.dom.pcap.packets.CaptureStruct;
import com.dom.pcap.packets.encoding.FieldNumber;
import com.dom.pcap.packets.encoding.FieldType;

/**
 * Contains info on a hidden entityId.
 *
 * @author Vincent Zhang
 */
public class GameStateHiddenEntity
        extends CaptureStruct {

    @FieldNumber(1)
    @FieldType(GameEnums.DataType.INT32)
    private int entityId;

    @FieldNumber(2)
    @FieldType(GameEnums.DataType.ENUM)
    private GameEnums.Zone zone;

    /**
     * Get the ID of the entity.
     */
    public int getEntityId() {
        return entityId;
    }

    /**
     * Get which zone the entity belongs to.
     */
    public GameEnums.Zone getZone() {
        return zone;
    }
    
    @Override
    public String toString() {
        
        return "Entity:" + getEntityId() + " Zone:" + getZone();
    }
}
