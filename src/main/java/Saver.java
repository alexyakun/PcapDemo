import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.round;

public class Saver {
    List<Data> smallPacket = new ArrayList<>(); // храним данные за период
    List<Data> bigPacket = new ArrayList<>(); // храним RMS значения
    FaultStatus faultStatus = new FaultStatus(false, false,false,false,false);
    private double timeFault = 0;
    private int numPacket = 0;
    private int sumPacket = 0;
    private double svTime = 0;
    private int checkCount = 0;


    public void savePacket(SvPacket svPacket){
        if(svPacket.getSmpCount() == checkCount + 1 || checkCount == 0) {
            sumPacket+=1;
            checkCount = svPacket.getSmpCount();
            Data svData = new Data();
            svData.setSv_time(svTime);
            svTime += 0.02 / 80;
            svData.setSvPacket(svPacket);
            if (numPacket < 80) {
                smallPacket.add(svData);
                numPacket+=1;
            } else {
                numPacket = 0;
                RMSData rmsData = miniParser(smallPacket);
                if(!faultStatus.isHasStatus()){
                    System.out.println(svPacket);
                    faultStatus.setHasStatus(true);
                    faultStatus.setNormalCurrent(rmsData.getIa());
                    faultStatus.setNormalVoltage(rmsData.getUa());
                    SayStatus();
                }
                faultChecker(rmsData);
                //System.out.println(rmsData);

                if(faultStatus.isFault()){
                    timeFault += 0.02;

                }
                smallPacket.clear();
                //System.out.println(rmsData);
            }
        }
        else if (svPacket.getSmpCount() != checkCount){
            //System.out.println("Count "+ checkCount);
            //System.out.println("Пакетов получено "+sumPacket);
            checkCount = svPacket.getSmpCount();
            if(!faultStatus.isFault()){
                SayStatus();
            }

            savePacket(svPacket);
        }
    }
    private RMSData miniParser(List<Data> sPacket){
        RMSData rmsData = new RMSData();
        rmsData.setRmsTime(sPacket.get(0).getSv_time());
        rmsData.setIa(RMS(sPacket.stream()
                .map(s ->s.getSvPacket().getDataset().getInstIa())
                .collect(Collectors.toList()),0.02));
        rmsData.setIb(RMS(sPacket.stream()
                .map(s ->s.getSvPacket().getDataset().getInstIb())
                .collect(Collectors.toList()),0.02));
        rmsData.setIc(RMS(sPacket.stream()
                .map(s ->s.getSvPacket().getDataset().getInstIc())
                .collect(Collectors.toList()),0.02));
        rmsData.setIn(RMS(sPacket.stream()
                .map(s ->s.getSvPacket().getDataset().getInstIn())
                .collect(Collectors.toList()),0.02));
        rmsData.setUa(RMS(sPacket.stream()
                .map(s ->s.getSvPacket().getDataset().getInstUa())
                .collect(Collectors.toList()),0.02));
        rmsData.setUb(RMS(sPacket.stream()
                .map(s ->s.getSvPacket().getDataset().getInstUb())
                .collect(Collectors.toList()),0.02));
        rmsData.setUc(RMS(sPacket.stream()
                .map(s ->s.getSvPacket().getDataset().getInstUc())
                .collect(Collectors.toList()),0.02));
        rmsData.setUn(RMS(sPacket.stream()
                .map(s ->s.getSvPacket().getDataset().getInstUn())
                .collect(Collectors.toList()),0.02));
        return rmsData;
    }
    private double RMS(List<Double> current, double time){
        int lenI = current.size();
        double delta = time / lenI;
        double sum = 0;
        for(double el : current) {
            sum+= el*el*delta;
        }
        return Math.sqrt(sum/time);
    }
    private void SayStatus(){
        System.out.println("Ток в нормальном режиме "+String.format("%.2f",faultStatus.getNormalCurrent())+" A");
        System.out.println("Напряжение в нормальном режиме - "+String.format("%.2f",faultStatus.getNormalVoltage())+" В");
    }
    private void SayFaultStatus(double timeFault){
        String stimeFault = String.format("%.2f",timeFault);
        if(faultStatus.isFaultA() && !faultStatus.isFaultB()&& !faultStatus.isFaultC()){
            System.out.println("Однофазное короткое замыкание в фазе А, длительностью "+stimeFault+" c");

        }
        if(!faultStatus.isFaultA()&& faultStatus.isFaultB()&& !faultStatus.isFaultC()){
            System.out.println("Короткое замыкание в фазе B, длительностью "+stimeFault+" c");
        }
        if(!faultStatus.isFaultA()&& !faultStatus.isFaultB()&& faultStatus.isFaultC()){
            System.out.println("Короткое замыкание в фазе C, длительностью "+stimeFault+" c");
        }
        if(faultStatus.isFaultA()&& faultStatus.isFaultB()&& !faultStatus.isFaultC()){
            System.out.println("Двухфазное короткое замыкание в фазах А и B, длительностью "+stimeFault+" c");
        }
        if(faultStatus.isFaultA()&& !faultStatus.isFaultB()&& faultStatus.isFaultC()){
            System.out.println("Двухфазное короткое замыкание в фазах А и C, длительностью "+stimeFault+" c");
        }
        if(!faultStatus.isFaultA()&& faultStatus.isFaultB()&& faultStatus.isFaultC()){
            System.out.println("Двухфазное короткое замыкание в фазах B и C, длительностью "+stimeFault+" c");
        }
        if(faultStatus.isFaultA()&& faultStatus.isFaultB()&& faultStatus.isFaultC()){
            System.out.println("Трехфазное короткое замыкание, длительностью "+stimeFault+" c");
        }
        System.out.println(faultStatus);

    }
    private void faultChecker(RMSData rmsData){

        if(rmsData.getIa() < 2*faultStatus.getNormalCurrent()&&rmsData.getIa() < 2*faultStatus.getNormalCurrent()&&rmsData.getIa() < 2*faultStatus.getNormalCurrent()){
            if(timeFault!=0){
                SayFaultStatus(timeFault);
                timeFault = 0;
                faultStatus.setFaultCurrentA(0);
                faultStatus.setFaultCurrentB(0);
                faultStatus.setFaultCurrentC(0);
                faultStatus.setFaultVoltageA(0);
                faultStatus.setFaultVoltageB(0);
                faultStatus.setFaultVoltageC(0);

            }
            faultStatus.setFault(false);
            faultStatus.setNormalCurrent(rmsData.getIa());
            faultStatus.setNormalVoltage(rmsData.getUa());
        }
        if(rmsData.getIa() > 2*faultStatus.getNormalCurrent()){
            if(!faultStatus.isFaultA()){
                System.out.println("Начало кз в фазе А");
                faultStatus.setFaultCurrentA(rmsData.getIa());
                faultStatus.setFaultCurrentB(rmsData.getIb());
                faultStatus.setFaultCurrentC(rmsData.getIc());
                faultStatus.setFaultVoltageA(rmsData.getUa());
                faultStatus.setFaultVoltageB(rmsData.getUb());
                faultStatus.setFaultVoltageC(rmsData.getUc());
            }
            if(rmsData.getIa()>faultStatus.getFaultCurrentA()){
                faultStatus.setFaultCurrentA(rmsData.getIa());
            }
            faultStatus.setFaultA(true);
            faultStatus.setFault(true);
        } else{
            faultStatus.setFaultA(false);
        }
        if(rmsData.getIb() > 2*faultStatus.getNormalCurrent()){
            if(!faultStatus.isFaultB()){
                System.out.println("Начало кз в фазе B");
                faultStatus.setFaultCurrentA(rmsData.getIa());
                faultStatus.setFaultCurrentB(rmsData.getIb());
                faultStatus.setFaultCurrentC(rmsData.getIc());
                faultStatus.setFaultVoltageA(rmsData.getUa());
                faultStatus.setFaultVoltageB(rmsData.getUb());
                faultStatus.setFaultVoltageC(rmsData.getUc());
            }
            if(rmsData.getIb()>faultStatus.getFaultCurrentB()){

                faultStatus.setFaultCurrentB(rmsData.getIb());

            }
            faultStatus.setFaultB(true);
            faultStatus.setFault(true);
        }else{
            faultStatus.setFaultB(false);
        }
        if(rmsData.getIc() > 2*faultStatus.getNormalCurrent()){
            if(!faultStatus.isFaultC()){
                System.out.println("Начало кз в фазе C");
                faultStatus.setFaultCurrentA(rmsData.getIa());
                faultStatus.setFaultCurrentB(rmsData.getIb());
                faultStatus.setFaultCurrentC(rmsData.getIc());
                faultStatus.setFaultVoltageA(rmsData.getUa());
                faultStatus.setFaultVoltageB(rmsData.getUb());
                faultStatus.setFaultVoltageC(rmsData.getUc());
            }
            if(rmsData.getIc()>faultStatus.getFaultCurrentC()){
                faultStatus.setFaultCurrentC(rmsData.getIc());
            }
            faultStatus.setFaultC(true);
            faultStatus.setFault(true);
        }else{
            faultStatus.setFaultC(false);
        }


    }

}
