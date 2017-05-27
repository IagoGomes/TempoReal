
package Entity;

import GUI.TaskView;

public class Task {
    private int deadlineAbsolute;
    private int computeTime;
    private int period;
    private int index;
    private TaskView panel;
    private boolean isInstanciate;
    private final String name;
    private int deadlineRelative;
    
    /**
     * Construtor da classe
     * @param name String
     * @param deadlineAbsolute int
     * @param computeTime  int
     * @param period int
     */
    public Task(String name, int deadlineAbsolute, int computeTime, int period){
        this.deadlineRelative =  this.deadlineAbsolute = deadlineAbsolute;
        this.computeTime=computeTime;
        this.period = period;
        this.name= name;
        index=0;
        isInstanciate = false;
    }//fim construtor
    
    /**
     * Método que modifica o valor de deadline
     * @param deadline : int
     */
    public void setDeadline(int deadline){
        this.deadlineAbsolute = deadline;
    }//fim setDeadline
    
    /**
     * Método que retorna o valor de deadline
     * @return int
     */
    public int getDeadline(){
        return deadlineAbsolute;
    }//fim getDeadline
    
    /**
     * Método que modifica o valor de computeTime
     * @param computeTime : int
     */
    public void setComputeTime(int computeTime){        
        this.computeTime=computeTime;
    }//fim setComputeTime
    
    /**
     * Método que retorna o valor de ComputeTime
     * @return int
     */
    public int getComputeTime(){
        return computeTime;
    }//fim getComputeTime
    
    /**
     * Método que retorna o valor do período da task
     * @return int
     */
    public int getPeriod(){
        return period;
    }//fim getPeriod
    
    /**
     * Método que modifica o valor do período da task
     * @param period int
     */
    public void setPeriod(int period){
        this.period=period;
    }//fim setPeriod
    
    
       /**
     * Método que retorna o valor do deadline relativo da task
     *
     * @return int
     */
    public int getRelativeDeadline() {
        return deadlineRelative;
    }//fim getDeadlineRelative

    /**
     * Método que modifica o valor do dealline relativo da task
     *
     * @param deadlineRelative int
     */
    public void setRelativeDeadline(int deadlineRelative) {
        this.deadlineRelative = deadlineRelative;
    }//fim setDeadlineRelative
   
    
    /**
     * Método que modifica a referencia do panel da task
     * @param panel AnchorPane
     */
    public void setPanel(TaskView panel){
        this.panel=panel;
    }//fim setPanel
    
    /**
     * Método que atualisa as informações da task
     * @return true : se a atualização ocorreu sem nenhum erro
     *         false: caso contrário
     */
    public boolean update(){
        return panel.updateInfos();
    }//fim update
    
    /**
     * Método que retorna o indice da instância atual
     * @return int
     */
    public int getIndex() {
       return index++;
    }//fim getIndex
    
    /**
     * Método que reinicia o contador de instâncias da task
     */
    public void clean(){
        index=0;
    }//fim clean
    
 
    public boolean instanciate(){
        return (isInstanciate) ? true : !(isInstanciate=true);
    }
    
    public String getName(){
        return name;
    }
    /**
     * Converte o objeto em String
     * @return String
     */
    @Override
    public String toString(){
        return getName() + "{D=" + getDeadline() +", C="+getComputeTime()+", P="+getPeriod()+"};";
    }//fim toString

    
}//fim class
