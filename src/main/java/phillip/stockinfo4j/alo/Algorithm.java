package phillip.stockinfo4j.alo;

import java.util.List;

public class Algorithm {

    /***
     * 快速排序
     * @param list
     * @param low 第二指標
     * @param high 第一指標
     */
    public static void quicksort(List<Integer> list, int low, int high) {
        if (low > high) {
            return;
        }
        int pivot = list.get(high);
        int pointer = low;
        for (int i = low; i < high; i++) {
            if (list.get(i) <= pivot) {
                int temp = list.get(i);
                list.set(i, list.get(pointer));
                list.set(pointer, temp);
                pointer++;
            }
        }
        int temp = list.get(pointer);
        list.set(pointer, list.get(high));
        list.set(high, temp);
        quicksort(list, low, pointer - 1);
        quicksort(list, pointer + 1, high);
    }


    private static void sort(int[] array, int start, int end) {
        //跳出遞歸的條件
        if (start >= end) {
            return;
        }
        int mid = (start + end) / 2;
        // 遞歸實現歸併排序
        sort(array, start, mid);
        sort(array, mid + 1, end);
        mergerSort(array, start, mid, end);
    }

    /**
     * 將兩個有序序列歸併為一個有序序列(二路歸併)
     * @param array
     * @param start 開始數組開始的位置
     * @param mid 中間位置(因為合的必定是相挨的數組)
     * @param end 結束的位置
     */
    private static void mergerSort(int[] array, int start, int mid, int end) {
        // 定義一個臨時數組，用來存儲排序後的結果
        int[] arr = new int[end - start + 1];
        //零時數組的下標
        int low = 0;
        //記錄開始的位置，方便後面替換用
        int left = start;
        int center = mid + 1;
        //取出最小值放入臨時數組中
        while (start <= mid && center <= end) {
        //如果第一個數組的數大於第二個數組，則取第二哥數組中的數據
            arr[low++] = array[start] > array[center] ? array[center++] : array[start++];
        }
        //若還有段序列不為空，則將其加入臨時數組末尾
        while (start <= mid) {
            arr[low++] = array[start++];
        }
        while (center <= end) {
            arr[low++] = array[center++];
        }
        //將臨時數組中的值copy到原數組中
        for (int i = left, j = 0; i <= end && j < arr.length; i++, j++) {
            array[i] = arr[j];
        }
    }

}
