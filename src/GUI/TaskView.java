/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Entity.Simulation;
import Entity.Task;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author shanks
 */
public class TaskView extends AnchorPane{
    private final Label title;
    private VBox vbox;
    private GridPane gridPanel;
    private Label labelDeadline;
    private final Label labelComputeTime;
    private final Label labelPeriode;
    private final TextField tfDeadline;
    private TextField tfComputeTime;
    private TextField tfPeriode;
    
    private final int width =141;
    private final int height =134;
    private final int tfWidth = 40;
    
    private Task task;
    
    private Simulation simulation;
    
    /**
     * Construtor da classe
     * @param task TaskInstance cuja view está associada
     */
    public TaskView(Task task, Simulation simulation){
        this.task=task;
        this.simulation=simulation;
        title = new Label(task.getName());
        
        gridPanel = new GridPane();
        
        labelDeadline = new Label("Deadline:");
        tfDeadline = new TextField();
        tfDeadline.setMaxWidth(tfWidth);
        tfDeadline.setText(Integer.toString(task.getDeadline()));
        tfDeadline.setOnMouseClicked((event) -> {
           if(tfDeadline.getText().trim().equals("0"))
               tfDeadline.clear();
        });

        
        labelComputeTime=new Label("Temp. Comp.:");
        tfComputeTime= new TextField();
        tfComputeTime.setMaxWidth(tfWidth);
        tfComputeTime.setText(Integer.toString(task.getComputeTime()));
        tfComputeTime.setOnMouseClicked((event) -> {
           if(tfComputeTime.getText().trim().equals("0"))
               tfComputeTime.clear();
        });
                
        labelPeriode=new Label("Período:");
        tfPeriode= new TextField();
        tfPeriode.setMaxWidth(tfWidth);
        tfPeriode.setText(Integer.toString(task.getPeriod()));
        tfPeriode.setOnMouseClicked((event) -> {
           if(tfPeriode.getText().trim().equals("0"))
               tfPeriode.clear();
        });
        tfPeriode.setOnKeyReleased((e)->{
           try{
               this.task.update();
               simulation.updateSimulationTime();
           }catch(Exception ex){}
        });
        
        gridPanel.add(labelDeadline, 0, 0);
        gridPanel.add(tfDeadline, 1, 0);
        gridPanel.add(labelComputeTime, 0, 1);
        gridPanel.add(tfComputeTime, 1, 1);
        gridPanel.add(labelPeriode, 0, 2);
        gridPanel.add(tfPeriode, 1, 2);            
        
        vbox = new VBox();
        vbox.setMaxSize(width, height);
        vbox.setMinSize(width, height);
        vbox.setPrefSize(width, height);
        vbox.getChildren().add(title);
        vbox.getChildren().add(gridPanel);
        this.getChildren().add(vbox);
    }//fim construtor
    
    /**
     * Método que carrega todas as informações dos textfield de um taskview e 
     * atualiza os valores das propriedades das tasks (computeTime, deadline e periode)
     * @return true, se foi possível atualizar
     *         false, caso contrário
     */
    public boolean updateInfos(){
        //falta completar alguma informação
        if(tfComputeTime.getText().trim().isEmpty() || tfDeadline.getText().trim().isEmpty() || tfPeriode.getText().trim().isEmpty()){
            if(tfComputeTime.getText().trim().isEmpty())
                labelComputeTime.setStyle("-fx-text-fill:red");               
            if(tfDeadline.getText().trim().isEmpty())
                labelDeadline.setStyle("-fx-text-fill:red");
            if(tfPeriode.getText().trim().isEmpty()){
                labelPeriode.setStyle("-fx-text-fill:red");
            }else{
                try{
                    int p = Integer.parseInt(tfPeriode.getText().trim());
                                     
                    task.setPeriod(p);
                    
                }catch(Exception e){
                }
            }
            return false;
        }//fim if
        if(tfComputeTime.getText().trim().equals("0") || tfDeadline.getText().trim().equals("0") || tfPeriode.getText().trim().equals("0")){
            if(tfComputeTime.getText().trim().equals("0"))
                labelComputeTime.setStyle("-fx-text-fill:red");
            if(tfDeadline.getText().trim().equals("0"))
                labelDeadline.setStyle("-fx-text-fill:red");
            if(tfPeriode.getText().trim().equals("0")){
                labelPeriode.setStyle("-fx-text-fill:red");
            }else{
                
                try{
                    int p = Integer.parseInt(tfPeriode.getText().trim());
                    task.setPeriod(p);
                    
                }catch(Exception e){
                }
            }
            return false;
        }//fim if
        try{
            labelComputeTime.setStyle("-fx-text-fill:black");
            labelDeadline.setStyle("-fx-text-fill:black");
            labelPeriode.setStyle("-fx-text-fill:black");
            
            int computeTime=Integer.parseInt(tfComputeTime.getText());
            int deadline = Integer.parseInt(tfDeadline.getText());
            int periode = Integer.parseInt(tfPeriode.getText());
            
            task.setComputeTime(computeTime);
            task.setDeadline(deadline);
            task.setPeriod(periode);
            return true;
        }catch(Exception ex){
            return false;
        }//fim try-catch
    }//fim updateInfos
}//fim class
