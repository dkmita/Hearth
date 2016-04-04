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

import com.dom.pcap.GameEnums;
import com.dom.pcap.packets.encoding.FieldNumber;
import com.dom.pcap.packets.encoding.FieldType;
import com.dom.pcap.packets.structs.powerhistory.GameState;

/**
 * Contains information about the current game state since the last game state packet. Server to Client only.
 *
 * @author Vincent Zhang
 */
public class Packet019GameState 
    extends CapturePacket {
	
	public static final boolean DEBUG_GAME_STATE = System.getProperty( "DEBUG_GAME_STATE", "false").equalsIgnoreCase( "true" );

    @FieldNumber(1)
    @FieldType(GameEnums.DataType.STRUCT)
    private GameState[] states;


    public Packet019GameState() {
        states = new GameState[0];
    }

    /**
     * A sequence of GameStates detailing various aspects of the game.
     *
     * @see com.dom.pcap.packets.structs.powerhistory.GameState
     */
    public GameState[] getStates() {
        return states;
    }
    
    
    @Override
    public void postRead() {
    	if( DEBUG_GAME_STATE ) {
	        System.out.println( "Processed 019GameState message with " + states.length + " states" );
	        for( GameState state : states ) {
	            System.out.println( state );
	        }
    	}
    	
        for( GameState state : states ) {
            state.process();
        }
    }
}
