package Entity;

import java.util.Comparator;

public class EDFComparator implements Comparator<Task> {
    @Override
    public int compare(Task o1, Task o2) {
        if(o1.getRelativeDeadline()> o2.getRelativeDeadline()) return 1;
        else if (o1.getRelativeDeadline() < o2.getRelativeDeadline()) return -1;
        else return 0;
    }//fim compare
}//fim class
