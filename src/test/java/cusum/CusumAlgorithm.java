package cusum;

import org.junit.Test;

public class CusumAlgorithm {

    public int getCurrentValue() {
        return (int) (Math.random() * 10);
    }

    public int min(int val1, int val2) {
        return val1 < val2 ? val1 : val2;
    }


    public int max(int val1, int val2) {
        return val1 > val2 ? val1 : val2;
    }

    @Test
    public void testImplementation() {
        //target m
        int median = 5;
        int upperBoundElasticity = 3, lowerBoundElasticity = 3;
        int upperThreshold = 9, lowerThreshold = 1;
        int positiveCusum = 0, negativeCusum = 0, t = 1;

        while (true) {
            int upperBoundSignal = 0, lowerBoundSignal = 0;
            int x = getCurrentValue();
            positiveCusum = max(0, x - (median + upperBoundElasticity) + positiveCusum);
            negativeCusum = min(0, x - (median - lowerBoundElasticity) + negativeCusum);
            if (positiveCusum - upperThreshold > 0) {
                upperBoundSignal = 1;
                positiveCusum = 0;
                negativeCusum = 0;
                //upper threshold breached here
            }
            if (negativeCusum - lowerThreshold < 0) {
                lowerBoundSignal = 1;
                positiveCusum = 0;
                negativeCusum = 0;
                //lower threshold breached here
            }
            t++;
        }
    }


    @Test
    public void what() {
        double positiveCusum = 0;
        double negativeCusum = 0;
        double threshold = 11;
        while (true) {

            //Get residual
            double current_value = getCurrentDoubleValue();
            double drift = 0.5;
            //normalize residual, but avoid division by zero. Cause that's bad.

            //CUSUM algorithm performed on residuals
            positiveCusum = Math.max(0, positiveCusum + drift + current_value);
            negativeCusum = Math.max(0, Math.abs(negativeCusum + drift - current_value));/* Abs might not be needed -->*/

            if (positiveCusum > threshold || negativeCusum > threshold) {

                System.out.println("breach of thresholds! Positive Cusum: " + positiveCusum + " , negative: " + negativeCusum  );
            }
        }
    }


    private double getCurrentDoubleValue() {
        return Math.random() * 10;
    }


}
