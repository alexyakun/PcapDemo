import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class SvPacket {
    private String macDst;

    private String macSrc;
    private String packType;

    private short appID;

    private String svID;

    private int smpCount;

    private int confRev;

    private int smpSynch;

    @Override
    public String toString() {
        return "{macDst='" + macDst + '\'' +
                ", macSrc='" + macSrc + '\'' +
                ", packType='" + packType + '\'' +
                ", appID=" + appID +'\n'+
                "svID='" + svID + '\'' +
                ", smpCount=" + smpCount +
                ", confRev=" + confRev +
                ", smpSynch=" + smpSynch +
                '\n' + dataset+"}"+'\n';
    }

    private Dataset dataset = new Dataset();


    @Getter @Setter
    public class Dataset {
        private double instIa;
        private int qIa;
        private double instIb;
        private int qIb;
        private double instIc;
        private int qIc;
        private double instIn;
        private int qIn;
        private double instUa;
        private int qUa;
        private double instUb;
        private int qUb;
        private double instUc;
        private int qUc;
        private double instUn;
        private int qUn;
        @Override
        public String toString() {
            return "instIa=" + instIa +
                    ", qIa=" + qIa +
                    ", instIb=" + instIb +
                    ", qIb=" + qIb +
                    ", instIc=" + instIc +
                    ", qIc=" + qIc +
                    ", instIn=" + instIn +
                    ", qIn=" + qIn + '\n'+
                    "instUa=" + instUa +
                    ", qUa=" + qUa +
                    ", instUb=" + instUb +
                    ", qUb=" + qUb +
                    ", instUc=" + instUc +
                    ", qUc=" + qUc +
                    ", instUn=" + instUn +
                    ", qUn=" + qUn;
        }

    }




}
