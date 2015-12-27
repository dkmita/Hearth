
package com.dom.pcap.packets.encoding;


import com.dom.pcap.GameEnums;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FieldType {

    GameEnums.DataType value();

}
