/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Entity.Kernel;
import Entity.Simulation;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.function.ToIntFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javax.imageio.ImageIO;

/**
 * FXML Controller class
 *
 * @author shanks
 */
public class ViewController implements Initializable {

    private Simulation simulation;

    @FXML private TextField tfTaskNumber;
    @FXML private ChoiceBox boxAlgorithm;
    @FXML private Button bStartSimulation;
    @FXML private HBox panelTask;
    @FXML private AreaChart chart;
    @FXML private HBox panelTaskInstance;
    @FXML private ScrollPane scrollChart;
    @FXML private Label labelAlert;
    @FXML private Button btSalveAsPng;
    @FXML private Button btStop;
    @FXML private Button btPause;
    @FXML private Button btAdd;
    @FXML private Button btSub;
    @FXML private TextField tfSimulationTime;
    @FXML private TextField tfRateProcessor;
    @FXML private ProgressBar progressIndicator;

    public static enum MESSAGE_TYPE {
        WARNING, OK, ERROR, INFORMATION
    };

    private boolean controlA = true;

    private ObservableList<XYChart.Series<String, Integer>> data;

    /**
     * Initializes the controller class.
     *
     * @param url URL
     * @param rb ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        data = FXCollections.observableArrayList();

        simulation = new Simulation(this);

        btSalveAsPng.setTooltip(new Tooltip("Salva um snapshot da simulação"));
        btStop.setTooltip(new Tooltip("Para uma simulação"));
        btPause.setTooltip(new Tooltip("Pausa uma simulação"));
        btAdd.setTooltip(new Tooltip("Adicionar uma tarefa"));
        btSub.setTooltip(new Tooltip("Remover uma tarefa"));
        //configuração do boxAlgorithm
        boxAlgorithm.setItems(FXCollections.observableArrayList(
                "LSF", "Taxa Monotônica", "EDF", "Deadline Monotônico"));
        boxAlgorithm.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            actionBoxAlgorithm();
        });//fim addListener
        boxAlgorithm.getSelectionModel().select(2);

        //configuração do tfNumberTask
        tfTaskNumber.setText("0");
        tfTaskNumber.setOnMouseClicked((event) -> {
            if (tfTaskNumber.getText().trim().equals("0")) {
                tfTaskNumber.clear();
            }
        });
        tfTaskNumber.setOnKeyReleased((event) -> {
            if (controlA && event.getText().equals("a")) {
                actionTfTaskNumber();
                controlA = false;
            }
        });

        chart.setData(data);
        chart.getYAxis().tickLabelsVisibleProperty().setValue(Boolean.FALSE);
        chart.getXAxis().setTickLength(1.0);

    }//fim initialize

    /**
     * Método que retorna a lista de dados do gráfico
     *
     * @return ObservableList<Series<String, Integer>>
     */
    public ObservableList<Series<String, Integer>> getChartData() {
        return data;
    }//fim getChartData

    /**
     * Método que atualiza o tamanho do gráfico
     */
    public void updateChartWidth() {
        chart.setPrefWidth((chart.getData().size() <= 7) ? 742 : chart.getData().size() * 93);
    }//fim updateChartWidht

    /**
     * Método que adiciona uma lista de dados (representa uma única
     * taskInstance) no gráfico
     *
     * @param t Series<String, Integer>
     */
    public void addSeriesToChart(Series<String, Integer> t) {
        data.add(t);
    }//fim addSeriesToChart

    /**
     * Método que limpa o gráfico
     */
    public void cleanChart() {
        data.clear();
    }//fim cleanChart

    /**
     * Método que pausa uma simulação
     */
    public void actionPauseSimulation() {
        bStartSimulation.setDisable(false);
        btPause.setDisable(true);
        if (simulation.pause()) {
            setAlertMessage("A simulação foi pausada.", MESSAGE_TYPE.INFORMATION);
        }//fim if
    }//fim actionPauseSimulation

    /**
     * Método que cancela uma simulação
     */
    public void actionStopSimulation() {
        btPause.setDisable(true);
        bStartSimulation.setDisable(false);
        btStop.setDisable(true);

        if (simulation.stop(true)) {
            setAlertMessage("A simulação foi parada.", MESSAGE_TYPE.INFORMATION);
        }//fim if

    }//fim actionStopSimulatio

    /**
     * Método executado para inicializar a simulação do escalonamento das
     * tarefas
     */
    public void actionStartSimulation() {
        if (tfTaskNumber.getText().trim().isEmpty() || tfTaskNumber.getText().trim().equals("0")) {
            setAlertMessage("Erro ao iniciar a simulação. É "
                    + "preciso exirtir no mínimo uma tarefa.", MESSAGE_TYPE.ERROR);

            return;
        }//fim if

        if (simulation.start()) {
            //muda o texto do botão
            btSalveAsPng.setDisable(false);
            bStartSimulation.setDisable(true);
            btPause.setDisable(false);
            btStop.setDisable(false);
        }//fim if
    }//fim startSimulation

    /**
     * Método que salva o snapshot da simulação em uma png
     */
    @FXML
    public void saveChartAsPng() {
        btSalveAsPng.setDisable(true);
        setAlertMessage("Salvando snapshot...", MESSAGE_TYPE.INFORMATION);
        Timeline save = new Timeline(new KeyFrame(Duration.millis(100), (k) -> {
            try {
                WritableImage chartImage = chart.snapshot(new SnapshotParameters(), null);
                WritableImage panelImage = panelTaskInstance.snapshot(new SnapshotParameters(), null);

                String extension = ".png";
                String fileChartName = "simulation";
                File fileChart = new File(fileChartName + extension);

                VBox v = new VBox();
                ImageView im = new ImageView(chartImage);
                ImageView im2 = new ImageView(panelImage);
                v.getChildren().add(im);
                v.getChildren().add(im2);
                WritableImage img = v.snapshot(new SnapshotParameters(), null);

                int i = 0;
                while (fileChart.exists()) {
                    fileChart = new File(fileChartName + (i++) + extension);
                }

                ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", fileChart);
                Process pro = Runtime.getRuntime().exec("shotwell " + fileChart.getPath());
                try {
                    pro.waitFor();
                } catch (InterruptedException ex) {
                }
                setAlertMessage("Snapshot salvo com sucesso", MESSAGE_TYPE.INFORMATION);
            } catch (Exception e) {
                setAlertMessage("Erro ao salvar os snapshots da simulação", MESSAGE_TYPE.ERROR);
            }//fim try-catch

        }));
        save.play();
    }//fim saveChartAsPNG

    /**
     * Método executado quando há uma alteração no número de tarefas simuladas
     */
    public void actionTfTaskNumber() {
        try {
            if (tfTaskNumber.getText().trim().equals("a")) {
                simulation.initDefaultTasks();
                tfTaskNumber.setText(Integer.toString(simulation.getSize()));
            } else {
                int newAmount = Integer.parseInt(tfTaskNumber.getText().trim());
                simulation.setTaskNumber(newAmount);
            }
            setSimulationTime(simulation.getSimulationTime());
            setAlertMessage("Tarefas criadas com sucesso", MESSAGE_TYPE.INFORMATION);
        } catch (NumberFormatException e) {
            setAlertMessage("Erro ao modificar a quantidade de tarefas simuladas", MESSAGE_TYPE.ERROR);
        }//fim try-catch
        // simulation.setTaskNumber();
    }//fim actionTfTaskNumber

    /**
     * Método executado quando há uma alteração no algoritmo de escalonamento de
     * tarefas
     */
    public void actionBoxAlgorithm() {
        int indexAlgorithm = boxAlgorithm.getSelectionModel().getSelectedIndex();
        switch (indexAlgorithm) {
            case 0:
                simulation.setSchedulingAlgorithm(Kernel.SCHEDULING_ALGORITHM.LST);
                break;
            case 1:
                simulation.setSchedulingAlgorithm(Kernel.SCHEDULING_ALGORITHM.RM);
                break;
            case 2:
                simulation.setSchedulingAlgorithm(Kernel.SCHEDULING_ALGORITHM.EDF);
                break;
            case 3:
                simulation.setSchedulingAlgorithm(Kernel.SCHEDULING_ALGORITHM.DM);
        }//fim switch        
    }//fim actionBoxAlgorithm

    /**
     * Método executado quando o botão - é pressionado, adicionando uma task do
     * escalonador
     */
    public void actionButtonAdd() {
        try {
            String text = tfTaskNumber.getText().trim();
            int num = (text.isEmpty()) ? 0 : Integer.parseInt(text);
            tfTaskNumber.setText(Integer.toString(++num));
            actionTfTaskNumber();
            setAlertMessage("Tarefas criadas com sucesso", MESSAGE_TYPE.INFORMATION);
        } catch (Exception e) {
            setAlertMessage("Erro ao modificar a quantidade de tarefas simuladas", MESSAGE_TYPE.ERROR);
        }
    }//fim actionButtonAdd

    /**
     * Método executado quando o botão - é pressionado, removendo uma task do
     * escalonador
     */
    public void actionButtonSub() {
        try {
            String text = tfTaskNumber.getText().trim();
            int num = (text.isEmpty()) ? 0 : Integer.parseInt(text);
            num = (num == 0) ? 0 : --num;
            tfTaskNumber.setText(Integer.toString(num));
            actionTfTaskNumber();
            setAlertMessage("Tarefas removida com sucesso", MESSAGE_TYPE.INFORMATION);
        } catch (Exception e) {
            setAlertMessage("Erro ao modificar a quantidade de tarefas simuladas", MESSAGE_TYPE.ERROR);
        }
    }//fim actionButtonSub

    /**
     * Método que retorna o panelTasksIntance
     *
     * @return HBox
     */
    public HBox getPanelTaskInstance() {
        return panelTaskInstance;
    }//fim getPAnelTaskInstance

    /**
     * Método que retorna o panelTask
     *
     * @return HBox
     */
    public HBox getPanelTask() {
        return panelTask;
    }//fim getPanelTask

    /**
     * Método que imprime um alerta na tela principal
     *
     * @param message String
     * @param type int
     */
    public void setAlertMessage(String message, MESSAGE_TYPE type) {
        labelAlert.setText(message);
        switch (type) {
            case WARNING:
                labelAlert.setStyle("-fx-text-fill:#cd8500;-fx-border-color:darkgray");
                break;
            case INFORMATION:
                labelAlert.setStyle("-fx-text-fill:#00688b;-fx-border-color:darkgray");
                break;
            case ERROR:
                labelAlert.setStyle("-fx-text-fill:#ff0000;-fx-border-color:darkgray");
                break;
            case OK:
                labelAlert.setStyle("-fx-text-fill:#00cd66;-fx-border-color:darkgray");
                break;
        }//fim switch
    }//fim setAlert

    /**
     * Método que limpa o painel de intâncias de tasks
     */
    public void cleanPanelInstanceTask() {
        TaskInstanceView.clean();
        panelTaskInstance.getChildren().clear();
    }//fim cleanPanelInstanceTask

    /**
     * Método que mostra na interface o tempo de simulação total
     *
     * @param simulationTime int
     */
    public void setSimulationTime(int simulationTime) {
        tfSimulationTime.setText(Integer.toString(simulationTime));
    }   //fim setSimulationTime

    /**
     * Método que atualiza a barra de progresso da janela
     *
     * @param i int: progresso atual
     */
    public void updateProgressBar(double i) {
        progressIndicator.setProgress(i);
    }//fim updateProgressBar

    /**
     * Método que mostra a taxa de uso do processador na tela
     *
     * @param rate float
     */
    public void setRateProcessor(float rate) {
        tfRateProcessor.setText(Float.toString(rate));
    }//fim setRateProcessor
}
