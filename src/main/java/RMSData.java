import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RMSData {
    private double Ia;
    private double Ib;
    private double Ic;
    private double In;
    private double Ua;
    private double Ub;
    private double Uc;
    private double Un;

    private double rmsTime;

    @Override
    public String toString() {
        return "RMSData{" +
                "Ia=" + Ia +
                ", Ib=" + Ib +
                ", Ic=" + Ic +
                ", Ua=" + Ua +
                ", Ub=" + Ub +
                ", Uc=" + Uc +
                ", rmsTime=" + rmsTime +
                '}';
    }
}
