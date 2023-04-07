import lombok.extern.slf4j.Slf4j;
import org.pcap4j.core.PcapPacket;

import java.util.Optional;
@Slf4j
public class SvDecoder {
    private static final int datasetSize = 64;
    public Optional<SvPacket> decode(PcapPacket packet){
        try {
            byte[] data = packet.getRawData();
            int length = data.length;
            SvPacket result = new SvPacket();

            result.setMacDst(byteArrayToMac(data, 0));
            result.setMacSrc(byteArrayToMac(data, 6));
            result.setPackType(byteArrayToType(data, 12));
            result.setAppID(data[14]);
            result.setSvID(byteArrayToSvID(data,33));
            result.setSmpCount(data[46] & 0xFF | (data[45] & 0xFF) << 8);
            result.setConfRev(byteArrayToInt(data, 49));
            result.setSmpSynch(data[55]);
            result.getDataset().setInstIa(byteArrayToInt(data, 58)/100.0);
            result.getDataset().setInstIb(byteArrayToInt(data, 66)/100.0);
            result.getDataset().setInstIc(byteArrayToInt(data, 74)/100.0);
            result.getDataset().setInstIn(byteArrayToInt(data, 82)/100.0);
            result.getDataset().setInstUa(byteArrayToInt(data, 90)/1000.0);
            result.getDataset().setInstUb(byteArrayToInt(data, 98)/1000.0);
            result.getDataset().setInstUc(byteArrayToInt(data, 106)/1000.0);
            result.getDataset().setInstIn(byteArrayToInt(data, 114)/1000.0);
            result.getDataset().setQIa(byteArrayToInt(data, 62));
            result.getDataset().setQIb(byteArrayToInt(data, 70));
            result.getDataset().setQIc(byteArrayToInt(data, 78));
            result.getDataset().setQIn(byteArrayToInt(data, 86));
            result.getDataset().setQUa(byteArrayToInt(data, 94));
            result.getDataset().setQUb(byteArrayToInt(data, 102));
            result.getDataset().setQUc(byteArrayToInt(data, 110));
            result.getDataset().setQUn(byteArrayToInt(data, 118));


            /*System.out.println(result.getMacDst());
            System.out.println(result.getMacSrc());
            System.out.println();*/

            return Optional.of(result);
        }catch (Exception e){
            log.error("Cannot parse sv packet");
        }
        return Optional.empty();

    }

    public static String byteArrayToMac(byte[] b, int offset) {
        return String.format("%02x:%02x:%02x:%02x:%02x:%02x",
                b[offset],
                b[1 + offset],
                b[2 + offset],
                b[3 + offset],
                b[4 + offset],
                b[5 + offset]
        );
    }
    public static String byteArrayToSvID(byte[] b, int offset) {
        return String.format("%c%c%c%c%c%c%c%c%c%c",
                b[offset],
                b[1 + offset],
                b[2 + offset],
                b[3 + offset],
                b[4 + offset],
                b[5 + offset],
                b[6 + offset],
                b[7 + offset],
                b[8 + offset],
                b[9 + offset]
        );
    }
    public static String byteArrayToType(byte[] b, int offset) {
        return String.format("0x%02x%02x",
                b[offset],
                b[1 + offset]
        );
    }
    public static int byteArrayToInt(byte[] b, int offset){
        return b[offset + 3] & 0xFF | (b[offset + 2 ] & 0xFF) << 8 | (b[offset + 1] & 0xFF) << 16 | (b[offset] & 0xFF) << 24;
    }
    public static short byteArrayToShort(byte[] b, int offset){
        return (short) (b[offset] & 0xF);
    }
}
