package com.example.bankanalysis.model;

public class DataQualityReport {
    private boolean balanceContinuous;
    private int discontinuousPoints;
    private String balanceCheckDetail;
    private double benfordChiSquare;
    private boolean benfordPass;
    private String benfordDetail;

    public boolean isBalanceContinuous() { return balanceContinuous; }
    public void setBalanceContinuous(boolean balanceContinuous) { this.balanceContinuous = balanceContinuous; }

    public int getDiscontinuousPoints() { return discontinuousPoints; }
    public void setDiscontinuousPoints(int discontinuousPoints) { this.discontinuousPoints = discontinuousPoints; }

    public String getBalanceCheckDetail() { return balanceCheckDetail; }
    public void setBalanceCheckDetail(String balanceCheckDetail) { this.balanceCheckDetail = balanceCheckDetail; }

    public double getBenfordChiSquare() { return benfordChiSquare; }
    public void setBenfordChiSquare(double benfordChiSquare) { this.benfordChiSquare = benfordChiSquare; }

    public boolean isBenfordPass() { return benfordPass; }
    public void setBenfordPass(boolean benfordPass) { this.benfordPass = benfordPass; }

    public String getBenfordDetail() { return benfordDetail; }
    public void setBenfordDetail(String benfordDetail) { this.benfordDetail = benfordDetail; }
}
