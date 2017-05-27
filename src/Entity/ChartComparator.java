/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import java.util.Comparator;
import javafx.scene.chart.XYChart.Data;

/**
 *
 * @author shanks
 */
public class ChartComparator implements Comparator<Data<String, Integer>>{

    @Override
    public int compare(Data<String, Integer> o1, Data<String, Integer> o2) {
        if(Integer.parseInt(o1.getXValue()) > Integer.parseInt(o2.getXValue())) return 1;
        else if(Integer.parseInt(o1.getXValue()) > Integer.parseInt(o2.getXValue())) return 1;
        else return 0;
    }
    
}
