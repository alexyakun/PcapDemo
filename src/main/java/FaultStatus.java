import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class FaultStatus {
    private boolean HasStatus;
    private double NormalCurrent;
    private double NormalVoltage;
    private boolean Fault;
    private boolean FaultA;
    private boolean FaultB;

    public FaultStatus(boolean hasStatus, boolean fault, boolean faultA, boolean faultB, boolean faultC) {
        HasStatus = hasStatus;
        Fault = fault;
        FaultA = faultA;
        FaultB = faultB;
        FaultC = faultC;
    }

    private boolean FaultC;
    private double FaultCurrentA;
    private double FaultCurrentB;
    private double FaultCurrentC;
    private double FaultCurrentN;
    private double FaultVoltageA;
    private double FaultVoltageB;
    private double FaultVoltageC;

    @Override
    public String toString() {
        return "Параметры КЗ{" +
                "IA=" + String.format("%.2f",FaultCurrentA) +" А"+
                ", IB=" + String.format("%.2f",FaultCurrentB) +" А"+
                ", IC=" + String.format("%.2f",FaultCurrentC) +" А"+'\n'+
                "UA=" + String.format("%.2f",FaultVoltageA) +" В"+
                ", UB=" + String.format("%.2f",FaultVoltageB) +" В"+
                ", UC=" + String.format("%.2f",FaultVoltageC) +" В"+
                '}'+'\n';
    }

    private double FaultVoltageN;


}
