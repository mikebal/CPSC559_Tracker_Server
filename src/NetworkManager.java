import java.net.InetAddress;

/**
 * Created by Michael on 2/25/2016.
 */
public class NetworkManager {

    public static final String SPECIAL_SEPARATOR = "'#";

    public String getIPaddress(){
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
        String advertisment = "";
        String connectedPeers = "0";
        String maxPeers = "100";
        advertisment += getIPaddress() + SPECIAL_SEPARATOR;
        advertisment += getTrackerName() + SPECIAL_SEPARATOR;
        advertisment += String.valueOf(openPort) + SPECIAL_SEPARATOR;
        advertisment += connectedPeers + SPECIAL_SEPARATOR;
        advertisment += maxPeers + SPECIAL_SEPARATOR;

        return advertisment;
    }

    public String getTrackerName(){
        String name = "So many Cats";
        return name;
    }
}
