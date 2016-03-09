import java.io.*;

/**
 * Created by Michael on 3/2/2016.
 *
 */
public class ClientObject implements Serializable{
    private String IP_Address;
    private String port;
    private static final String SPECIAL_BREAK_SYMBOL = "'#";

    public ClientObject(String ip, String port){
        this.IP_Address = ip;
        this.port = port;
    }

    public String get_IP_Address(){
        return this.IP_Address;
    }

    public String get_Client_info(){
        String info = this.IP_Address;
        info += SPECIAL_BREAK_SYMBOL;
        info += port;

        return info;
    }

    public int getPort(){
        return Integer.parseInt(this.port);
    }
}
