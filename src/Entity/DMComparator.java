/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import java.util.Comparator;

/**
 *
 * @author shanks
 */
public class DMComparator implements Comparator<Task>{

    @Override
    public int compare(Task o1, Task o2) {
       if(o1.getDeadline()>o2.getDeadline()) return 1;
       else if(o1.getDeadline()<o2.getDeadline()) return -1;
       else return 0;
    }//fim compare
    
}//fim class
