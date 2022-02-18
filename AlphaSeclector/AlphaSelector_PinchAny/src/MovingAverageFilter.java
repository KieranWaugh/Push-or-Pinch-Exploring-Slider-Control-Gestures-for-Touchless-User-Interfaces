/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Utility class that implements a general exponential moving average filter.
 * 
 * @author Euan Freeman
 */
public class MovingAverageFilter {
    private ArrayList<Double> data;
    private double value;
    private double weight;
    private int windowSize;
    
    public MovingAverageFilter(double weight, int windowSize) {
        this.weight = weight;
        this.windowSize = windowSize;
        data = new ArrayList(windowSize + 1);
        value = 0.0;
    }
    
    /**
     * Returns the current filter value.
     */
    public double getValue() {
        return value;
    }
    
    /**
     * Updates the filter with the given value, then calculates the new
     * exponential moving average.
     * 
     * @param value Value used to update the moving average.
     * 
     * @return Exponential moving average after adding the new value.
     */
    public double update(final double value) {
        data.add(value);
        
        // Remove oldest value if necessary
        if (data.size() > windowSize) {
            data.remove(0);
        }
        
        this.value = exponentialMovingAverage(data, weight);
        
        return this.value;
    }
    
    /**
     * Resets the filter so that a new average will be computed on the
     * next {@link #update}.
     */
    public void reset() {
        data.clear();
    }
    
    /**
     * Utility function for calculating the total of the given array.
     * 
     * @param a Collection of numbers to be summed.
     * 
     * @return Array sum.
     */
    public static final double sum(Collection<Double> a) {
        double total = 0.0;
        
        for (double n : a) {
            total += n;
        }
        
        return total;
    }
    
    /**
     * Utility function for calculating the exponential moving average of the
     * given array using the given weight.
     * 
     * @param data Collection of values to be averaged.
     * @param weight Strength of the filter, from 0.0 to 1.0.
     * 
     * @return Exponential moving average using the given values.
     */
    public static final double exponentialMovingAverage(ArrayList<Double> data, double weight) {
        double num = 0.0;
        double den = 0.0;
        
        double average = sum(data) / data.size();
        
        for (int i = 0; i < data.size(); i++) {
            num += data.get(i) * Math.pow(weight, data.size() - i - 1);
            den += Math.pow(weight, data.size() - i - 1);
        }
        
        num += average * Math.pow(weight, data.size());
        den += Math.pow(weight, data.size());
        
        return num/den;
    }
    
    /**
     * Main function can be used to test the implementation of this filter.
     */
    public static void main(String[] args) {
        MovingAverageFilter filter = new MovingAverageFilter(0.9, 10);
        Random r = new Random();
        double randomMin = 0;
        double randomMax = 20.0;
        
        for (int i = 0; i < 100; i++) {
            double random = randomMin + (randomMax - randomMin) * r.nextDouble();
            double movingAverage = filter.update(random);
            
            System.out.printf("Random = % .3f     Average: % .3f\n", random, movingAverage);
        }
    }
}
