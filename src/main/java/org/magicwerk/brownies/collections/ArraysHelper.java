package org.magicwerk.brownies.collections;

import java.util.Arrays;


public class ArraysHelper {

    public static int compare(int val1, int val2) {
        if (val1 < val2)
            return -1;
        else if (val1 > val2)
            return 1;
        else
            return 0;
    }

    public static int compare(long val1, long val2) {
        if (val1 < val2)
            return -1;
        else if (val1 > val2)
            return 1;
        else
            return 0;
    }

    public static int compare(double val1, double val2) {
        if (val1 < val2)
            return -1;
        else if (val1 > val2)
            return 1;
        else
            return 0;
    }

    public static int compare(float val1, float val2) {
        if (val1 < val2)
            return -1;
        else if (val1 > val2)
            return 1;
        else
            return 0;
    }

    public static int compare(boolean val1, boolean val2) {
        return (val1 == val2) ? 0 : ((val1) ? 1 : -1);
    }


    public static void sort(int[] values, int fromIndex, int toIndex) {
        Arrays.sort(values, fromIndex, toIndex);
    }

    public static void sort(long[] values, int fromIndex, int toIndex) {
        Arrays.sort(values, fromIndex, toIndex);
    }

    public static void sort(double[] values, int fromIndex, int toIndex) {
        Arrays.sort(values, fromIndex, toIndex);
    }

    public static void sort(float[] values, int fromIndex, int toIndex) {
        Arrays.sort(values, fromIndex, toIndex);
    }

    public static void sort(boolean[] values, int fromIndex, int toIndex) {
        boolean reorder = false;
        int numFalse = 0;
        int numTrue = 0;
        for (int i = fromIndex; i < toIndex; i++) {
            if (values[i]) {
                numTrue++;
            } else {
                numFalse++;
                if (numTrue > 0) {
                    reorder = true;
                }
            }
        }
        if (reorder) {
            for (int i = 0; i < numFalse; i++) {
                values[fromIndex + i] = false;
            }
            for (int i = 0; i < numTrue; i++) {
                values[fromIndex + numFalse + i] = true;
            }
        }
    }

    public static void sort(byte[] values, int fromIndex, int toIndex) {
        Arrays.sort(values, fromIndex, toIndex);
    }

    public static void sort(char[] values, int fromIndex, int toIndex) {
        Arrays.sort(values, fromIndex, toIndex);
    }

    public static void sort(short[] values, int fromIndex, int toIndex) {
        Arrays.sort(values, fromIndex, toIndex);
    }


    public static int binarySearch(int[] values, int fromIndex, int toIndex, int key) {
        return Arrays.binarySearch(values, fromIndex, toIndex, key);
    }

    public static int binarySearch(long[] values, int fromIndex, int toIndex, long key) {
        return Arrays.binarySearch(values, fromIndex, toIndex, key);
    }

    public static int binarySearch(double[] values, int fromIndex, int toIndex, double key) {
        return Arrays.binarySearch(values, fromIndex, toIndex, key);
    }

    public static int binarySearch(float[] values, int fromIndex, int toIndex, float key) {
        return Arrays.binarySearch(values, fromIndex, toIndex, key);
    }

    public static int binarySearch(boolean[] values, int fromIndex, int toIndex, boolean key) {
        if (key) {
            if (values[toIndex - 1] == true) {
                return toIndex - 1;
            } else {
                return -toIndex - 1;
            }
        } else {
            if (values[fromIndex] == false) {
                return fromIndex;
            } else {
                return -fromIndex - 1;

            }
        }
    }

    public static int binarySearch(byte[] values, int fromIndex, int toIndex, byte key) {
        return Arrays.binarySearch(values, fromIndex, toIndex, key);
    }

    public static int binarySearch(char[] values, int fromIndex, int toIndex, char key) {
        return Arrays.binarySearch(values, fromIndex, toIndex, key);
    }

    public static int binarySearch(short[] values, int fromIndex, int toIndex, short key) {
        return Arrays.binarySearch(values, fromIndex, toIndex, key);
    }

}
