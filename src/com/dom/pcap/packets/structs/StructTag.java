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
 * A data tag containing information about an entity.
 *
 * @author Vincent Zhang
 */
public class StructTag extends CaptureStruct {

    @FieldNumber(1)
    @FieldType(GameEnums.DataType.ENUM)
    private GameEnums.GameTag propertyName;

    @FieldNumber(2)
    @FieldType(GameEnums.DataType.INT)
    private int value;

    public StructTag() {
        super();
    }

    /**
     * Gets the property that this tag describes.
     */
    public GameEnums.GameTag getPropertyName() {
        return propertyName;
    }

    /**
     * Gets the value of the property that this tag describes.
     */
    public int getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return ( propertyName != null ? propertyName.name() : "UNKNOWN" ) + "=" + value;
    }
}
