package com.dom.pcap.packets.structs.powerhistory;

import com.dom.pcap.GameEnums;
import com.dom.pcap.packets.CaptureStruct;
import com.dom.pcap.packets.encoding.FieldNumber;
import com.dom.pcap.packets.encoding.FieldType;

/**
 * Indicates the beginning of a game state block.
 *
 * @author Vincent Zhang
 */
public class GameStateStart extends CaptureStruct {

    @FieldNumber(1)
    @FieldType(GameEnums.DataType.ENUM)
    private GameEnums.ActionSubType type;

    @FieldNumber(2)
    @FieldType(GameEnums.DataType.INT)
    private int index;

    @FieldNumber(3)
    @FieldType(GameEnums.DataType.INT)
    private int source;

    @FieldNumber(4)
    @FieldType(GameEnums.DataType.INT)
    private int target;

    public GameEnums.ActionSubType getType() {
        return type;
    }

    public int getIndex() {
        return index;
    }

    public int getSource() {
        return source;
    }

    /**
     * The ID of the target entity of the event.
     */
    public int getTarget() {
        return target;
    }
    
    @Override
    public String toString() {
        
        return "SubType:" + getType().name() + " Index:" + getIndex() + " Source:" + getSource() + " Target:" + getTarget();
    }
}
