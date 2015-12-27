package com.dom.pcap.packets.structs.powerhistory;

import com.dom.pcap.packets.CaptureStruct;

/**
 * Indicates the end of a game state block.
 *
 * @author Vincent Zhang
 */
public class GameStateEnd
        extends CaptureStruct {

    @Override
    public String toString() {
        
        return "End";
    }

}
