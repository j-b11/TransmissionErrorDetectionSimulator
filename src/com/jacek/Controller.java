package com.jacek;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

public class Controller  implements Initializable {
    @FXML
    private TextArea inputArea;
    @FXML
    private TextArea encodedArea;
    @FXML
    private TextArea disruptedArea;
    @FXML
    private TextArea decodedArea;
    @FXML
    private TextArea disruptedBitsArea;
    @FXML
    private Label numberOfDetectedErrors;
    @FXML
    private Label actualNumberOfErrors;
    @FXML
    private Label algorithmEffectiveness;
    @FXML
    private Label CRCLabel;
    @FXML
    private TextField inputSignalLengthTextField;
    @FXML
    private ComboBox errorDetectingAlgorithmCB;
    @FXML
    private ComboBox disruptionGeneratorCB;

    private ObservableList<String> algorithmList;
    private ObservableList<String> generatorList;
    private SignalGenerator signalGenerator;
    private DisruptionGenerator disruptionGenerator;
    private Algorithm algorithm;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //handle comboBoxes:
        algorithmList = FXCollections.observableArrayList(
                "Even Parity Bit Control",
                "Cyclic Redundancy Check 16Bit",
                "Cyclic Redundancy Check SDLC",
                "Cyclic Redundancy Check SDLC Reverse",
                "Cyclic Redundancy Check 32Bit",
                "Hamming Code"
        );

        generatorList = FXCollections.observableArrayList(
                "One Bit Disruption Generator",
                "Simple Bit Disruption Generator",
                "No Disruption"
        );

        errorDetectingAlgorithmCB.setItems(algorithmList);
        disruptionGeneratorCB.setItems(generatorList);
        errorDetectingAlgorithmCB.getSelectionModel().selectFirst();
        disruptionGeneratorCB.getSelectionModel().selectFirst();
        disruptedBitsArea.setText("Disrupted bits : \n");

        //input signal length must be a number
        inputSignalLengthTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    inputSignalLengthTextField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }


    public void handleSimulationButtonAction(ActionEvent actionEvent) {
        signalGenerator = new SimpleSignalGenerator(Integer.valueOf(inputSignalLengthTextField.getText()));

        switch (errorDetectingAlgorithmCB.getSelectionModel().getSelectedIndex()){
            case 0:
                algorithm = new EvenParityBitControl();
                break;

            case 1:
                algorithm = new CyclicRedundancyCheck16Bit("crc16");
                break;

            case 2:
                algorithm = new CyclicRedundancyCheck16Bit("sdlc");
                break;

            case 3:
                algorithm = new CyclicRedundancyCheck16Bit("sdlc reverse");
                break;

            case 4:
                algorithm = new CyclicRedundancyCheck32Bit("crc32");
                break;

            case 5:
                algorithm = new HammingCode();
                break;

                default:
                    algorithm = new EvenParityBitControl();
                    break;
        }

        switch (disruptionGeneratorCB.getSelectionModel().getSelectedIndex()){
            case 0:
                disruptionGenerator = new OneBitDisruptionGenerator();
                break;

            case 1:
                disruptionGenerator = new SimpleDisruptionGenerator();
                break;

            case 2:
                disruptionGenerator = new NoDisruptionGenerator();
                break;

            default:
                disruptionGenerator = new OneBitDisruptionGenerator();
                break;
        }

        SimulationRunner simulationRunner = new MySimulationRunner(algorithm, signalGenerator, disruptionGenerator);
        Simulation simulation = simulationRunner.runSimulation();

        showSimulationResults(simulation);

    }

    private void showSimulationResults(Simulation simulation){
        //----------input-----------
        String input = "";
        for(byte b : simulation.getInput()){
            input += Integer.toBinaryString(b & 255 | 256).substring(1) + "\n";
        }
        inputArea.setText(input);

        //----------encoded---------
        String encoded = "";
        for(byte b : simulation.getEncoded()){
            encoded += Integer.toBinaryString(b & 255 | 256).substring(1) + "\n";
        }
        encodedArea.setText(encoded);

        //---------disrupted---------
        String disrupted = "";
        for(byte b : simulation.getDisrupted()){
            disrupted += Integer.toBinaryString(b & 255 | 256).substring(1) + "\n";
        }
        disruptedArea.setText(disrupted);

        //-------decoded-----------
        String decoded = "";
        for(byte b : simulation.getDecoded()){
            decoded += Integer.toBinaryString(b & 255 | 256).substring(1) + "\n";
        }
        if(decoded.isEmpty()){
            decodedArea.setText("Data is corrupted !");
        }
        else
            decodedArea.setText(decoded);

        //--------labels----------
        actualNumberOfErrors.setText(String.valueOf(simulation.getActualNumberOfErrors()));

        if(algorithm instanceof CyclicRedundancyCheck16Bit && simulation.getNumberOfErrorsDetected() == 1 ||
                algorithm instanceof CyclicRedundancyCheck32Bit  && simulation.getNumberOfErrorsDetected() == 1){
            numberOfDetectedErrors.setText("1 or more");
            algorithmEffectiveness.setText("100.00 %");
        }
        else {
            numberOfDetectedErrors.setText(String.valueOf(simulation.getNumberOfErrorsDetected()));
            if(Double.isNaN(simulation.getErrorDetectedToActualRatio())){
                algorithmEffectiveness.setText("--");
            }
            else {
                algorithmEffectiveness.setText(String.format("%.2f",
                        simulation.getErrorDetectedToActualRatio() * 100) + "%");
            }

        }

        //printing crcCode if crc algorithm selected
        CRCLabel.setText("");
        if(algorithm instanceof CyclicRedundancyCheck16Bit){
            CRCLabel.setText(((CyclicRedundancyCheck16Bit) algorithm).getCrcCode()) ;
        }

        if(algorithm instanceof CyclicRedundancyCheck32Bit){
            CRCLabel.setText(((CyclicRedundancyCheck32Bit) algorithm).getCrcCode()) ;
        }

        //showing disrupted bits:
        disruptedBitsArea.setText("Disrupted bits : \n");
        disruptedBitsArea.setWrapText(true);
        if(simulation.getDisruptedBits() != null){
            for (Object o : simulation.getDisruptedBits().entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                for (Integer bit : (ArrayList<Integer>) entry.getValue()) {
                    disruptedBitsArea.setText(disruptedBitsArea.getText() + bit + " of byte " + entry.getKey() + ", ");
                }
            }
            disruptedBitsArea.setText(disruptedBitsArea.getText().substring(0, disruptedBitsArea.getText().length()
                    - 2));
        }
        else {
            disruptedBitsArea.setText(disruptedBitsArea.getText() + "----");
        }
    }
}
