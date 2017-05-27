package Entity;

import GUI.TaskView;
import GUI.ViewController;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;

public class Simulation {

    private boolean isRunning;
    private Kernel kernel;
    private int duration = 0;
    private ViewController view;
    private double progress=0;

    /**
     * Construtor da classe
     *
     * @param view ViewController
     */
    public Simulation(ViewController view) {
        this.view = view;
        this.isRunning = false;
        kernel = new Kernel();
        kernel.setSimulation(this);
    }//fim construtor

    /**
     * Método que inicia a simulação
     *
     * @return boolean
     */
    public boolean start() {
        if (!kernel.updateTasksInfo()) {
            view.setAlertMessage("Erro ao iniciar a simulação."
                    + "Verifique as informações atribuidas as tarefas.", ViewController.MESSAGE_TYPE.ERROR);
            return false;
        }//fim if-else

        if (kernel.isScheduling()) {
            view.setAlertMessage("O conjunto de tarefas  é escalonável pela politica " + kernel.getSchedulingAlgorithm(),
                    ViewController.MESSAGE_TYPE.OK);
        } else {
            view.setAlertMessage("O conjunto de tarefas não é escalonável pela politica " + kernel.getSchedulingAlgorithm(),
                    ViewController.MESSAGE_TYPE.WARNING);
        }//fim if-else
        if (kernel.getStatusKernel().equals(Kernel.Status.STOP)) {
            view.cleanChart();
            view.cleanPanelInstanceTask();
            if(!kernel.getState().equals(Thread.State.TERMINATED)){
                
                kernel.start();        
            }else{
                Kernel.SCHEDULING_ALGORITHM e = kernel.getSchedulingAlgorithm();
                kernel = new Kernel();
                kernel.setSimulation(this);
                kernel.setSchedulingAlgorithm(e);
                kernel.start();
                
            }
            progress = kernel.getSimulationTime();
            return true;
        }else if(kernel.getStatusKernel().equals(Kernel.Status.PAUSED)){
            kernel.resumeKernel();
            return true;
        }
        return false;
    }//fim start

    /**
     * Método que retorna a simulação
     *
     * @return boolean
     */
    public boolean resume() {
        try {
            if (kernel.getStatusKernel().equals(Kernel.Status.PAUSED)) {
                kernel.resumeKernel();
            }//fim if
            return true;
        } catch (Exception e) {
            return false;
        }//fim try-catch
    }//fim resume

    /**
     * Método que para a simulação
     *
     * @return boolean
     */
    public boolean stop(boolean all) {
        return kernel.stopKernel(all);
    }//fim stop

    /**
     * Método que pausa a simulação
     *
     * @return boolean
     */
    public boolean pause() {
        return kernel.pause();
    }//fim pause

    public void setTaskNumber(int newAmount) {
        int quant = newAmount - kernel.size();
        if (quant > 0) { //adicionar novas tarefas
            for (int i = 0; i < quant; i++) {
                Task t = new Task("T" + (kernel.size() + 1), 0, 0, 0);
                kernel.add(t);
                TaskView tk = new TaskView(t, this);
                t.setPanel(tk);
                view.getPanelTask().getChildren().add(tk);
            }//fim for
        } else { //remover últimas tarefas
            kernel.remove(-quant);
            view.getPanelTask().getChildren().remove(view.getPanelTask().getChildren().size() + quant, view.getPanelTask().getChildren().size());
        }//fim if-else
    }//fim setTaskNumber

    /**
     * Método que adiciona 5 tasks default para o simulador
     */
    public void initDefaultTasks() {
        Task t1 = new Task("T" + (kernel.size() + 1), 7, 2, 9);
        kernel.add(t1);
        TaskView tk1 = new TaskView(t1, this);
        t1.setPanel(tk1);
        view.getPanelTask().getChildren().add(tk1);

        Task t2 = new Task("T" + (kernel.size() + 1), 9, 1, 10);
        kernel.add(t2);
        TaskView tk2 = new TaskView(t2, this);
        t2.setPanel(tk2);
        view.getPanelTask().getChildren().add(tk2);

        Task t3 = new Task("T" + (kernel.size() + 1), 15, 2, 12);
        kernel.add(t3);
        TaskView tk3 = new TaskView(t3, this);
        t3.setPanel(tk3);
        view.getPanelTask().getChildren().add(tk3);

        Task t4 = new Task("T" + (kernel.size() + 1), 11, 3, 14);
        kernel.add(t4);
        TaskView tk4 = new TaskView(t4, this);
        t4.setPanel(tk4);
        view.getPanelTask().getChildren().add(tk4);

        Task t5 = new Task("T" + (kernel.size() + 1), 8, 1, 7);
        kernel.add(t5);
        TaskView tk5 = new TaskView(t5, this);
        t5.setPanel(tk5);
        view.getPanelTask().getChildren().add(tk5);
    }//fim initDefaultTasks

    /**
     * Método que modifica o algoritmo de escalonamento utilizado na simulação
     *
     * @param i int
     */
    public void setSchedulingAlgorithm(Kernel.SCHEDULING_ALGORITHM i) {
          kernel.setSchedulingAlgorithm(i);
    }//fim setSchedulingAlgorithm

    /**
     * Método que retorna a quantidade de tasks presente no escalonador
     *
     * @return
     */
    public int getSize() {
        return kernel.size();
    }//fim getSize

    /**
     * Método que adiciona um objeto series para o gráfico
     *
     * @param seriesData XYChart.Series<String, Integer>
     */
    void addSeriesToChart(XYChart.Series<String, Integer> seriesData) {
        view.addSeriesToChart(seriesData);
    }//fim addSeriesToChart

    /**
     * Método que retonar o panel para adicionar as tasksInstanceView
     *
     * @return HBox
     */
    public HBox getPanelTaskInstance() {
        return view.getPanelTaskInstance();
    }//fim

    /**
     * Método que atualiza o tamanho do gráfico
     */
    public void updateChartWidth() {
        view.updateChartWidth();
    }//fim updateChartWidth
    
    /**
     * Método que retorna o tempo de simulação total
     * @return int 
     */
    public int getSimulationTime(){
        return kernel.getSimulationTime();
    }//fim simulationTime
    
    /**
     * Atualiza o tempo de simulação na janela
     */
    public void updateSimulationTime(){
        view.setSimulationTime(kernel.getSimulationTime());
    }//fim updateSimulationTime
    
    /**
     * Atualiza a barra de progresso da janela
     * @param i int : tempo passado
     */
    public void updateProgress(int i){
        view.updateProgressBar(((double)i)/progress);
    }//fim updateProgress 
    
    /**
     * Método que mostra a taxa de uso do processador na tela da aplicação
     * @param rate float
     */
    public void setRateProcessor(float rate){
        view.setRateProcessor(rate);
    }//fim setRateProcessor

    /**
     * Método que mostra na janela que a simulação acabou
     */
    void finish() {
       view.setAlertMessage("A simulação terminou!", ViewController.MESSAGE_TYPE.INFORMATION);
    }//fim finish

    /**
     * Método que captura da tela o fator de velociade da animação
     * @return float
     */
    public float getSpeedFator() {
        return 0.5f;
    }//fim getSpeedFator
}
