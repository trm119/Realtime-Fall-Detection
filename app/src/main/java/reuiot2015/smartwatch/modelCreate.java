package reuiot2015.smartwatch;
import java.util.Random;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.ConverterUtils.DataSource;
/**
 * Created by cha on 2017/7/18.
 */

public class modelCreate {


    public static void main(String[] args) throws Exception {

        String inputName = "C:\\Users\\cha\\Desktop\\IOT2016\\model test folder\\ReLabeledWalker.csv";
        String outputName = "C:\\Users\\cha\\Desktop\\IOT2016\\model test folder\\fallsvmmodel.model";
        String testDataName = "C:\\Users\\cha\\Desktop\\IOT2016\\model test folder\\ReLabeledWalker.csv";

        // points to the source of data is that will be trained.
        DataSource source = new DataSource(inputName);

        //prints summary of dataset
        System.out.println(source.getDataSet().toSummaryString());
        //System.out.println(source.getDataSet().toString());

        //This reads in the instances(rows) of csv.
        Instances train = source.getDataSet();
        //This sets which attribute(columns in csv) is the one that will be classified
        if (train.classIndex() == -1)
            train.setClassIndex(train.numAttributes() - 1);

        //prints out number of attributes
        System.out.println(train.numAttributes());

        //create new svm and set the kerneel type to RBFkernel
        LibSVM svm = new LibSVM();
        svm.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_RBF, LibSVM.TAGS_KERNELTYPE));

        // builds and trains classifier
        svm.buildClassifier(train);

        //runs 5 fold crossvalidation on svm and prints results
        Evaluation eval = new Evaluation(train);
        eval.crossValidateModel(svm, train, 5, new Random(1));
        System.out.println(eval.toSummaryString("\nResults\n======\n", true));
        System.out.println(eval.fMeasure(1) + " " + eval.precision(1) + " " + eval.recall(1));
        System.out.println(eval.toMatrixString());

        //uses serialization to save the model in the specified location.
        weka.core.SerializationHelper.write(outputName, svm);
        //uses deserialization to load model from wherever it is saved
        Classifier newsvm = (Classifier) weka.core.SerializationHelper.read(outputName);

        // loads testing data set
        DataSource source2 = new DataSource(testDataName);
        //prints it
        //System.out.println(source2.getDataSet().toString());

        Instances testdata = source2.getDataSet();
        if (testdata.classIndex() == -1)
            testdata.setClassIndex(testdata.numAttributes() - 1);
        // cross validate testdata
        Evaluation eval2 = new Evaluation(testdata);
        eval2.crossValidateModel(newsvm, testdata, 5, new Random(1));
        System.out.println(eval2.toSummaryString("\nResults\n======\n", true));
        System.out.println(eval2.fMeasure(1) + " " + eval2.precision(1) + " " + eval2.recall(1));
        System.out.println(eval2.toMatrixString());

    }

}
