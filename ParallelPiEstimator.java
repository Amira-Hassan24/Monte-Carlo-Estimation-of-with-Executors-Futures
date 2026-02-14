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

public class ParallelPiEstimator implements PiEstimator {

    @Override
    public double estimatePi(SimulationConfig config) {
        long totalPoints = config.getTotalPoints();
        int numTasks = config.getNumTasks();
        int numThreads = config.getNumThreads();

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Callable<Long>> tasks = new ArrayList<>();
        long pointsPerTask = totalPoints / numTasks;

        for (int i = 0; i < numTasks; i++) {
            tasks.add(() -> {
                long inside = 0;
                for (long j = 0; j < pointsPerTask; j++) {
                    double x = ThreadLocalRandom.current().nextDouble();
                    double y = ThreadLocalRandom.current().nextDouble();
                    if (x*x + y*y <= 1) inside++;
                }
                return inside;
            });
        }

        long insideCircle = 0;
        try {
            List<Future<Long>> results = executor.invokeAll(tasks);
            for (Future<Long> f : results) insideCircle += f.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        executor.shutdown();
        return 4.0 * insideCircle / totalPoints;
    }
}