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

package com.dom.pcap.packets.structs;

import com.dom.pcap.GameEnums;
import com.dom.pcap.packets.CaptureStruct;
import com.dom.pcap.packets.encoding.FieldNumber;
import com.dom.pcap.packets.encoding.FieldType;

/**
 * A choice that can be made, and the targets (if any) that can receive the action of the choice.
 *
 * @author Vincent Zhang
 */
public class StructSubOption extends CaptureStruct {

    @FieldNumber(1)
    @FieldType(GameEnums.DataType.INT32)
    private int id;

    @FieldNumber(3)
    @FieldType(GameEnums.DataType.INT32)
    private int[] targets;


    public StructSubOption() {
        super();
        targets = new int[0];
    }

    /**
     * Gets this option's id.
     */
    public int getId() {
        return id;
    }

    /**
     * Get the ids of valid target entities that this action may be used against.
     */
    public int[] getTargets() {
        return targets;
    }
    
    public String toString() {
    	
    	String returnString = String.valueOf( id );
    	
    	if( targets.length > 0 ) {
    		returnString += "->[";
    		for( int i = 0; i < targets.length; i++ ) {
    			returnString += targets[i];
    			if( i < targets.length - 1 ) {
    				returnString += ",";
    			}
    		}
    		returnString += "]";
    	}
    	
    	return returnString;
    }
}
