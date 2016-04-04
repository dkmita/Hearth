package com.dom.pcap;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;

import com.dom.game.GameStateTracker;
import com.dom.pcap.packets.CaptureStruct;
import com.dom.pcap.packets.encoding.HSDecoder;


public class Pcap {
	 
	private static final String INTERNET_CONNECTION_NAME = "en0";
	
    public static void main( String[] args ) throws Exception {
    	
        //Obtain the list of network interfaces
    	
    	GameStateTracker.startGame();
        
        NetworkInterface[] interfaces;
        
        try {
        	interfaces = JpcapCaptor.getDeviceList();
        }
        catch( UnsatisfiedLinkError e ) {
        	System.out.println( "Make sure you have libjpcap.jnilib in your java.library.path" );
        	throw e;
        }

        NetworkInterface networkInterface = null;
        
        //for each network interface
        for( NetworkInterface ni : interfaces ) {
        	System.out.println( ni.name );
            if( ni.name.contains( INTERNET_CONNECTION_NAME ) ) {
                networkInterface = ni;
                break;
            }
        }

        if( networkInterface == null ) {
            throw new Exception( "No NetworkInterface device found. Make sure running user has privileges" );
        }

        JpcapCaptor captor = JpcapCaptor.openDevice( networkInterface, 65535, false, 1000);

        while( true ) {

            captor.processPacket( -1, new PacketListener() );
        }
    }


    static class PacketListener
        implements PacketReceiver {


    	static final int HEARTHSTONE_HEADER_BYTES = 8;
    	static byte[] hearthstoneMessage;
       	static int messageLengthReceived;
    	
        static ArrayList<String> ignoreIPs = new ArrayList<String>();

        static {
            ignoreIPs.add( "12.129.242.24" );   // iir.blizzard.com
            ignoreIPs.add( "12.129.236.214" );  // public-test.patch.battle.net
            ignoreIPs.add( "12.129.206.133" );  // us.patch.battle.net
            ignoreIPs.add( "213.248.127.133"); // eu.patch.battle.net
            ignoreIPs.add( "12.129.206.130" );  // us.logon.battle.net
            ignoreIPs.add( "213.248.127.130" ); // eu.logon.battle.net
            ignoreIPs.add( "12.130.244.193" );  // us.actual.battle.net
            ignoreIPs.add( "80.239.208.193" );  // eu.actual.battle.net
            ignoreIPs.add( "87.248.207.183" );  // eu.depot.battle.net
            ignoreIPs.add( "12.129.222.51" );   // us.tracker.worldofwarcraft.com
            ignoreIPs.add( "195.12.234.51" );   // eu.tracker.worldofwarcraft.com
            ignoreIPs.add( "67.215.65.132" );   // tracker.worldofwarcraft.com
        }


        @Override
        public void receivePacket( Packet packet ) {

            if( packet instanceof TCPPacket ) {

                TCPPacket tcp = (TCPPacket) packet;

                if( 
                	tcp.src_port == 3724 ||   // game info
                	tcp.src_port == 1119 ||  // other info (battlenet/friend info?)
                	false ) {  
                    
                	// do filtering based on src_ip;
                    onPacket( tcp );
                }
            }
        }


        private void onPacket( TCPPacket tcp ) {

            if( tcp.syn && tcp.ack && tcp.data.length == 0 ) {
                //System.out.println( "Game opening" );
            }

            if( tcp.fin && tcp.ack ) {
                //System.out.println( "Game closing" );
            }
            
            byte[] payload = tcp.data;
            
            if( payload.length == 0 ) {
            	return;
            }
            
            ByteBuffer buffer = ByteBuffer.wrap( payload );
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            int packetId = buffer.getInt();
            
            if( packetId == 116 || // ping 
            	packetId == 24 ||  // specator info
            	packetId > 1000 || packetId < 0 ) // probably some garbage
            	{ 
            	return;
            }
            
            if( ( packetId & 0xFF ) == 0 ) {
                System.out.println( "PacketID was zero, skipping" );
                return;
            }
            
            if( messageLengthReceived != 0 ) {
            	
            	if( messageLengthReceived + payload.length > hearthstoneMessage.length ) {
            		System.out.println( "Continuation of message too long. totalexpected: " + hearthstoneMessage.length + " previous: " + messageLengthReceived + " current: " + payload.length );
            		messageLengthReceived = 0;
            		hearthstoneMessage = null;
            		return;
            	}
            	
            	System.out.println( "Continuation of message: this payload " + payload.length );
                for( int i = 0; i < payload.length; i++ ) {
                	hearthstoneMessage[ i + messageLengthReceived ] = payload[ i ];
                }
                messageLengthReceived += payload.length;
                
            	if( messageLengthReceived + payload.length < hearthstoneMessage.length ) return;
            }
            else {
            	
            	int totalMessageLength = buffer.getInt();
            	int payloadLength = payload.length - HEARTHSTONE_HEADER_BYTES;
            	
                System.out.println( LocalDateTime.now().format( ISO_LOCAL_TIME ) + 
                					"  PacketType: " + packetId + 
                					"  Payload Length: " + payloadLength + 
                					"  Message Length: " + totalMessageLength + 
                	            	"  " + tcp.src_ip.getHostAddress() + " " + tcp.src_port + "  " + tcp.dst_ip + " " + tcp.dst_port );
                
            	if( totalMessageLength < 0 ) {
            		//System.out.println( "Received negative Hearthstone message length: " + totalMessageLength +
            		//					" total packet length: " + payload.length );
            		return;
            	}
                
                hearthstoneMessage = new byte[totalMessageLength];
            
                for( int i = 0; i < Math.min( totalMessageLength, payloadLength ); i++ ) {
                	hearthstoneMessage[ i ] = payload[ i + HEARTHSTONE_HEADER_BYTES ];
                }
            	
                messageLengthReceived = payloadLength;
                
                if( payloadLength < totalMessageLength ) {
	            	System.out.println( "Packet too short for message. Waiting for next packet" );
	            	System.out.println( tcp.src_ip.getHostAddress() + " " + tcp.src_port + "  " + tcp.dst_ip + " " + tcp.dst_port );
	            	// TODO(dkmita): figure out what's going on with the first messages of a game. remove next line once its done
	            	messageLengthReceived = 0;
	            	hearthstoneMessage = null;
	            	return;
                }
            }
            
            ByteBuffer dataBuffer = ByteBuffer.allocate(hearthstoneMessage.length);
            dataBuffer.put(hearthstoneMessage);
            dataBuffer.flip();
            dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
            
        	GameEnums.PacketType packetType = GameEnums.getById(GameEnums.PacketType.class, packetId);
        	if( packetType == null ) {
        		System.out.println( "Unknown PacketType: " + packetId );
        		messageLengthReceived = 0;
        		return;
        	}
        	
        	Class<? extends CaptureStruct> clazz = packetType.clazz;
            		
            if( clazz == null ) {
                System.out.println( "no packet for type " + packetId );
            }
            
            try {
                
				HSDecoder.decode(dataBuffer, clazz);
			} 
            catch (IOException e) {
				e.printStackTrace();
			}
            
            messageLengthReceived = 0;
            hearthstoneMessage = null;
        }
    }
}
