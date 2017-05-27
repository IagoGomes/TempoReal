package Entity;

import java.util.Comparator;

public class LSTComparator implements Comparator<Task> {
    @Override
    public int compare(Task o1, Task o2) {
        if((o1.getDeadline() - o1.getComputeTime()) < (o2.getDeadline() - o2.getComputeTime())) return 1;
        else if((o1.getDeadline() - o1.getComputeTime()) > (o2.getDeadline() - o2.getComputeTime())) return -1;
        else return 0;
    }//fim compare
}//fim class
