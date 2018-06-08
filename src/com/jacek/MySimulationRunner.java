package com.jacek;



public class MySimulationRunner implements SimulationRunner{

    private Algorithm algorithm;
    private SignalGenerator signalGenerator;
    private DisruptionGenerator disruptionGenerator;

    public MySimulationRunner(Algorithm algorithm, SignalGenerator signalGenerator, DisruptionGenerator disruptionGenerator) {
        this.algorithm = algorithm;
        this.signalGenerator = signalGenerator;
        this.disruptionGenerator = disruptionGenerator;
    }

    public Simulation runSimulation(){

        Simulation simulation = new Simulation();
        byte[] temp;

        temp = signalGenerator.generateSignal();
        simulation.setInput(temp);

        temp = algorithm.encode(temp);
        simulation.setEncoded(temp);

        temp = disruptionGenerator.disrupt(temp);
        simulation.setDisrupted(temp);
        simulation.setActualNumberOfErrors(disruptionGenerator.getNumberOfCreatedErrors());
        simulation.setDisruptedBits(disruptionGenerator.getDisruptedBits());

        temp = algorithm.decode(temp);
        simulation.setDecoded(temp);
        simulation.setNumberOfErrorsDetected(algorithm.getErrorsCount());

        return simulation;
    }

}
