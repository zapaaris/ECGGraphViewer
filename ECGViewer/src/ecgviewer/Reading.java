package ecgviewer;

public class Reading {
private int id;
private String title;
private double start_time;
private double end_time;
private double assigned_duration;
private double calculated_duration;
private double I;
private double II;
private double III;
private double aVR;
private double aVL;
private double aVF;
private double V1;
private double V2;
private double V3;
private double V4;
private double V5;
private double V6;
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getTitle() {
	return title;
}
public void setTitle(String title) {
	this.title = title;
}
public double getStart_time() {
	return start_time;
}
public void setStart_time(double start_time) {
	this.start_time = start_time;
}
public double getEnd_time() {
	return end_time;
}
public void setEnd_time(double end_time) {
	this.end_time = end_time;
}
public double getAssigned_duration() {
	return assigned_duration;
}
public void setAssigned_duration(double assigned_duration) {
	this.assigned_duration = assigned_duration;
}
public double getCalculated_duration() {
	return calculated_duration;
}
public void setCalculated_duration(double calculated_duration) {
	this.calculated_duration = calculated_duration;
}
public double getI() {
	return I;
}
public void setI(double i) {
	I = i;
}
public double getII() {
	return II;
}
public void setII(double iI) {
	II = iI;
}
public double getIII() {
	return III;
}
public void setIII(double iII) {
	III = iII;
}
public double getaVR() {
	return aVR;
}
public void setaVR(double aVR) {
	this.aVR = aVR;
}
public double getaVL() {
	return aVL;
}
public void setaVL(double aVL) {
	this.aVL = aVL;
}
public double getaVF() {
	return aVF;
}
public void setaVF(double aVF) {
	this.aVF = aVF;
}
public double getV1() {
	return V1;
}
public void setV1(double v1) {
	V1 = v1;
}
public double getV2() {
	return V2;
}
public void setV2(double v2) {
	V2 = v2;
}
public double getV3() {
	return V3;
}
public void setV3(double v3) {
	V3 = v3;
}
public double getV4() {
	return V4;
}
public void setV4(double v4) {
	V4 = v4;
}
public double getV5() {
	return V5;
}
public void setV5(double v5) {
	V5 = v5;
}
public double getV6() {
	return V6;
}
public void setV6(double v6) {
	V6 = v6;
}
public Reading(int id, String title, double start_time, double end_time, double assigned_duration,
		double calculated_duration, double i, double iI, double iII, double aVR, double aVL, double aVF, double v1,
		double v2, double v3, double v4, double v5, double v6) {
	super();
	this.id = id;
	this.title = title;
	this.start_time = start_time;
	this.end_time = end_time;
	this.assigned_duration = assigned_duration;
	this.calculated_duration = calculated_duration;
	I = i;
	II = iI;
	III = iII;
	this.aVR = aVR;
	this.aVL = aVL;
	this.aVF = aVF;
	V1 = v1;
	V2 = v2;
	V3 = v3;
	V4 = v4;
	V5 = v5;
	V6 = v6;
}
public Reading() {
	this.id = 0;
	this.title = "";
	this.start_time = 0.0;
	this.end_time = 0.0;
	this.assigned_duration = 0.0;
	this.calculated_duration = 0.0;
	I = 0.0;
	II = 0.0;
	III = 0.0;
	this.aVR = 0.0;
	this.aVL = 0.0;
	this.aVF = 0.0;
	V1 = 0.0;
	V2 = 0.0;
	V3 = 0.0;
	V4 = 0.0;
	V5 = 0.0;
	V6 = 0.0;
}


}
