package Entity;

import GUI.TaskInstanceView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class TaskInstance extends Task {

    private int initDate;
    private int endDate;
    private int activationDate;
    private final String nameInstance;
    private Series<String, Integer> datas;
    private AnchorPane instancePanel;
    private final Kernel kernel;
    private Timeline anim;
    private TaskInstanceView view;
    private int timeElipsed;
    int time;
    private final IntegerProperty timeBinding;
    private final boolean debug = false;
    
    public TaskInstance(Task t, int index, int createTime, Kernel k) {
        super(t.getName(), t.getDeadline(), t.getComputeTime(), t.getPeriod());
        this.initDate = createTime;
        setRelativeDeadline(t.getDeadline() + createTime);
        endDate = activationDate = -1;
        nameInstance = t.getName() + "_" + index;
        datas = new Series<>();
        datas.setName(nameInstance);
        this.kernel = k;
        timeElipsed = 0;
        timeBinding = new SimpleIntegerProperty();
        timeBinding.bind(kernel.getClock());

    }

    /**
     * Método que modifica o valor da date em que a task foi iniciada
     *
     * @param initDate int
     */
    public void setInitDate(int initDate) {
        this.initDate = initDate;
    }//fim setInitDate

    /**
     * Método que modifica o valor da data em que a task foi finalizada
     *
     * @param endDate int
     */
    public void setEndDate(int endDate) {
        this.endDate = endDate;
    }//fim setEndDate

    /**
     * Método que modifica o valor da data em que a task foi ativada
     *
     * @param activationDate int
     */
    public void setActivationDate(int activationDate) {
        this.activationDate = activationDate;
    }//fim setActivationDate

    /**
     * Método que ativa a task
     */
    public void active() {
        this.time = this.activationDate = timeBinding.get();
        this.view.updateInfos();
        if(debug)
            System.out.println("["+nameInstance+"]["+time+"] Ativou");
    }//fim active

    /**
     * Método que finaliza uma instancia de task
     */
    public void finish() {
        if (this.endDate == -1) {
            this.endDate = timeBinding.get();
            this.view.updateInfos();
            if(debug)
                System.out.println("["+nameInstance+"]["+endDate+"] Finalizou");
        }
    }//fim finish

    /**
     * Método que verifica se essa instancia de task foi finalizada
     *
     * @return boolean
     */
    public boolean isFinish() {
        return endDate != -1;
    }//fim isfinish

    /**
     * Método que retorna a data de início da task
     *
     * @return Calendar
     */
    public int getInitDate() {
        return initDate;
    }//fim getInitDate

    /**
     * Método que retorna a date de finalização da task
     *
     * @return Calendar
     */
    public int getEndTime() {
        return endDate;
    }//fim getEndDate

    /**
     * Método que retorna a data de ativação da task
     *
     * @return Calendar
     */
    public int getActivationTime() {
        return activationDate;
    }//fim getActivationDate

 

    /**
     * Método que verifica se a task já foi ativada
     *
     * @return boolean
     */
    public boolean isActived() {
        return activationDate != -1;
    }  //fim isRunning

    /**
     * Método que modifica o valor de instancePanel
     *
     * @param panel AnchorPane
     */
    public void setInstancePanel(AnchorPane panel) {
        this.instancePanel = panel;
    }//fim setInstancePanel

    /**
     * Método que retorna a referência de instancePanel
     *
     * @return AnchorPane
     */
    public AnchorPane getInstancePanel() {
        return instancePanel;
    }//fim getInstancePanel

    /**
     * Método que peempita a task
     */
    public void preempt() {
        if (anim != null) {
            anim.pause();
            if(debug)
                System.out.println("["+nameInstance+"]["+timeBinding.get()+"] Preemptou");
        }
    }//fim preempt

    public void resumeTask() {
        if (anim != null) {
            anim.play();
            if(debug)
                System.out.println("["+nameInstance+"]["+timeBinding.get()+"] Retornou");
        }
    }//fim t

    /**
     * Ação dessa instancia de task
     */
    public void start() {
        if (isActived()) {
            if (datas != null) {
                anim = new Timeline(new KeyFrame(Duration.seconds(1), (k) -> {
                    if (timeElipsed < getComputeTime()) {
                        setData(timeBinding.get() - 1);
                        timeElipsed++;
                    } //fim if

                }));
                anim.setCycleCount(getComputeTime());
                anim.setOnFinished((e) -> {
                    finish();
                    kernel.schedulingTask();
                });
                anim.play();
            }//fim if
        }//fim if
    }//fim run

    private void setData(int time) {
        if(debug)
            System.out.println("["+nameInstance+"]["+time+"] setData");
        Data c = new XYChart.Data(Integer.toString(time), 1);
        datas.getData().add(c);
        Data c1 = new XYChart.Data(Integer.toString(time + 1), 1);
        datas.getData().add(c1);
        if (time >= getRelativeDeadline()) {
            view.showOverflowDeadline();            
        }//fim if
        kernel.updateChartWidth();
        // }
    }//fim setData

    /**
     * Método que retorna o nome da instância da task
     *
     * @return String
     */
    public String getInstanceName() {
        return nameInstance;
    }//fim getInstanceName

    /**
     * Método que modifica objeto Series<String, Integer> para animação do
     * gráfico
     *
     * @param datas
     */
    public void setSeriesData(Series<String, Integer> datas) {
        this.datas = datas;
    }//fim setSeriesData

    /**
     * Método que retorna a Series<String, Integer> desta taskInstance. Esse
     * obejeto é usado para animar o gráfico
     *
     * @return
     */
    public Series<String, Integer> getSeriesData() {
        return datas;
    }//fim getSeriesData

    /**
     * Método que converte o objeto em String
     *
     * @return String
     */
    @Override
    public String toString() {
        return "(" + getInstanceName() + " -> {C=" + getComputeTime() + ",Dr=" + getRelativeDeadline() + "})";
    }//fim toString

    /**
     * Método que modifica o valor do taskView da taskInstance
     *
     * @param v TaskInstanceView
     */
    public void setInstanceView(TaskInstanceView v) {
        this.view = v;
        this.view.updateInfos();
    }//fim setInstanceView

    /**
     * Método que para a execução de uma instância de task
     */
    void stopTaskInstance() {
        if (anim != null) {
            anim.stop();
            if(debug)
                System.out.println("["+nameInstance+"]["+timeBinding.get()+"] Stop");
        }//fim if
    }//fim stopTaskInstance

}//fim class
