package Entity;

import GUI.TaskInstanceView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Kernel extends Thread {

    private static Map<String, Task> tasks = new HashMap<>();
    private List<TaskInstance> tasksInstances;
    private final int maxSimulationTime = 100, minSimulationTime = 0;
    

    public static enum SCHEDULING_ALGORITHM {
        EDF, LST, RM, DM
    };
    private SCHEDULING_ALGORITHM schedulingAlgoritm;

    public static enum Status {
        PAUSED, RUNNING, STOP
    };

    private Status status;
    
    private Simulation simulation;
    //relógio lógico
    private Clock clock;
    //cria timelines para cada task
    private Timeline timeline;
    //timeline que criar instances para cada uma task
    private Timeline makeTaskInstances;
    //lista que armazena todos os timelines criados para cada
    private ArrayList<Timeline> timelineTasksInstance;

    TaskInstance currentRunningTask;
    

    /**
     * Método construtor
     */
    public Kernel() {
        this.setName("Kernel");
        status = Status.STOP;
        
    }//fim construtor

    /**
     * Método que adiciona uma outra task ao kernel
     *
     * @param task TaskInstance
     */
    public void add(Task task) {
        tasks.put(task.getName(), task);
    }//fim add

    /**
     * Método que remove num taskInstance do kernel
     *
     * @param num int
     */
    public void remove(int num) {
        for (int i = 0, j = tasks.size(); i < num; i++, j--) {
            tasks.remove("T" + Integer.toString(j));
        }
    }//fim remove

    /**
     * Método que retorna a quantidade de tasksIntance do kernel
     *
     * @return int
     */
    public int size() {
        return tasks.size();
    }//fim size

    /**
     * Ação do kernel
     */
    boolean scheduling = false;
    @Override
    public void run() {
        status = Status.RUNNING;
        tasksInstances = new ArrayList<>();
        timelineTasksInstance = new ArrayList<>();
        ArrayList <Task> ts = new ArrayList<>();
        ts.addAll(tasks.values());
        
        
        switch (schedulingAlgoritm) {
            case EDF:
                Collections.sort(ts, new EDFComparatorT());
                break;
            case RM:
                Collections.sort(ts, new RateMonotonicComparator());
                break;
            case LST:
                Collections.sort(ts, new LSTComparator());
                break;
            case DM:
                Collections.sort(ts, new DMComparator());
        }
        
        //time=Calendar.getInstance().get(Calendar.SECOND);
        clock = new Clock(simulation);
        clock.start();

        
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), (k) -> {
            ts.forEach((ta) -> {                
                makeTaskInstances = new Timeline(new KeyFrame(Duration.seconds(ta.getPeriod()), (k1) -> {
                    TaskInstance o1 = new TaskInstance(ta, ta.getIndex(), clock.get(), this);
                    tasksInstances.add(o1);
                    simulation.addSeriesToChart(o1.getSeriesData());
                    TaskInstanceView tview1 = new TaskInstanceView(o1);
                    simulation.getPanelTaskInstance().getChildren().add(tview1);                  
                    o1.setInstanceView(tview1);
                    if(scheduling || tasksInstances.size()==tasks.size()){
                       schedulingTask();
                       scheduling=true;
                    }//fim if
                }));//fim timeline
                timelineTasksInstance.add(makeTaskInstances);
                makeTaskInstances.setCycleCount(this.getSimulationTime());
                makeTaskInstances.play();
                makeTaskInstances.jumpTo(Duration.millis(ta.getPeriod()*999));    
            });//fim for-each
        }));
        timeline.play();
        timeline.jumpTo(Duration.seconds(1));

    }//fim run

    /**
     * Método que modifica o algoritmo de escalonamento do kernel
     *
     * @param algorithm
     */
    public void setSchedulingAlgorithm(SCHEDULING_ALGORITHM algorithm) {
        this.schedulingAlgoritm = algorithm;
    }//fim setSchedulingAlgorithm

    /**
     * Método que retorna o nome do algoritmo de escalonamento atual
     *
     * @return String
     */
    public SCHEDULING_ALGORITHM getSchedulingAlgorithm() {
        return schedulingAlgoritm;
    }//fim getSchedulingAlgorithm

    /**
     * Método que analisa todos os tasksvies da GUI e atualiza as informações
     * das task, (computeTime, deadline e period)
     *
     * @return true se tudo ocorreu bem false, caso contrário
     */
    public boolean updateTasksInfo() {
        for (Task t : tasks.values()) {
            if (!t.update()) {
                return false;
            }//fim if
        }//fim for
        return true;
    }//fim updateTasksInfo

    /**
     * verifica se o conjunto de tarefa é escalonável de acordo com a política
     * de escolamento atual.
     *
     * @return true : se for escalonável false : caso contrário
     */
    public boolean isScheduling() {
        switch (this.schedulingAlgoritm) {
            case EDF:
                return edfTest();
            case RM:
                return rateMonotonicTest();
            case LST:
                return lstTest();
            case DM:
                return edfTest();
        }//fim switch
        return false;
    }//fim isScheduling

    /**
     * Método que realiza o teste de escalonabilidade para a política EDF
     *
     * @return true se o conjunto de tarefas é escalonável false caso contrário
     */
    private boolean edfTest() {
        float taxaProcessador_dEqualp = 0;
        float taxaProcessador_dLowerp = 0;
        float taxaProcessador_dArbitrario = 0;
        boolean dEqualp = true;
        boolean dLowerp = true;

        /**
         * Condições para o cálculo: - obs: o somatório é realizado para todas
         * as tarefas (1..n), sendo n o número de tarefas a) Se o deadline for
         * igual ao período taxa = somatório (tempoComputação / período) b) Se o
         * deadline for menor que o período taxa = somatório (tempoComputação /
         * deadline) c) para valores arbitrários de deadline taxa = somatório
         * (tempoComputacao / (min(deadline, período)))
         *
         * O conjunto de tarefas é escalonável se a taxa for menor ou igual a 1
         */
        for (Task t : tasks.values()) {
            dEqualp &= (t.getDeadline() == t.getPeriod());
            dLowerp &= (t.getDeadline() < t.getPeriod());

            if (dEqualp) {
                taxaProcessador_dEqualp += (t.getComputeTime() / t.getPeriod());
            }//fim if
            if (dLowerp) {
                taxaProcessador_dLowerp += (t.getComputeTime() / t.getDeadline());
            }//fim if

            float a = t.getComputeTime();
            float b = Math.min(t.getDeadline(), t.getPeriod());
            float c = a / b;
            taxaProcessador_dArbitrario += c;
        }//fim for
        float taxa = (dEqualp) ? taxaProcessador_dEqualp : ((dLowerp) ? taxaProcessador_dLowerp : taxaProcessador_dArbitrario);
        simulation.setRateProcessor(taxa);
        return taxa <= 1;
    }//fim edfTest

    /**
     * Método que realiza o teste de escalonabilidade para a política Rate
     * Monotonic
     *
     * @return true se o conjunto de tarefas é escalonável false caso contrário
     */
    private boolean rateMonotonicTest() {
        float taxa = 0;
        /**
         * Um conjunto de tarefas é escalonável segundo a política rate
         * monotonic se o somatório de tempoComputação/período de todas as
         * tarefas for menor ou igual a:
         *
         * n * (2^(1/n) -1)
         *
         * onde n é o número de tarefas
         */

        float n = tasks.size();
        double limit = n * (Math.pow(2, 1 / n) - 1);

        for (Task t : tasks.values()) {
            float a = t.getComputeTime();
            float b = t.getPeriod();
            taxa += a / b;
        }//fim for
        simulation.setRateProcessor(taxa);
        return taxa <= limit;
    }//fim rateMonotonicTest

    /**
     * Método que realiza o teste de escalonabilidade para a política lstTest
     *
     * @return true se o conjunto de tarefas é escalonável false caso contrário
     */
    private boolean lstTest() {
        return edfTest();
    }//fim lstTest

    /**
     * Método que modifica o objeto responsável pelo controle de simulação
     *
     * @param simulation Simulation
     */
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }//fim setSimulation

    /**
     * Método que escalona uma task
     */
    public void schedulingTask() {
        
        if (!tasksInstances.isEmpty() && tasksInstances.get(0).isFinish()) {
            tasksInstances.remove(0);
        } else if (!tasksInstances.isEmpty()) {
            tasksInstances.get(0).preempt();
        }//fim if

        if (tasksInstances.isEmpty()) {
            currentRunningTask = null;
            return;
        }
        switch (schedulingAlgoritm) {
            case EDF:
                Collections.sort(tasksInstances, new EDFComparator());
                break;
            case RM:
                Collections.sort(tasksInstances, new RateMonotonicComparator());
                break;
            case LST:
                Collections.sort(tasksInstances, new LSTComparator());
                break;
            case DM:
                Collections.sort(tasksInstances, new DMComparator());
        }

        if (!tasksInstances.get(0).isActived()) {
            tasksInstances.get(0).active();
            tasksInstances.get(0).start();
        } else {
            tasksInstances.get(0).resumeTask();
        }//fim if-else        
        currentRunningTask = tasksInstances.get(0);
    }//fim schedulingTask

    /**
     * Método que retorna o tempo atual do relógio lógico
     *
     * @return int
     */
    public int getTimeline() {
        return clock.get();
    }//fim getTimeline

    /**
     * Método que atualiza o tamanho do gráfico
     */
    public void updateChartWidth() {
        this.simulation.updateChartWidth();
    }//fim updateChartWidth

    /**
     * Método que retoma a execução do kernel
     *
     * @return true se conseguiu retomar a execução do objeto, false caso
     * contrário
     */
    public boolean resumeKernel() {
        try {
            //parando o relógio
            if (clock != null && clock.getStatus().equals(Animation.Status.PAUSED)) {
                clock.start();
            }//fim if
            //parando o timeline
            if (timeline != null && timeline.getStatus().equals(Animation.Status.PAUSED)) {
                timeline.play();
            }//fim if
            //parando o makeTaksInstance
            if (makeTaskInstances != null && makeTaskInstances.getStatus().equals(Animation.Status.PAUSED)) {
                makeTaskInstances.play();
            }//fim if
            //parando todos os timelines criados
            timelineTasksInstance.forEach((t) -> {
                if (t != null && t.getStatus().equals(Animation.Status.PAUSED)) {
                    t.play();
                }//fim if
            });
            //retomando todas as tarefas               
            tasksInstances.forEach((t) -> {
                t.resumeTask();
            });
            //suspendendo o kernel
            this.resume();
            status = Status.RUNNING;
            return true;
        } catch (Exception e) {
            return false;
        }//fim try-catch
    }

    /**
     * Método que pausa a execução do kernel
     *
     * @return true se conseguiu pausar o objeto, false caso contrário
     */
    boolean pause() {
        try {
            //parando o relógio
            if (clock != null && clock.getStatus().equals(Animation.Status.RUNNING)) {
                clock.pause();
            }//fim if
            //parando o timeline
            if (timeline != null && timeline.getStatus().equals(Animation.Status.RUNNING)) {
                timeline.pause();
            }//fim if
            //parando o makeTaksInstance
            if (makeTaskInstances != null && makeTaskInstances.getStatus().equals(Animation.Status.RUNNING)) {
                makeTaskInstances.pause();
            }//fim if
            //parando todos os timelines criados
            timelineTasksInstance.forEach((t) -> {
                if (t != null && t.getStatus().equals(Animation.Status.RUNNING)) {
                    t.pause();
                }//fim if
            });
            //preemptando todas as tarefas               
            tasksInstances.forEach((t) -> {
                t.preempt();
            });
            //suspendendo o kernel
            this.suspend();
            status = Status.PAUSED;
            return true;
        } catch (Exception e) {
            return false;
        }//fim try-catch
    }//fim pause

    /**
     * Método que retorna o status do kernel
     *
     * @return int
     */
    public Status getStatusKernel() {
        return status;
    }//fim getStatusKernel

    /**
     * Método que para uma execução atual do kernel
     * @return boolean
     */
    public boolean stopKernel(boolean all) {
        try {
            if (clock != null && all) {
                clock.stop();
            }//fim if
            if (timeline != null) {
                timeline.stop();
            }//fim if
            if (makeTaskInstances != null) {
                makeTaskInstances.stop();
            }//fim if
            //parando todos os timelines criados
            timelineTasksInstance.forEach((t) -> {
                if (t != null && t.getStatus().equals(Animation.Status.RUNNING)) {
                    t.stop();
                }//fim if
            });
            
            
            //preemptando todas as tarefas               
            if(all){
                tasksInstances.forEach((t) -> {
                    t.stopTaskInstance();
                });
            }

            timelineTasksInstance.clear();
            
            if(all)
                tasksInstances.clear();

            tasks.values().forEach((t)->{t.clean();});
            
            //suspendendo o kernel
            this.stop();
            status = Status.STOP;
            return true;
        } catch (Exception e) {
            return false;
        }
    }//fim stopKernel
    
    /**
     * Método que retorna o relógio da simulação
     * @return Clock
     */
    public Clock getClock(){
        return clock;
    }//fim getclock
    
    /**
     * Método que calcula o tempo de execução da simulação
     * @return int
     */
     private int calMMC(){
        int [] nums = new int[tasks.size()];
        
        {
            int j=0;
            for(Task t : tasks.values())
                nums[j++] = t.getPeriod();
        }
        
        int fator=2;
        int mmc=1;
   
        boolean enable=true;
        boolean incFator=true;
        boolean mudarMMC = false;
        
        while(enable){
            
            for(int i=0; i<nums.length; i++){
               if(nums[i] == 0){
                    return 0;                   
               }//fim if
               else if(nums[i] == 1) continue;
                              
                if(nums[i]%fator==0){
                    nums[i]=nums[i]/fator;
                    mudarMMC = true;
                    incFator = false;
                }//fim if
            }//fim for
            if(mudarMMC){
                mmc*=fator;
                mudarMMC = false;
            }//fim if
            if(incFator){
                fator++;
            }//fim if
            incFator = true;
            
            enable=false;
            for(int n: nums){
                if(n!=1)
                    enable=true;
            }//fim for
        }//fim while
        return  mmc;       
     }//fim calMMC
   
     /**
      * Método que calcula o tempo de execução da simulação para determinado conjunto
      * de tasks
      * @return  int
      */
    public int getSimulationTime() {
        return saturation(calMMC());
    }//fim getSimulationTime
    
    private int saturation(int num){
        return (num > maxSimulationTime) ? maxSimulationTime : (num<minSimulationTime) ? minSimulationTime : num;
    }//fim saturation 
    
    /**
     * Método que retorno o fator de velocidade da animação
     * @return float
     */
    public float getSpeedFator() {
        return simulation.getSpeedFator();
    }//fim getSpeedFator
}
