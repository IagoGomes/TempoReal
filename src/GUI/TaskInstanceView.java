/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Entity.TaskInstance;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author shanks
 */
public class TaskInstanceView extends AnchorPane{
    private final TaskInstance taskInstance;
    private final Label labelNameTitle;
    private final Label labelActivetionTime;
    private final Label labelEndTime;
    private final Label labelRelativeDeadline;
    private final Label labelInitiateTime;
    private final TextField tfActivetionTime;
    private final TextField tfEndTime;
    private final TextField tfRelativeDeadline;
    private final TextField tfInitiateTime;
    private final VBox vbox;
    private final GridPane grid;
    private final int width =141;
    private final int height =134;
    private final int tfWidth = 40;
    private static final String rgb [] = {"f39c7c", "f8c571", "95cf95", "88c6da", "8996da",
                                     "bd89d9", "d9889d", "b3b3b3"};
    private static int indexRGB = 0;
    private final String color;
    
    
    public TaskInstanceView(TaskInstance taskInstance){
        this.taskInstance = taskInstance;
        
        labelNameTitle = new Label(this.taskInstance.getInstanceName());
        
        labelActivetionTime = new Label("Temp. At.:");
        labelEndTime = new Label("Temp. Fim:");
        labelRelativeDeadline = new Label("Rel. Deadline:");
        labelInitiateTime = new Label("Temp. Início:");
        
        tfActivetionTime = new TextField();
        tfActivetionTime.setMaxWidth(tfWidth);
        tfActivetionTime.setEditable(false);
        tfActivetionTime.setText("-");
        
        tfEndTime = new TextField();
        tfEndTime.setMaxWidth(tfWidth);
        tfEndTime.setEditable(false);
        tfEndTime.setText("-");
        
        tfRelativeDeadline = new TextField();
        tfRelativeDeadline.setMaxWidth(tfWidth);
        tfRelativeDeadline.setEditable(false);
        tfRelativeDeadline.setText("-");
        
        tfInitiateTime = new TextField();
        tfInitiateTime.setMaxWidth(tfWidth);
        tfInitiateTime.setEditable(false);
        tfInitiateTime.setText("-");
        
        grid = new GridPane();
        grid.add(labelInitiateTime, 0, 0);
        grid.add(tfInitiateTime, 1, 0);
        grid.add(labelActivetionTime, 0,1);
        grid.add(tfActivetionTime, 1, 1);
        grid.add(labelEndTime, 0, 2);
        grid.add(tfEndTime, 1, 2);
        grid.add(labelRelativeDeadline, 0, 3);
        grid.add(tfRelativeDeadline, 1, 3);       
                
        vbox = new VBox();
        vbox.setMaxSize(width, height);
        vbox.setMinSize(width, height);
        vbox.setPrefSize(width, height);
        vbox.getChildren().add(labelNameTitle);
        vbox.getChildren().add(grid);
        
        this.setStyle("-fx-background-color:#"+(color=rgb[(indexRGB++)%rgb.length])+"88");
        this.getChildren().add(vbox);
    }//fim construtor
    
    /**
     * Método que atualiza o painel
     */
    public void updateInfos(){
        tfRelativeDeadline.setText((taskInstance.getRelativeDeadline()==-1)? "-" :
                              Integer.toString(taskInstance.getRelativeDeadline()));
        tfActivetionTime.setText((taskInstance.getActivationTime()==-1)? "-" :
                              Integer.toString(taskInstance.getActivationTime()));
        tfEndTime.setText((taskInstance.getEndTime()==-1)? "-" :
                              Integer.toString(taskInstance.getEndTime()));
        tfInitiateTime.setText((taskInstance.getInitDate()==-1)? "-" :
                              Integer.toString(taskInstance.getInitDate()));
    }//fim updateInfos
    
    /**
     * Método que reinicia a contagem das cores
     */
    public static void clean(){
        indexRGB=0;
    }//fim clean
    
    public void showOverflowDeadline(){
       Label labelOverflowDeadline = new Label("Estorou Deadline!");
       labelOverflowDeadline.setStyle("-fx-background-color:#ff000088");
       grid.add(labelOverflowDeadline, 0, 4, 2, 1);
       
    }
    
}//fim class
