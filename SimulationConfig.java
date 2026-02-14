/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxapplication3;

/**
 *
 * @author A
 */
public class SimulationConfig {
    private long totalPoints;
    private int numTasks;
    private int numThreads;

    public SimulationConfig(long totalPoints, int numTasks, int numThreads) {
        this.totalPoints = totalPoints;
        this.numTasks = numTasks;
        this.numThreads = numThreads;
    }

    public long getTotalPoints() { return totalPoints; }
    public int getNumTasks() { return numTasks; }
    public int getNumThreads() { return numThreads; }
}