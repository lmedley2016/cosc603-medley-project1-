
/* This program calculates the Fire Danger Rating Indexes as part of the National Fire Danger
 * Rating system that is widely used by most federal and state forest fire control agencies.
 * The original subroutine was written in FORTRAN and the following is an attempt to re-engineer
 * the code and convert it to Java for portability and maintainability.
 */
// Import statements to show what utilities are being used in the class
import java.io.*;
import java.util.*;
public class fireDangerCalculator {
	public fireDangerCalculator (double dry, double wet, boolean isnow, double precipt, 
			double windspeed, double buo, int iherb){ 
		
		
		
		
		
		
		
	}
	
	public static void main(String[] args) { 
		
		double FFM = 99.0;	// Fine Fuel Moisture Index
		double ADFM = 99.0;	// Adjusted (10-day lag) Fuel Moisture
		double DF = 0.0;	// Drying Factor
		double FLOAD = 0.0;	// Fire Load Rating (Man-hour base)
		double grassIndex;
		double timberIndex;
		double precip;
		
		// The following are table values used in computing the Danger Ratings.  A and B are
		// piecewise regression coefficients used to determine FFM.
		
		double[] coeffA = {-0.185900, -0.85900, -0.059660, -0.077373};
		double[] coeffB = {30.0, 19.2, 13.8, 22.5};
		double[] coeffC = {4.5, 12.5, 27.5};
		double[] coeffD = {16.0, 10.0, 7.0, 5.0, 4.0, 3.0};
		
		boolean isSnow = false;	// isSnow is a boolean value to test if snow is present
		
		if (isSnow) {
			// If snow is on the ground and the timber and grass spread indexes must be set 
			// to 0. Build up will be adjusted for precipitation.
			grassIndex = 0.0;
			timberIndex = 0.0;
			
			if (precip - 0.1) {
				
			}
		}
		
		
		
	}
	
	// Calculating the Fine Fuel Moisture (FFM) Index
		public static double FFMCalculator
}
}
