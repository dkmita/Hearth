
package com.dom.pcap.packets.encoding;

import com.dom.pcap.GameEnums;
import com.dom.pcap.packets.CaptureStruct;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

/**
 * Packet decoder.
 */
@SuppressWarnings("unchecked")
public class HSDecoder {

    private static final Map<String, Class<?>> ARRAY_CLASS_CACHE = new HashMap<String, Class<?>>();

    /**
     * Decodes a CaptureStruct from the given buffer.
     *
     * @param buffer A ByteBuffer containing the raw bytes.
     * @param clazz  The target CaptureStruct.
     * @param <T>    The subtype of CaptureStruct.
     * @throws IOException If there was an error reading the struct.
     */
    @SuppressWarnings("boxing")
    public static <T extends CaptureStruct> T decode( ByteBuffer buffer, Class<? extends CaptureStruct> clazz ) 
            throws IOException {
        
        Field[] fields = clazz.getDeclaredFields();
        
        HashMap<Integer, Field> fieldMap = new HashMap<Integer, Field>();
        
        for (Field field : fields) {
            FieldNumber order = field.getAnnotation(FieldNumber.class);
            if (order != null) {
                fieldMap.put(order.value(), field);
            }
        }
        
        T ret;
        
        try {
            ret = (T) clazz.getDeclaredConstructor().newInstance();
        } 
        catch( InstantiationException e ) {
            throw new RuntimeException("Unable to instantiate CaptureStruct " + clazz.getName(), e);
        }
        catch(  IllegalAccessException e ) {
        	throw new RuntimeException("Unable to instantiate CaptureStruct " + clazz.getName(), e);
        }
        catch( NoSuchMethodException e ) {
        	throw new RuntimeException("Unable to instantiate CaptureStruct " + clazz.getName(), e);
        }
        catch( InvocationTargetException e ) {
        	throw new RuntimeException("Unable to instantiate CaptureStruct " + clazz.getName(), e);
        }
        
        HashMap<Field, List<Object>> workingArrays = new HashMap<Field, List<Object>>();
        
        while( !fieldMap.isEmpty() && buffer.remaining() > 0 ) {
            
            processNextField( buffer, clazz, fieldMap, ret, workingArrays );
        }
        
        //  Process our arrays
        for( Map.Entry<Field, List<Object>> entry : workingArrays.entrySet() ) {
            try {
                
                handleArray(ret, entry);
            } 
            catch ( Exception e ) {
                
                throw new RuntimeException("Error processing arrays for " + clazz.getName() + " for field " + entry.getKey().getName(), e);
            }
        }
        
        ret.postRead();
        
        return ret;
    }

    @SuppressWarnings("boxing")
    private static <T extends CaptureStruct> void processNextField(ByteBuffer buffer, Class<? extends CaptureStruct> clazz, HashMap<Integer, Field> fieldMap, T ret, HashMap<Field, List<Object>> workingArrays) throws
            IOException {
        
        int i = Integer.valueOf( buffer.get() );
        
        int fieldNumber = i >> 3;
        
        int type = i & 0x07;
        
        Field field = fieldMap.get(fieldNumber);
        
        if( field == null ) {
            
            throw new IOException("Unknown field " + fieldNumber + " in " + clazz.getName() + ", " + fieldMap.size() + " remaining " + fieldMap.toString() + "\n contents " + ret._structName);
        }
        
        String fieldName = field.getName();
        
        field.setAccessible( true );
        
        FieldType fType = field.getAnnotation(FieldType.class);
        
        if( fType == null ) {
            
            throw new IOException("Missing field type for " + field.getDeclaringClass().getName() + "#" + fieldName);
        }
        
        GameEnums.DataType dataType = fType.value();
        
        boolean isArray = field.getType().isArray();
        
        if( ! isArray ) {
            
            fieldMap.remove( fieldNumber );
        }
        
        List<Object> list = null;
        
        if( isArray ) {
            
            list = workingArrays.get( field );
            
            if( list == null ) {
                
                list = new ArrayList<Object>();
                workingArrays.put( field, list );
            }
        }
        
        try {
            switch( dataType ) {
                case STRING: {
                    long length = readUnsignedVarInt(buffer);
                    byte[] data = new byte[(int) length];
                    buffer.get(data);
                    String s = new String(data);
                    if( isArray ) {
                        list.add(s);
                    } else {
                        field.set( ret, s );
                    }
                }
                break;
                
                case BYTES: {
                    long length = readUnsignedVarInt( buffer );
                    byte[] data = new byte[(int) length];
                    buffer.get(data);
                    if( isArray ) {
                        list.add( data );
                    } 
                    else {
                        field.set( ret, data );
                    }
                }
                break;
                
                case STRUCT: {
                    long length = readUnsignedVarInt(buffer);
                    byte[] data = new byte[(int) length];
                    buffer.get(data);
                    StructHandler handler = field.getAnnotation( StructHandler.class );
                    Class<? extends CaptureStruct> handlerClazz;
                    if( handler == null || handler.value() == null ) {
                        if (field.getType().isArray()) {
                            handlerClazz = (Class<? extends CaptureStruct>) field.getType().getComponentType();
                        } 
                        else {
                            handlerClazz = (Class<? extends CaptureStruct>) field.getType();
                        }
                    } 
                    else {
                        handlerClazz = handler.value();
                    }
                    ByteBuffer dataBuffer = ByteBuffer.allocate((int) length);
                    dataBuffer.put(data);
                    dataBuffer.flip();
                    dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
                    CaptureStruct packet = HSDecoder.decode(dataBuffer, handlerClazz);
                    if (isArray) {
                        list.add(packet);
                    } else {
                        field.set(ret, packet);
                    }
                }
                break;
                case INT:
                case INT32: {
                    if (isArray) {
                        int numElements = readUnsignedVarInt(buffer);
                        for (int c = 0; c < numElements; c++) {
                            list.add(readUnsignedVarInt(buffer));
                        }
                    } 
                    else {
                        field.setInt(ret, readUnsignedVarInt(buffer));
                    }
                }
                break;
                case UINT32: {
                    if( isArray ) {
                        int numElements = readUnsignedVarInt(buffer);
                        for (int c = 0; c < numElements; c++) {
                            list.add(readSignedVarInt(buffer));
                        }
                    } 
                    else {
                        field.setInt(ret, readSignedVarInt(buffer));
                    }
                }
                break;
                case INT64: {
                    if (isArray) {
                        int numElements = readUnsignedVarInt(buffer);
                        for (int c = 0; c < numElements; c++) {
                            list.add(readSignedVarLong(buffer));
                        }
                    } else {
                        field.setLong(ret, readSignedVarLong(buffer));
                    }
                }
                break;
                case UINT64: {
                    if (isArray) {
                        int numElements = readUnsignedVarInt(buffer);
                        
                        for (int c = 0; c < numElements; c++) {
                            list.add(readUnsignedVarLong(buffer));
                        }
                    } else {
                        field.setLong(ret, readUnsignedVarLong(buffer));
                    }
                }
                break;
                case BOOL: {
                    if( isArray ) {
                        int numElements = readUnsignedVarInt(buffer);
                        for( int c = 0; c < numElements; c++ ) {
                            list.add( readSignedVarLong(buffer) != 0L );
                        }
                    } 
                    else {
                        field.setBoolean( ret, readSignedVarLong(buffer) != 0L );
                    }
                }
                break;
                case ENUM: {
                    if( isArray ) {
                        int numElements = readUnsignedVarInt( buffer );
                        for( int c = 0; c < numElements; c++ ) {
                            int val = (int) readUnsignedVarLong(buffer);
                            Object enumVal = GameEnums.getById((Class<Enum>) field.getType(), val);
                            list.add(enumVal);
                        }
                    } 
                    else {
                        int val = (int) readUnsignedVarLong( buffer );
                        Object enumVal = GameEnums.getById((Class<Enum>) field.getType(), val);
                        field.set(ret, enumVal);
                    }
                }
                break;
                case FIXED32: {
                    if( isArray ) {
                        int numElements = readUnsignedVarInt(buffer);
                        for( int c = 0; c < numElements; c++ ) {
                            list.add(buffer.getInt());
                        }
                    } 
                    else {
                        field.setInt(ret, buffer.getInt());
                    }
                }
                break;
                default: {
                    throw new IOException("Unknown type " + type);
                }
            }
        } 
        catch( IOException e ) {
            
            throw e;
        } 
        catch( Exception e ) {
            
            throw new RuntimeException( "Unexpected error while parsing packet " + clazz.getName(), e );
        }
    }

    private static <T extends CaptureStruct> void handleArray(T ret, Map.Entry<Field, List<Object>> entry) throws IllegalAccessException {
        Field field = entry.getKey();
        List<Object> list = entry.getValue();
        Class<?> type = field.getType();
        if (!type.isArray()) {
            throw new IllegalArgumentException("Field " + field.getName() + " is not an array!");
        }
        Class<?> componentType = type.getComponentType();
        int size = list.size();
        //  For primative arrays, we unfortunately must explicitly create a primative array and populate it
        //  Field.set() does not like array objects created via Array.newInstance().
        if (componentType.isPrimitive()) {
            if (Integer.TYPE.equals(componentType)) {
                int[] arr = new int[size];
                int i = 0;
                for (Iterator<Object> iter = list.iterator(); iter.hasNext(); i++) {
                    arr[i] = ( (Integer) iter.next() ).intValue();
                }
                field.set(ret, arr);
            } else if (Long.TYPE.equals(componentType)) {
                long[] arr = new long[size];
                int i = 0;
                for (Iterator<Object> iter = list.iterator(); iter.hasNext(); i++) {
                    arr[i] = ( (Long) iter.next() ).longValue();
                }
                field.set(ret, arr);
            } else if (Double.TYPE.equals(componentType)) {
                double[] arr = new double[size];
                int i = 0;
                for (Iterator<Object> iter = list.iterator(); iter.hasNext(); i++) {
                    arr[i] = ( (Double) iter.next() ).doubleValue();
                }
                field.set(ret, arr);
            } else if (Float.TYPE.equals(componentType)) {
                float[] arr = new float[size];
                int i = 0;
                for (Iterator<Object> iter = list.iterator(); iter.hasNext(); i++) {
                    arr[i] = ( (Float) iter.next() ).floatValue();
                }
                field.set(ret, arr);
            } else if (Boolean.TYPE.equals(componentType)) {
                boolean[] arr = new boolean[size];
                int i = 0;
                for (Iterator<Object> iter = list.iterator(); iter.hasNext(); i++) {
                    arr[i] = ( (Boolean) iter.next() ).booleanValue();
                }
                field.set(ret, arr);
            } else if (Byte.TYPE.equals(componentType)) {
                byte[] arr = new byte[size];
                int i = 0;
                for (Iterator<Object> iter = list.iterator(); iter.hasNext(); i++) {
                    arr[i] = ( (Byte) iter.next() ).byteValue();
                }
                field.set(ret, arr);
            } else if (Short.TYPE.equals(componentType)) {
                short[] arr = new short[size];
                int i = 0;
                for (Iterator<Object> iter = list.iterator(); iter.hasNext(); i++) {
                    arr[i] = ( (Short) iter.next() ).shortValue();
                }
                field.set(ret, arr);
            } else if (Character.TYPE.equals(componentType)) {
                char[] arr = new char[size];
                int i = 0;
                for (Iterator<Object> iter = list.iterator(); iter.hasNext(); i++) {
                    arr[i] = ( (Character) iter.next() ).charValue();
                }
                field.set(ret, arr);
            }
        } else {
            Class<?> arrayClass = ARRAY_CLASS_CACHE.get(componentType.getName());
            if (arrayClass == null) {
                arrayClass = Array.newInstance(componentType, 0).getClass();
                ARRAY_CLASS_CACHE.put(componentType.getName(), arrayClass);
            }
            
            T[] arrayCopy = (T[]) Array.newInstance(componentType, size);
            for( int i = 0; i < size; i++ ) {
            	arrayCopy[i] = (T) list.get(i);
            }
            field.set(ret, arrayCopy);
        }
    }

    private static long readSignedVarLong(ByteBuffer buffer) { //throws IOException {
//        long raw = readUnsignedVarInt(buffer);
//        long temp = (((raw << 63) >> 63) ^ raw) >> 1;
//        return temp ^ (raw & (1L << 63));
        //  TODO So far we haven't seen any actual negative numbers in packets, so it's possible they don't actually differentiate between signed and unsigned.
        return readUnsignedVarLong(buffer);
    }

    private static long readUnsignedVarLong(ByteBuffer buffer) {
        long value = 0L;
        int i = 0;
        long b;
        while (((b = toUnsignedInt( buffer.get() )) & 0x80L) != 0) {
            value |= (b & 0x7F) << i;
            i += 7;
        }
        return value | (b << i);
    }

    private static int readSignedVarInt(ByteBuffer buffer) { //throws IOException {
//        int raw = readUnsignedVarInt(buffer);
//        int temp = (((raw << 31) >> 31) ^ raw) >> 1;
//        return temp ^ (raw & (1 << 31));
        //  TODO So far we haven't seen any actual negative numbers in packets, so it's possible they don't actually differentiate between signed and unsigned.
        return readUnsignedVarInt(buffer);
    }

    public static int readUnsignedVarInt(ByteBuffer buffer) {
        int value = 0;
        int i = 0;
        int b;
        while (((b = toUnsignedInt(buffer.get())) & 0x80) != 0) {
            value |= (b & 0x7F) << i;
            i += 7;
        }
        return value | (b << i);
    }
    
    @SuppressWarnings("cast")
    private static int toUnsignedInt( byte b ) {
    	
    	int signedInt = (int) b;
    	
    	if( signedInt < 0 ) {
    		
    		signedInt += 0x0100000000L;
    	}
    	
    	return signedInt;
    }
}
