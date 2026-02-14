/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxapplication3;

/**
 *
 * @author A
 */
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ParallelEulerEstimator implements EulerEstimator {
    
    @Override
    public double estimateEuler(SimulationConfig config) {
        long totalSamples = config.getTotalPoints();
        int numThreads = config.getNumThreads();
        
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        long samplesPerTask = totalSamples / numThreads;
        
        List<Callable<Long>> tasks = new ArrayList<>();
        
        for (int i = 0; i < numThreads; i++) {
            final long taskSamples = (i < totalSamples % numThreads) ? 
                samplesPerTask + 1 : samplesPerTask;
            
            tasks.add(() -> {
                long totalCount = 0;
                ThreadLocalRandom random = ThreadLocalRandom.current();
                
                for (long trial = 0; trial < taskSamples; trial++) {
                    double sum = 0.0;
                    int count = 0;
                    while (sum < 1.0) {
                        sum += random.nextDouble();
                        count++;
                    }
                    totalCount += count;
                }
                return totalCount;
            });
        }
        
        try {
            List<Future<Long>> futures = executor.invokeAll(tasks);
            long totalCount = 0;
            
            for (Future<Long> future : futures) {
                totalCount += future.get();
            }
            
            return (double) totalCount / totalSamples;
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }
    }
}
