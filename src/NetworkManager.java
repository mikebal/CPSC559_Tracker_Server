import java.net.InetAddress;

/**
 * Created by Michael on 2/25/2016.
 */
public class NetworkManager {

    String name = "So many Cats";

    public static final String SPECIAL_SEPARATOR = "'#";

    public static String getIPaddress(){
        String IPaddress = "";
    try {
        InetAddress thisIp = InetAddress.getLocalHost();
        IPaddress = thisIp.getHostAddress();
        }
    catch(Exception e) {
        e.printStackTrace();
        }
        return IPaddress;
    }

    public String generateTrackerAdvertisment(int openPort){
        String advertisement = "";
        String connectedPeers = "0";
        String maxPeers = "100";
        advertisement += getIPaddress() + SPECIAL_SEPARATOR;
        advertisement += getTrackerName() + SPECIAL_SEPARATOR;
        advertisement += String.valueOf(openPort) + SPECIAL_SEPARATOR;
        advertisement += connectedPeers + SPECIAL_SEPARATOR;
        advertisement += maxPeers + SPECIAL_SEPARATOR;

        return advertisement;
    }

    public String getTrackerName(){
        //String name = "So many Cats";
        return this.name;
    }

    public void setTrackerName(String name){
        this.name = name;
    }
}
