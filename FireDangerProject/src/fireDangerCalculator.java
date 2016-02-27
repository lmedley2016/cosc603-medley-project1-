

/** This program calculates the Fire Danger Rating Indexes as part of the National Fire Danger
 * Rating system that is widely used by most federal and state forest fire control agencies.
 * The original subroutine was written in FORTRAN and the following is an attempt to re-engineer
 * the code and convert it to Java for portability and maintainability.
 */

/**
 * @author Lauren Medley
 * @version 1.0
 */

// TODO: Auto-generated Javadoc
/**
 * The Class fireDangerCalculator.  The following are data returned from calling the constructor.  The values are as 
 * follows:
 * 
 * df - Drying Factor
 * ffm - Fine Fuel Moisture
 * adfm - Adjusted (10-day lag) Fuel Moisture
 * grass - Grass Spread Index
 * timber - Timber Spread Index
 * fload - Fire Load Rating (man-hour base)
 * bui - Build Up Index
 * 
 */

public class fireDangerCalculator {
	
	
	
	double df, // Drying Factor
	ffm, // Fine Fuel Moisture
	adfm, // Adjusted (10-day lag) Fuel Moisture
	grass, // Grass Spread Index
	timber, // Timber Spread Index
	fload, // Fire Load Rating (man-hour base)
	bui;  	// Build Up Index (today's)
	
	/** Yesterday's Build Up. */
	double buo;
	
	/** the precipitation. */
	double precip;
	
	/**
	 * The following are table values used in computing the Danger Ratings.  A and B are
	 * piecewise regression coefficients used to determine ffm.
	 * 
	 */
	
	/** The values of coefficient A. */
	double[] coeffA = {-0.185900, -0.85900, -0.059660, -0.077373};
	
	/** The values of coefficient B */
	double[] coeffB = {30.0, 19.2, 13.8, 22.5};
	
	/** The values of coefficient C. */
	double[] coeffC = {4.5, 12.5, 27.5};
	
	/** The values of coefficient D. */
	double[] coeffD = {16.0, 10.0, 7.0, 5.0, 4.0, 3.0};
	
	/**
	 * Instantiates a new fire danger calculator.  The following are input values needed 
	 * to calculate the National Fire Danger Ratings and Fire Load Index 
	 *
	 * @param dryTemp the dry bulb temperature
	 * @param wetTemp the wet bulb temperature
	 * @param isSnow the boolean variable to indicate if there is snow on the ground
	 * @param precip the precipitation
	 * @param windSpeed the current wind speed in miles per hour
	 * @param buo the last value of the build up index
	 * @param iHerb the current herb state 1=cured, 2=transition, 3=green
	 */
	
	public fireDangerCalculator(double dryTemp, double wetTemp, boolean isSnow, double precip, 
			double windSpeed, double buo, int iHerb) { 
		// Initializations of Drying Factor, Fine Fuel Moisture, Adjusted Fuel Moisture, Grass Index,
		// Timber Index and yesterday's Build Up Index
		this.df = 0.0;
		this.ffm = 99.0;
		this.adfm = 99.0;
		this.grass = 0.0;
		this.timber = 0.0;
		this.fload = 0.0;
		this.buo = buo;
		
		if (isSnow) { // boolean values isSnow is true indicates there is some snow on the ground
			grass = 0.0;	// set both grass and timber indexes to be zero
			timber = 0.0; 
			adjustBuildUpIndex(precip);  // call adjustBuildUpIndex to account for precipitation
		} else { // Calculate ffm, df, adjust Fine Fuel for herb stage, adjust bui based on rain precipitation,
			     // adjust buo with df, calculate the adjusted fuel moisture (adfm), adjust the grass and 
				 // timber indexes based on wind speed.  Finally, calculate the fire load (Fload).
			calculateFineFuelMoisture(dryTemp, wetTemp); // adjust ffm based on dry and wet bulb temps
			calculateDryingFactor();
			adjustFfmWithHerb(iHerb);
			adjustBuildUpIndex(precip);
			adjustBuoWithDryingFactor();
			calculateAdfm();
			adjustGrassTimberIndex(windSpeed);
			if (timber > 0.0 && bui > 0.0) {
				calculateFload();
			}
		}
				
	}	
	
	/**
	 * This method calculates the Build Up Index based on yesterday's build up.
	 * The Build Up Index is adjusted when precipitation exceeds 0.1 inches.
	 * 
	 * Precipitation is the past 24 hours precipitation in inches and hundredths.
	 *
	 * @param precip the precipitation in the past 24 hours
	 */	
	
	public void adjustBuildUpIndex(double precip) {
		if (precip > 0.1) {  
			 bui = -50.0 * ((Math.log((1.0 - (-1.0 * Math.exp(buo / 50))))) * Math.exp(1.175) * precip);  
			 if (bui < 0.0) {  
				 bui = 0.0;  
			 }  
		}  
	}  
	
	/**
	 * This method calculates the Fine Fuel Moisture (ffm).  The depression of
	 * the wet bulb (dif = dryTemp - wetTemp) is used to decide which set of piecewise
	 * regression coefficients (which set of A and B) will be used.
	 * 
	 * @param dryTemp the dry bulb temperature
	 * @param wetTemp the wet bulb temperature
	 */
	
	public void calculateFineFuelMoisture(double dryTemp, double wetTemp) {
		
		double diff = dryTemp - wetTemp; // Difference between dry and wet bulb temperatures
		double a = 0.0; // Initialize value of A
		double b = 0.0; // Initialize value of B
		
		if (diff < 4.5) {  // Comparing the difference between dryTemp and wetTemp with coeffC[1]
			a = coeffA[0];
			b = coeffB[0];
		} else if (diff < 12.5) {  // Comparing the difference between dryTemp and wetTemp with coeffC[2]
			a = coeffA[1];
			b = coeffB[1];
		} else if (diff < 27.5) {  // Comparing the difference between dryTemp and wetTemp with coeffC[3]
			a = coeffA[2];
			b = coeffB[2];
		} else {	// Set a and b to values of last elements of coeffA[] and coeffB[] arrays
			a = coeffA[3];
			b = coeffB[3];
		}
		
		ffm = b * Math.exp(a) * diff; 		
		
	}	
	
	/**
	 * This method calculates the Drying Factor based on values of coeffD[].
	 */
	public void calculateDryingFactor() {
		
		for (int i = 1; i<=6; i++) {
			if (ffm - coeffD[i-1] > 0) {
				df = i -1;
				return;
			}
		}
		df = 7.0;  // Setting the Drying Factor to be 7
		
	}
	
	/**
	 * This method adjusts the Fine Fuel Moisture based on the current herb stage.
	 * The Fine Fuel Moisture is added 5% for each herb stage greater than one.
	 * 
	 * @param iHerb the current herb state 1=cured, 2=transition, 3=green
	 */
	
	public void adjustFfmWithHerb(int iHerb) {
		
		if (ffm <= 1.0) {
			ffm = 1.0;
		} else {
			ffm = ffm + (iHerb - 1) * 5;  // Adding 5 percent for each herb state greater than 1
		}
	}
	
	/**
	 * Adding the Drying Factor to today's Build Up after correction for rain is adjusted.
	 * 
	 */
	
	public void adjustBuoWithDryingFactor() {
		
		bui = buo + df;
	}
	
	/**
	 * This method calculates the Adjusted Fuel Moisture based on the current
	 * Fine Fuel Moisture with today's Build Up Index.
	 * 
	 */
	
	public void calculateAdfm() {
		
		adfm = 0.9 * ffm + 0.5 + 9.5 * Math.exp(-1.0 * bui / 50);
		
	}
	
	/**
	 * This method adjusts the Grass and Timber Indexes based on wind speed and the 
	 * Adjusted Fuel Moisture.  There are two conditions that are being checked for.  The first 
	 * condition is the adfm.  If it is greater than 30%, the Grass and Timber indexes will be set 
	 * to 1.  The second condition is the wind speed.  If it is greater than 14 Mph, a different 
	 * formula will be used to adjust the Grass and Timber indexes.
	 * 
	 * @param windSpeed the current wind speed in miles per hour
	 */
	
	public void adjustGrassTimberIndex(double windSpeed) {
		
		if (adfm >= 30.0 && ffm >= 30.0) {
			
			grass = 1.0;
			timber = 1.0;
		} else {
			if (windSpeed >= 14.0 && grass <= 0.0 && timber < 99.0) {
				grass = 0.00918 * (windSpeed + 14.0) * (33.0 - adfm) * 1.65 - 3;  
				timber = 0.00918 * (windSpeed + 14.0) * (33.0 - adfm) * 1.65 - 3;
				}  
			if (grass > 99.0 && timber > 99.0) {
				grass = 99.0; 
				timber = 99.0;  
			} 
			else {  
				grass = 0.01312 * (windSpeed + 6.0) * (33.0 - adfm) * 1.65 - 3;
				timber = 0.01312 * (windSpeed + 6.0) * (33.0 - adfm) * 1.65 - 3;
			}  
			if (timber <= 0.0) {
				timber = 1.0;  
			}  
			if (grass < 0.0) {  
				grass = 1.0;  
			}  
			if (grass == 0) {  
				grass = 0;  
			}  
		}  
		
	}
	
	/**
	 * This method calculates the Fire Load Index when both Timber Spread and Build Up
	 * Indexes are zero.  Ensure that Fload is greater than zero, otherwise set it to zero.
	 */
	
	public void calculateFload() {
		 
			 if (timber > 0.0 && bui > 0.0) {  
				 fload = 1.75 * Math.log10(timber) + 0.32 * Math.log10(bui) - 1.640;  
				 if (fload < 0.0) {  
					 fload = 0.0;  
				 } else {  
					 fload = 10.0 * fload;  
				 }
			 }
			 else if (timber == 0.0 && bui == 0.0) {
				 fload = 0.0;  
			 }  
	}
	
	/**
	 * Main method to test all values in calculating the Fire Danger Ratings.
	 *
	 * @param args the arguments
	 */

	public static void main(String args[]){
		
		// Instantiate a new fireDangerCalculator to test all values from input values
		
		fireDangerCalculator fdc = new fireDangerCalculator(32, 12, false, 0.5, 20, 2, 1);  
		
		/**
		 * The following values are calculated from the given input values for dryTemp, wetTemp,
		 * isSnow, precip, windSpeed, buo (yesterday's build up), and iHerb (current herb stage)
		 */
		
		System.out.println("The following are values calculated from the fireDangerCalculator:\n");
		System.out.println("------------------------------------------------------------------\n");		
		System.out.println("The Drying Factor is: " + fdc.df);  
		System.out.println("The Fine Fuel Moisture is: " + fdc.ffm);  
		System.out.println("The Adjusted (10-day lag) Fuel Moisture is: " + fdc.adfm);  
		System.out.println("The Grass Spread Index is: " + fdc.grass);  
		System.out.println("The Timber Spread Index is: " + fdc.timber);  
		System.out.println("The Fire Load Rating (man-hour base) is: " + fdc.fload);  
		System.out.println("The Build Up Index is: " + fdc.bui);
		System.out.println("------------------------------------------------------------------\n");		
		
	}
		 	  		
}


		
		
		
		
		
		
		
		
		
		
		
		
		
	
