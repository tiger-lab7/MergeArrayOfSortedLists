package org.example;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Solution3Multithreading {

    public ListNode mergeKLists(ListNode[] lists) {
        if (lists.length == 0) return null;


        int interval = 1;
        while (interval < lists.length) {

            ExecutorService executorService = Executors.newFixedThreadPool(4);
            ArrayList<Future<ListNode>> futures = new ArrayList<>();


            for (int i = 0; i + interval < lists.length; i = i + interval * 2) {
                Callable<ListNode> callable = new Merge2Lists(lists[i], lists[i + interval]);

                futures.add(executorService.submit(callable));
                //lists[i] = merge2Lists(lists[i], lists[i + interval]);
            }

            int complete;
            do {
                complete = 0;
                for (int i = 0; i < futures.size(); ++i) {
                    if (futures.get(i).isDone()) complete += 1;
                }
            }
            while (complete == futures.size());

            int n = 0;
            for (int i = 0; i + interval < lists.length; i = i + interval * 2) {

                try {
                    lists[i] = futures.get(n).get();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                n += 1;
            }


            executorService.shutdown();

            interval *= 2;
        }

        return lists[0];
    }


}

class Merge2Lists implements Callable<ListNode> {

    private ListNode first, second;

    Merge2Lists(ListNode first, ListNode second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public ListNode call() {
        //System.out.println(Thread.currentThread().getName() + " started!");

        ListNode tmp = new ListNode(0);
        ListNode output = tmp;

        while (first != null && second != null) {

            if (first.val < second.val) {
                tmp.next = first;
                tmp = tmp.next;
                first = first.next;
            } else {
                tmp.next = second;
                tmp = tmp.next;
                second = second.next;
            }
        }

        if (first == null) tmp.next = second;
        if (second == null) tmp.next = first;

        return output.next;
    }
}