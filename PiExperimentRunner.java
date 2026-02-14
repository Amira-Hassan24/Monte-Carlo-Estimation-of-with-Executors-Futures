/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxapplication3;

/**
 *
 * @author A
 */
public class PiExperimentRunner {

    public static Result runExperiment(PiEstimator estimator, SimulationConfig config) {
        long start = System.nanoTime();
        double piEstimate = estimator.estimatePi(config);
        long end = System.nanoTime();
        double error = Math.abs(piEstimate - Math.PI);
        double timeMs = (end - start) / 1_000_000.0;

        return new Result(piEstimate, error, timeMs);
    }

    public static class Result {
        public double piEstimate;
        public double error;
        public double timeMs;

        public Result(double piEstimate, double error, double timeMs) {
            this.piEstimate = piEstimate;
            this.error = error;
            this.timeMs = timeMs;
        }
    }
}
