/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hue7.bsp1;

import com.sun.jmx.remote.util.EnvHelp;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import static java.util.concurrent.ForkJoinTask.invokeAll;
import java.util.concurrent.RecursiveAction;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;

/**
 *
 * @author holzm
 */
public final class ReciprocalArraySum {

    /**
     * Default constructor.
     */
    private ReciprocalArraySum() {
    }

    /**
     * Sequentially compute the sum of the reciprocal values for a given array.
     *
     * @param input Input array
     * @return The sum of the reciprocals of the array input
     */
    protected static double seqArraySum(final double[] input) {
        double sum = 0;

        // ToDo: Compute sum of reciprocals of array elements
        for (int i = 0; i < input.length; i++) {
            sum = sum + (1d / i);
        }
        return sum;
    }

    /**
     * This class stub can be filled in to implement the body of each task
     * created to perform reciprocal array sum in parallel.
     */
    private static class ReciprocalArraySumTask extends RecursiveAction {

        /**
         * Starting index for traversal done by this task.
         */
        private final int startIndexInclusive;
        /**
         * Ending index for traversal done by this task.
         */
        private final int endIndexExclusive;
        /**
         * Input array to reciprocal sum.
         */
        private final double[] input;
        /**
         * Intermediate value produced by this task.
         */
        private double value;

        private static int SEQUENTIAL_THRESHOLD = 50000;

        /**
         * Constructor.
         *
         * @param setStartIndexInclusive Set the starting index to begin
         * parallel traversal at.
         * @param setEndIndexExclusive Set ending index for parallel traversal.
         * @param setInput Input values
         */
        ReciprocalArraySumTask(final int setStartIndexInclusive,
                final int setEndIndexExclusive, final double[] setInput) {
            this.startIndexInclusive = setStartIndexInclusive;
            this.endIndexExclusive = setEndIndexExclusive;
            this.input = setInput;
        }

        /**
         * Getter for the value produced by this task.
         *
         * @return Value produced by this task
         */
        public double getValue() {
            return value;
        }

        @Override
        protected void compute() {
            // TODO: Implement Thread forking on Threshold value. (If size of
            // array smaller than threshold: compute sequentially else, fork
            // 2 new threads

            if (input.length <= SEQUENTIAL_THRESHOLD) { // base case
                double sum = seqArraySum(input);
                System.out.println("Summe:" + seqArraySum(input));
            } else { // recursive case
                // Calculate new range
                int mid = input.length / 2;

                double[] firstHalf = Arrays.copyOfRange(input, startIndexInclusive, mid);
                double[] secondHalf = Arrays.copyOfRange(input, mid, endIndexExclusive);

                ReciprocalArraySumTask firstSubtask
                        = new ReciprocalArraySumTask(startIndexInclusive, endIndexExclusive, firstHalf);
                ReciprocalArraySumTask secondSubtask
                        = new ReciprocalArraySumTask(startIndexInclusive, endIndexExclusive, secondHalf);

                //firstSubtask.fork(); // queue the first task
                //secondSubtask.compute(); // compute the second task
                //firstSubtask.join(); // wait for the first task result
                invokeAll(firstSubtask, secondSubtask);

            }

        }
    }

    /**
     * TODO: Extend the work you did to implement parArraySum to use a set
     * number of tasks to compute the reciprocal array sum.
     *
     * @param input Input array
     * @param numTasks The number of tasks to create
     * @return The sum of the reciprocals of the array input
     */
    static double result;

    protected static double parManyTaskArraySum(final double[] input,
            final int numTasks) {

        ForkJoinPool forkJoinPool = new ForkJoinPool(numTasks);
        if (input.length <= ReciprocalArraySumTask.SEQUENTIAL_THRESHOLD) { // base case
            double sum = seqArraySum(input);
            //System.out.format("Sum of %s: %d\n", data.toString(), sum);
            return sum;
        } else { // recursive case
            // Calculate new range
            ReciprocalArraySumTask reciTask = new ReciprocalArraySumTask(1, 4, input);
            forkJoinPool.execute(reciTask);
            forkJoinPool.invoke(reciTask);

            ForkJoinTask<Void> result = forkJoinPool.submit(reciTask);

        }
        return result;
    }

    public static void main(String[] args) {
        double[] input = new double[60000];
        for (int i = 0; i < input.length; i++) {
            input[i] = (Math.random() * 100);

        }

        ForkJoinPool pool = new ForkJoinPool();
        // System.out.println("Pool parallelism: " + pool.getParallelism());
        ReciprocalArraySumTask task = new ReciprocalArraySumTask(0, input.length - 1, input);
        //  task.compute();
        pool.invoke(task);
    }
}
