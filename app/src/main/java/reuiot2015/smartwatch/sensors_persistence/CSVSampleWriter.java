package reuiot2015.smartwatch.sensors_persistence;

public abstract class CSVSampleWriter implements SampleAccumulator.SampleAccumulationListener {

    //Closes the output stream to the file
    public abstract void release();
    
}
