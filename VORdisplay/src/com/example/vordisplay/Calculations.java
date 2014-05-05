package com.example.vordisplay;


public class Calculations {

	float GetDistance(float time, int speed){
		
		return speed*time;
		
	}
	
	float GetTime(float start, float end){
		
		return end - start;
	
	}
	
	double GetAngle(float x, float y){
		
		return Math.toDegrees(Math.atan(y/x));
	
	}
	
	int LawOfCos(double e, double d, int angle){

		return (int) Math.sqrt(Math.pow(e, 2) + Math.pow(d, 2) - 2*e*d*Math.cos(Math.toRadians(angle)));
	
	}
}
