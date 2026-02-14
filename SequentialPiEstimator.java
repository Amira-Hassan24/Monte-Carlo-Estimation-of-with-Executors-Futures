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

public class SequentialPiEstimator implements PiEstimator {

    @Override
    public double estimatePi(SimulationConfig config) {
        long totalPoints = config.getTotalPoints();
        long insideCircle = 0;

        for (long i = 0; i < totalPoints; i++) {
            double x = ThreadLocalRandom.current().nextDouble();
            double y = ThreadLocalRandom.current().nextDouble();
            if (x*x + y*y <= 1) insideCircle++;
        }

        return 4.0 * insideCircle / totalPoints;
    }
}