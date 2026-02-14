/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxapplication3;

/**
 *
 * @author A
 */
import java.util.concurrent.ThreadLocalRandom;

public class SequentialEulerEstimator implements EulerEstimator {
    
    @Override
    public double estimateEuler(SimulationConfig config) {
        long samples = config.getTotalPoints();
        long totalCount = 0;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        
         
        for (long trial = 0; trial < samples; trial++) {
            double sum = 0.0;
            int count = 0;
            
            while (sum < 1.0) {
                sum += random.nextDouble(); 
                count++;
            }
            
            totalCount += count;
        }
        
        
        return (double) totalCount / samples;
    }
}