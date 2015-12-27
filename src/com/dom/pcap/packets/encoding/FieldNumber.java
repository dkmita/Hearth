
package com.dom.pcap.packets.encoding;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FieldNumber {

    int value();

}
