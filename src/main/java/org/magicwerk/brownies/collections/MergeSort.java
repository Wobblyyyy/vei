package org.magicwerk.brownies.collections;

import org.magicwerk.brownies.collections.helper.NaturalComparator;

import java.util.Comparator;
import java.util.List;


public class MergeSort<E> {
    List<E> list;
    Comparator<? super E> comparator;

    private MergeSort(List<E> list, Comparator<? super E> comparator) {
        this.list = list;
        if (comparator == null) {
            comparator = NaturalComparator.INSTANCE();
        }
        this.comparator = comparator;
    }

    public static <E> void sort(List<E> list, Comparator<? super E> comparator) {
        MergeSort<E> sort = new MergeSort<E>(list, comparator);
        sort.sort();
    }

    public static <E> void sort(List<E> list, Comparator<? super E> comparator, int from, int to) {
        MergeSort<E> sort = new MergeSort<E>(list, comparator);
        sort.sort(from, to);
    }

    private void sort() {
        sort(0, list.size());
    }

    private void sort(int from, int to) {
        if (to - from < 12) {
            insertSort(from, to);
            return;
        }
        int middle = (from + to) / 2;
        sort(from, middle);
        sort(middle, to);
        merge(from, middle, to, middle - from, to - middle);
    }

    private int compare(int idx1, int idx2) {
        return comparator.compare(list.get(idx1), list.get(idx2));
    }

    private void swap(int idx1, int idx2) {
        E val = list.get(idx1);
        list.set(idx1, list.get(idx2));
        list.set(idx2, val);
    }

    private int lower(int from, int to, int val) {
        int len = to - from, half;
        while (len > 0) {
            half = len / 2;
            int mid = from + half;
            if (compare(mid, val) < 0) {
                from = mid + 1;
                len = len - half - 1;
            } else
                len = half;
        }
        return from;
    }

    private int upper(int from, int to, int val) {
        int len = to - from, half;
        while (len > 0) {
            half = len / 2;
            int mid = from + half;
            if (compare(val, mid) < 0)
                len = half;
            else {
                from = mid + 1;
                len = len - half - 1;
            }
        }
        return from;
    }

    private void insertSort(int from, int to) {
        if (to > from + 1) {
            for (int i = from + 1; i < to; i++) {
                for (int j = i; j > from; j--) {
                    if (compare(j, j - 1) < 0)
                        swap(j, j - 1);
                    else
                        break;
                }
            }
        }
    }

    private int gcd(int m, int n) {
        while (n != 0) {
            int t = m % n;
            m = n;
            n = t;
        }
        return m;
    }

    private void rotate(int from, int mid, int to) {

        if (from == mid || mid == to) {
            return;
        }
        int n = gcd(to - from, mid - from);
        while (n-- != 0) {
            E val = list.get(from + n);
            int shift = mid - from;
            int p1 = from + n, p2 = from + n + shift;
            while (p2 != from + n) {
                list.set(p1, list.get(p2));
                p1 = p2;
                if (to - p2 > shift) {
                    p2 += shift;
                } else {
                    p2 = from + (shift - (to - p2));
                }
            }
            list.set(p1, val);
        }
    }

    private void merge(int from, int pivot, int to, int len1, int len2) {
        if (len1 == 0 || len2 == 0) {
            return;
        }
        if (len1 + len2 == 2) {
            if (compare(pivot, from) < 0) {
                swap(pivot, from);
            }
            return;
        }
        int first_cut, second_cut;
        int len11, len22;
        if (len1 > len2) {
            len11 = len1 / 2;
            first_cut = from + len11;
            second_cut = lower(pivot, to, first_cut);
            len22 = second_cut - pivot;
        } else {
            len22 = len2 / 2;
            second_cut = pivot + len22;
            first_cut = upper(from, pivot, second_cut);
            len11 = first_cut - from;
        }
        rotate(first_cut, pivot, second_cut);
        int newMid = first_cut + len22;
        merge(from, first_cut, newMid, len11, len22);
        merge(newMid, second_cut, to, len1 - len11, len2 - len22);
    }

}
