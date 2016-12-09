package pt.ulisboa.ist.sirs.project.securesmarthome.smarthomedevice;

/**
 * Created by Mathias on 2016-12-04.
 */
public class SHDMain {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new Exception("Wrong number of command options");
        }
        System.out.println("Initializing SHD");
        if(args[0].equals("lightBulb")) {
            LightBulb bulb = new LightBulb(new SHDSecurity());
            bulb.run();
        }

        //////////////////////////////////////////////////////////////////////////
        //                                                                      //
        // Option to add extra SHDs, according to the same pattern used above   //
        //                                                                      //
        //////////////////////////////////////////////////////////////////////////
    }
}
