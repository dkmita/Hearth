package com.dom.pcap;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import com.dom.pcap.packets.CapturePacket;
import com.dom.pcap.packets.CaptureStruct;
import com.dom.pcap.packets.encoding.HSDecoder;
import com.dom.util.ByteUtils;


public class Pcap {

    public static void main( String[] args ) throws Exception {

        
        System.out.println(System.getProperty("java.library.path"));

        //Obtain the list of network interfaces
        NetworkInterface[] interfaces = JpcapCaptor.getDeviceList();

        NetworkInterface networkInterface = null;

        //for each network interface
        for( NetworkInterface ni : interfaces ) {

            if( ni.name.contains( "en1" ) ) {

                networkInterface = ni;

                break;
            }
        }

        if( networkInterface == null ) {

            throw new Exception( "No NetworkInterface device found" );
        }

        JpcapCaptor captor = JpcapCaptor.openDevice( networkInterface, 65535, false, 1000);

        while( true ) {

            captor.processPacket( -1, new PacketListener() );
        }
    }


    static class PacketListener
        implements PacketReceiver {


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


                if( tcp.src_port == 3724 || tcp.src_port == 1119 ) {

                    // do filtering based on src_ip;

                    //System.out.println( tcp.src_ip );

                    //System.out.println(packet);

                    onPacket( tcp );
                }


            }
        }


        private void onPacket( TCPPacket tcp ) {

        	//System.out.println( tcp.src_ip.getHostAddress() + " " + tcp.src_port + "  " + tcp.dst_ip + " " + tcp.dst_port );

            if( tcp.syn && tcp.ack && tcp.data.length == 0 ) {

                //System.out.println( "Game opening" );
                //System.out.println( "------------------" );
                //System.out.println( tcp );
            }

            if( tcp.fin && tcp.ack ) {

                //System.out.println( "Game closing" );
                //System.out.println( "------------------" );
                //System.out.println( tcp );
            }
            
            //int length = tcp.length;
            
            byte[] payload = tcp.data;
            
            if( payload.length == 0 ) {
            	
            	//System.out.println( "Saw TCP packet with 0 paylaod. skipping" );
            	return;
            }
            
            ByteBuffer buffer = ByteBuffer.wrap( payload );
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            int packetId = buffer.getInt();
            //  If we have an invalid packetId drop the next 2 bytes
            //  (some sort of protocol noise?)
            if( packetId != 19 ) {
            	
            	//System.out.println( " uninteresting message" );
            	return;
            }
            if( ( packetId & 0xFF ) == 0 ) {
                
                System.out.println( "PacketID was zero, skipping" );
                
                return;
            }
            
            int length = buffer.getInt();
            
            System.out.println( "Payload Length: " + payload.length + "  Message Length: " + length );
            
            //cal.setTimeInMillis( System.currentTimeMillis() );
            //String time = dateTimeFormat.format( cal.getTime() );
            //System.out.println(  time + "   ID: " + packetId + "  payload length: " + payload.length + "   length: " + length + "   tcp length: " + tcp.len );
            //ByteUtils.printBytes( payload, length );

            if( Math.abs( length ) > payload.length ) {
            	
            	System.out.println( "Something wrong with parsing length. skipping" );
            	System.out.println( tcp.src_ip.getHostAddress() + " " + tcp.src_port + "  " + tcp.dst_ip + " " + tcp.dst_port );
            	
            	length = payload.length - 8;
            	//return;
            }
            
            ByteBuffer dataBuffer = ByteBuffer.allocate(length);
            byte[] hearthstonePacketData = new byte[length];
            for( int i = 0; i < length; i++ ) {
            	hearthstonePacketData[ i ] = payload[ i + 8 ];
            }
            
            dataBuffer.put(hearthstonePacketData);
            
            dataBuffer.flip();
            
            dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
            
            Class<? extends CaptureStruct> clazz = GameEnums.getById(GameEnums.PacketType.class, packetId).clazz;
            		
            if( clazz == null ) {
                
                System.out.println( "no packet for type " + packetId );
            }
            
            try {
                
				CapturePacket packet = HSDecoder.decode(dataBuffer, clazz);
			} 
            catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
}
