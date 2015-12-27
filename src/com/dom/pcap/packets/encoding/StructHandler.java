
package com.dom.pcap.packets.encoding;

import com.dom.pcap.packets.CaptureStruct;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface StructHandler {

    Class<? extends CaptureStruct> value();

}
