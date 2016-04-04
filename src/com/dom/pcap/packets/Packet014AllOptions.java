/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Vincent Zhang
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.dom.pcap.packets;

import com.dom.game.GameStateTracker;
import com.dom.pcap.GameEnums;
import com.dom.pcap.packets.encoding.FieldNumber;
import com.dom.pcap.packets.encoding.FieldType;
import com.dom.pcap.packets.structs.StructOption;

/**
 * Contains all the possible choices that the player may make currently. Server to Client only.
 *
 * @author Vincent Zhang
 */
public class Packet014AllOptions extends CapturePacket {
	
	public static final boolean DEBUG_OPTIONS = System.getProperty( "DEBUG_OPTIONS", "false").equalsIgnoreCase( "true" );

    @FieldNumber(1)
    @FieldType(GameEnums.DataType.INT32)
    private int sequenceNumber;

    @FieldNumber(2)
    @FieldType(GameEnums.DataType.STRUCT)
    private StructOption[] options;

    public Packet014AllOptions() {
        super();
        options = new StructOption[0];
    }

    /**
     * Gets the sequence number for this list of options. With each Packet014 this number gets bigger (not always by one).
     */
    public int getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Gets the list of options that can be performed.
     */
    public StructOption[] getOptions() {
    	
        return options;
    }
    
    @Override
    public void postRead() {
    	if( DEBUG_OPTIONS ) {
	        System.out.println( "Processed 014GameState message with " + options.length + " options" );
	        for( StructOption option : options ) {
	            System.out.println( "  " + option );
	        }
    	}
    	GameStateTracker.getCurrentGame().printGameState();
    }
}
