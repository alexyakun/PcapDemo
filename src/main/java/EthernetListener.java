import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.core.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j

public class EthernetListener {
    static{
        try {
            for (PcapNetworkInterface nic : Pcaps.findAllDevs()) log.info("Found NIC: {}", nic);
        } catch (PcapNativeException e) {throw new RuntimeException(e);}
    }
    @Setter
    private String nickName;
    private PcapHandle handle; // обработчик сетевых карт
    private final List<PacketListener> listeners = new CopyOnWriteArrayList<>();
    // сюда будут приходить пакеты
    //asdf
    private final PacketListener defaultPacketLister = packet -> {
        listeners.forEach(listener ->listener.gotPacket(packet));
    };

    @SneakyThrows
    public void start(){
        if(handle ==null){
            initializeNetworkInterface();
        }
        if(handle !=null){
            //отбирает только SV-пакеты с определенным макадресом назначения.
            String filter = "ether proto 0x88ba && ether dst 01:0C:CD:04:00:01";
            handle.setFilter(filter, BpfProgram.BpfCompileMode.OPTIMIZE);
            // создаем отдельный поток для приема пакетов
            Thread captureThread = new Thread(() -> {

                try {
                    log.info("Starting packet capture");
                    handle.loop(0, defaultPacketLister);
                } catch (PcapNativeException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (NotOpenException e) {
                    throw new RuntimeException(e);
                }
                log.info("Packet capture finished");

            });
            captureThread.start();

        }
    }

    @SneakyThrows
    private void initializeNetworkInterface() {
        Optional<PcapNetworkInterface> nic = Pcaps.findAllDevs().stream()
                .filter(i -> nickName.equals(i.getDescription()))
                .findFirst();
        if(nic.isPresent()){
            // Promiscuous пропускает все пакеты, даже если они не предназначены
            // для данной сетевой карты
            handle = nic.get().openLive(1500, PcapNetworkInterface.PromiscuousMode.NONPROMISCUOUS,10);
            log.info("Network handler created: {}", nic);
        }
        else{
            log.error("Network interface is not found");
        }
    }

    public void addListener(PacketListener listener){
        listeners.add(listener);
    }
}
