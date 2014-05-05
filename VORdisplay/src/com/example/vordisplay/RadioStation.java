package com.example.vordisplay;

import java.util.Random;

import android.R.string;

public class RadioStation {
	
	public int Random(){
		Random r = new Random();
		return r.nextInt(9);
	}
	
	public String IntToMorse(int rand){
		
		if(rand == 0)
			return "-----";
		else if(rand == 1)
			return ".----";
		else if(rand == 2)
			return "..---";
		else if(rand == 3)
			return "...--";
		else if(rand == 4)
			return "....-";
		else if(rand == 5)
			return ".....";
		else if(rand == 6)
			return "-....";
		else if(rand == 7)
			return "--...";
		else if(rand == 8)
			return "---..";
		else 
			return "----.";
		
	}
	
	public String CreateMorse(){
		
		return IntToMorse(Random()) + " " + IntToMorse(Random()) + " " +IntToMorse(Random());
	}

	public int CreateRadial(){
		
		Random r = new Random();
		return r.nextInt(359);
	}
	
	public long CreateDistance(){
		
		Random r = new Random();
		return r.nextInt(1000000 - 10000) + 10000;
		
	}
}
