//package pt.ulisboa.ist.sirs.project.securesmarthome.communication;
//
//import java.io.File;
//import java.nio.ByteBuffer;
//import java.nio.channels.FileChannel;
//import java.nio.channels.FileChannel.MapMode;
//import java.nio.MappedByteBuffer;
//import java.nio.file.StandardOpenOption;
//
//
///**
// * Created by Alex Anders on 21/11/2016.
// */
//public class SimpleChannel implements CommunicationChannel {
//
//    public SimpleChannel() throws Exception{
//        File f = new File( "FILE_NAME" );
//
//        FileChannel channel = FileChannel.open( f.toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE );
//
//        MappedByteBuffer b = channel.map( MapMode.READ_WRITE, 0, 4096 );
//        byteBuffer = b.as;
//    }
//
//    @Override
//    public void sendMessage(byte[] bytes) {
//
//        byteBuffer.put( Character. );
//
//        System.out.println( "Waiting for receiver." );
//        while( byteBuffer.get( 0 ) != '\0' );
//        System.out.println( "Finished waiting." );
//    }
//
//    @Override
//    public String receiveMessage() {
//        byte b;
//        String string = "";
//        System.out.println( "Waiting for data." );
//        while ((b = byteBuffer.get()) == 0);
//        while( b != 0 ) {
//            string = string.concat(String.valueOf(b));
//            b = byteBuffer.get();
//        }
//        System.out.println( "Data received:" );
//        System.out.println( string );
//
//        byteBuffer.put( 0, Byte.valueOf("0") );
//
//        return string;
//    }
//
//    private ByteBuffer byteBuffer;
//}
