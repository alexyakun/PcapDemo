import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        EthernetListener ethernetListener = new EthernetListener();
        ethernetListener.setNickName("Realtek PCIe GbE Family Controller");
        SvDecoder svDecoder = new SvDecoder();
        Saver saver = new Saver();
        int numPacket = 0;
        double svTime = 0;
        int checkCount = 0;
        List<SvPacket> smallPacket = new ArrayList<>();
        ethernetListener.addListener(packet ->{
            Optional<SvPacket> svPacket = svDecoder.decode(packet);
            if(svPacket.isPresent()){

                saver.savePacket(svPacket.get());
            }
        });
        ethernetListener.start();

    }
}
