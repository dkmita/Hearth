package com.dom.util;

import java.io.UnsupportedEncodingException;

import com.dom.pcap.GameEnums.GameTag;

public class ByteUtils {

    
	@SuppressWarnings("unused")
    private static int BYTES_PER_ROW = 8;
	
	@SuppressWarnings("boxing")
    public static void printBytes( byte[] bytes, long length ) {
	    
		int i = 0;
		
		// header is up until the first 10 byte
		
		System.out.print( "header " );
		
		int dataInt;
		
		while( i < length ) {
			
			dataInt = getUnsignedInt( bytes[i] );
			
			if( dataInt == 10 ) {
				
				break;
			}

			System.out.print( String.format( "%1$-4d", dataInt ) );

			i++;
		}
		
		System.out.println( "" );
		
		// every subgroup will end with the 10 endline
		
		while( i < length ) {
		    
			int endline = getUnsignedInt( bytes[i] );

		    if( endline != 10 ) {
				
		        length = Math.min( length, bytes.length );
		        
				System.out.println( "something wrong with byte parsing. expecting 10 but got " + endline + " " + i );
                
                for( int k = i; k < length; k++ ) {
                    
                    byte[] fakeByte = new byte[1];
                    
                    fakeByte[0] = bytes[ k ];
                    
                    System.out.print( String.format( "%1$-4s", parseChar( fakeByte ) ) );
                }
                
                System.out.println( "" );
				
		        for( int k = i; k < length; k++ ) {
		            
		            System.out.print( String.format( "%1$-4s", getUnsignedInt( bytes[k] ) ) );
		        }

		        System.out.println( "" );
		        
		        return;
			}
			
			i++;
			
            int subLength = 0;
            
            int lenShiftAmount = 0;
            
            int lenNextByte = 0;
            
            do {
                
                lenNextByte = getUnsignedInt( bytes [ i++ ] );
                
                subLength |= ( lenNextByte & 0x7F ) << lenShiftAmount;
                
                lenShiftAmount += 7;
            } 
            while( ( lenNextByte & 0x80 ) != 0 );
						
			if( subLength == 0 ) {
				
				continue;
			}

			int subType = getUnsignedInt( bytes[ i++ ] );
			
		    if( subType == 50 ) {
		        
		        System.out.print( "Power Start " );
		    }
		    else if( subType == 58 ) {
		        
		        System.out.print( "Power End " );
		    }
		    else if( subType == 34 ) {
		        
		        int z = i;
		        
		        System.out.print( "Tag Change: " );
		        
		        int lenSubSubType = getUnsignedInt( bytes[ z++ ] );
		        
		        int randomEight = getUnsignedInt( bytes[ z++ ] );
		        
		        int result = 0;
		        
		        int shiftAmount = 0;
		        
		        int nextByte = 0;
		        
		        do {
		            
		            nextByte = getUnsignedInt( bytes [ z++ ] );
		            
		            result |= ( nextByte & 0x7F ) << shiftAmount;
		            
                    shiftAmount += 7;
		        } 
		        while( ( nextByte & 0x80 ) != 0 );
		                
		        System.out.print( " Entity: " + result );
		        
		        int randomSixteen = getUnsignedInt( bytes [ z++ ] );
		        
                int gameTagId = 0;
                    
                shiftAmount = 0;
                                    
                do {
                    
                    nextByte = getUnsignedInt( bytes [ z++ ] );
                    
                    gameTagId |= ( nextByte & 0x7F ) << shiftAmount;
                    
                    shiftAmount += 7;
                } 
                while( ( nextByte & 0x80 ) != 0 );
                
                int randomTwentyFour = getUnsignedInt( bytes [ z++ ] );
                
                int gameTagValue = 0;
                    
                shiftAmount = 0;
                                    
                do {
                    
                    nextByte = getUnsignedInt( bytes [ z++ ] );
                    
                    gameTagValue |= ( nextByte & 0x7F ) << shiftAmount;
                    
                    shiftAmount += 7;
                } 
                while( ( nextByte & 0x80 ) != 0 );
                
                gameTagToString( gameTagId, gameTagValue );
		    }
		    else if( subType == 18 ) { // new card
		        
		        // parse 18 -- discovered card
		        
		        System.out.print( "Card discovered:  " );
		        
		        int z = i;
		        
		        // print 2 numbers. Don't know what they mean
		        
		        for( int x = 0; x < 2; x++ ) {
		            
                    System.out.print( String.format( "%1$-4s", getUnsignedInt( bytes[ z + x ] ) ) );
		        }
                
		        z += 2;
		        
		        System.out.print( "  EntityId: " + getUnsignedInt( bytes[ z++ ] ) );
		        
		        if( getUnsignedInt( bytes[ z++ ] ) != 18 ) { 
		            
		            System.out.println( "didn't find an 18 where expected" );
		        }
		        
		        int subSubLength = getUnsignedInt( bytes[ z++ ] );
		        			        		
		        byte[] cardId = new byte[ subSubLength ];
		        
		        System.out.print( "  Name: " );
		        
                for( int x = 0; x < subSubLength; x++ ) {
                        
                    byte[] fakeByte = new byte[1];
                        
                    fakeByte[0] = bytes[ z + x ];
                    
                    cardId[ x ] = bytes[ z + x ];
                    	                        
                    System.out.print( parseChar( fakeByte ) );
		        }
                
                z += subSubLength;
		        
		        while( z < length ) {
                
	                if( getUnsignedInt( bytes[ z ] ) != 26  ) {
	                    
	                    if( getUnsignedInt( bytes[ z ] ) != 10) {
	                        
	                        System.out.println( "  breaking with endline " + getUnsignedInt( bytes[ z ] ) );
	                    }
	                    
	                    break;
	                }
	                
	                z += 1;
			        
			        int attributeType = getUnsignedInt( bytes[ z++ ] );
			        
			        @SuppressWarnings("unused")
                    byte randomEight = bytes[ z++ ];
			        
			        if( attributeType == 4 ) {
			            
			            int gameTagId = getUnsignedInt( bytes[ z++ ] );
			            
			            @SuppressWarnings("unused")
                        int randomSixteen = bytes[ z++ ];
			            
			            int gameTagValue = getUnsignedInt( bytes[ z++ ] );
			            
			            gameTagToString( gameTagId, gameTagValue );
			        }
			        else if( attributeType == 5 ) {
			            
			            int gameTagId = getUnsignedInt( bytes[ z++ ] );
	                     
			            @SuppressWarnings("unused")
			            int someOtherValue = bytes[ z++ ];
	                        
			            @SuppressWarnings("unused")
			            int randomSixteen = bytes[ z++ ];
	                        
			            int gameTagValue = getUnsignedInt( bytes[ z++ ] );
	                        
			            gameTagToString( gameTagId, gameTagValue );
			        }
		        }
		    }
			else {
			    
			    i--;
			    
			    System.out.println( subType + " length: " + subLength );
			    
			    for( int k = 0; k < subLength + 1; k++ ) {
	                    
                    byte[] fakeByte = new byte[1];
                    
                    fakeByte[0] = bytes[ i + k ];
                    
                    System.out.print( String.format( "%1$-4s", parseChar( fakeByte ) ) );
                }
			    
			    System.out.println( "" );
			    
			    for( int k = 0; k < subLength + 1; k++ ) {
	                    
                    System.out.print( String.format( "%1$-4d", getUnsignedInt( bytes[i + k] ) ) );
                }
			    
			    System.out.println( "" );
			}
		    
            System.out.println( "" );
            
            //i += subLength - 1;
            
            //continue;
            
            i--; // show subType
            
            System.out.print( "  " );
            
            for( int k = 0; k < subLength; k++ ) {
                
                System.out.print( String.format( "%1$-4d", getUnsignedInt( bytes[i + k] ) ) );
            }
            
            System.out.println( "" );
            
			i += subLength;
		}
	}
	
	private static String parseChar( byte[] b ) {
		
		try {
			
			if( b[0] == '\n' ) {
				
				return "\\n";
			}
			if( b[0] == '\t' ) {
				
				return "\\t";
			}
			if( b[0] >= (byte)'0' && b[0] <= (byte)'z' ) {
			    
			    //return (char)b[0];
			    return new String( b, "UTF-8" );
			}
			    
			return "*";
			
		} catch ( UnsupportedEncodingException e ) {
			
			System.out.println( "couldn't parse bytes " );
			
			return "";
		}
	}
	
	
	@SuppressWarnings("cast")
    private static int getUnsignedInt( byte b ) {
		
		int i = (int) b;

		return i < 0 ? i + 256 : i;
	}
	
	private static int getVarInt( byte[] bytes, int offset ) {
	
        int result = 0;
        
        int shiftAmount = 0;
        
        int nextByte = 0;
        
        do {
            
            nextByte = getUnsignedInt( bytes [ offset++ ] );
            
            result |= ( nextByte & 0x7F ) << shiftAmount;
        } 
        while( ( nextByte & 0x80 ) != 0 );
        
        return result;
    }
	
    public static void gameTagToString( int gameTagId, int gameTagValue ) {
        
        GameTag gameTag = null;
        
        for( GameTag gt: GameTag.values() ) {
            
            if( gt.id == gameTagId ) {
                
                gameTag = gt;
            }
        }
        
        if( gameTag == null ) {
            
            System.out.print( "  " + gameTagId + "notfound:" + gameTagValue );
            
            return;
        }
        
        System.out.print( "  " + gameTag.name() + ":" + gameTagValue );
    }
	
	public static void main( String[] args ) {
		
		byte[] testBytes = { (byte)10, '\t' };
		
		printBytes( testBytes, 1 );
	}
}
