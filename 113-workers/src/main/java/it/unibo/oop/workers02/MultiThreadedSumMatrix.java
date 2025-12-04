package it.unibo.oop.workers02;

import java.util.stream.DoubleStream;

/**
 * This is a advanced implementation of the calculation matrix sum. 
 */
public class MultiThreadedSumMatrix implements SumMatrix {

    private final int nthread;

    /**
     * @param threads n. of thread that perform the sum.
     */
    public MultiThreadedSumMatrix(final int threads) {
        this.nthread = threads;
    }

    /**
     * @param matrix is a dobule matrix that be calculated.
     */
    @Override
    public double sum(final double[][] matrix) {
        final int size = matrix.length % nthread + matrix.length / nthread;

        return DoubleStream.iterate(0, start -> start + size)
        .limit(nthread)
        .mapToObj(start -> new Worker(matrix, (int) start, size))
        .peek(Thread::start)
        .peek(thread -> {
            try {
                thread.join();
            } catch (final InterruptedException e) {
                throw new IllegalStateException(e);
            }
        })
        .mapToDouble(Worker::getResult)
        .sum();
    }

    private static class Worker extends Thread {

        private final double[][] matrix;
        private final int startrow;
        private final int nrows;
        private double res;

        Worker(final double[][] matrix, final int start, final int nrow) {
            super();
            this.matrix = matrix; //NOPMD
            this.startrow = start;
            this.nrows = nrow;
        }

        @Override
        @SuppressWarnings("PMD.SystemPrintln")
        public synchronized void run() {
            System.out.println("Working from row " + startrow + " to row " + (startrow + nrows - 1));
            for (int i = startrow; i < matrix.length && i < startrow + nrows; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    this.res += this.matrix[i][j];
                }
            }
        }

        public synchronized double getResult() {
            return this.res;
        }
    }
}
