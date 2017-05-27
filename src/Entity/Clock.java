/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import GUI.ViewController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.util.Duration;

/**
 *
 * @author shanks
 */
public class Clock extends IntegerProperty{
    private Timeline time;
    private int  timeElipsed;
    private Simulation simulation;
    private int maxTime;
    public Clock (Simulation simulation){
        this.simulation=simulation;
        timeElipsed = 0;
        maxTime = this.simulation.getSimulationTime();
    }
    
    public void start(){
        time = new Timeline(new KeyFrame(Duration.seconds(1), (k)->{
            timeElipsed+=1;  
            simulation.updateProgress(timeElipsed);
            if(timeElipsed > maxTime){
                simulation.stop(false);
                simulation.finish();
            }
        }));
        time.setCycleCount(Integer.MAX_VALUE);
        time.play();
    }
    
    public void pause(){
        time.pause();
    }
    
    public Animation.Status getStatus(){
        return time.getStatus();
    }
    
    public void stop(){
        time.stop();
        timeElipsed = 0;
    }
    
    @Override
    public int get() {
        return timeElipsed;
    }

    @Override
    public void addListener(ChangeListener<? super Number> listener) {
       
    }

    @Override
    public void removeListener(ChangeListener<? super Number> listener) {
    }

    @Override
    public void addListener(InvalidationListener listener) {
    }

    @Override
    public void removeListener(InvalidationListener listener) {
    }


    @Override
    public String getName() {
        return "Clock";
    }

    @Override
    public void bind(ObservableValue<? extends Number> observable) {
        
    }

    @Override
    public void unbind() {
        
    }

    @Override
    public boolean isBound() {
        return true;
    }

    @Override
    public void set(int value) {
        timeElipsed = value;
    }

    @Override
    public Object getBean(){
        return null;
    }

   }
