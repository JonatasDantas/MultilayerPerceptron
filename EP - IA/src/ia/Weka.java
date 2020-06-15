package ia;

import java.io.File;
import java.io.IOException;

import weka.core.converters.CSVLoader;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;

public class Weka {
	
	public static CSVLoader loader(String filepath) throws IOException {// Lê uma fonte que está no formato separado por vírgula
		
		CSVLoader trainloader = new CSVLoader();
		
		trainloader.setSource(new File(filepath));//Redefine o objeto Loader e define a origem do conjunto de dados com o objeto File fornecido.
		trainloader.setNoHeaderRowPresent(true);
		
		return trainloader;
	}
	
	public static Instances instancia (CSVLoader loader) throws IOException {//
		
		Instances train = loader.getDataSet();// Return the full data set
		train.setClassIndex(train.numAttributes()-1);// torna o ultimo atributo ser a classe
		
		return train;
	}
	
	public static MultilayerPerceptron inicializaMLP (double taxaAprendizado, double momentum, int epocas, String camadasEscondidas, Instances buildClassifier) throws Exception {
		
		MultilayerPerceptron mlp = new MultilayerPerceptron();
		
		//Setting Parameters
		mlp.setLearningRate(taxaAprendizado);
		mlp.setMomentum(momentum);
		mlp.setTrainingTime(epocas);
		mlp.setHiddenLayers(camadasEscondidas);
		
		/*
		for(int i=0; i<epocas; i++) {
			//printa 	
			mlp.initializeClassifier(buildClassifier);
			System.out.println(mlp.getValidationThreshold());//The threshold used for validation testing
			mlp.next();
		}
		*/
		mlp.buildClassifier(buildClassifier);//train a neural network for the training data provided

		return mlp;
	}
	
	public static Evaluation avalia (MultilayerPerceptron mlp, Instances data) throws Exception {//classe usada para avaliação do treinamento
		
		Evaluation eval = new Evaluation(data);//Instância de avaliação criada com a matriz de custos especificada
	    eval.evaluateModel(mlp, data);
	    
	    System.out.println("Taxa de erro: " + eval.errorRate()); //Printing Training Mean root squared Error
	    System.out.println(eval.toSummaryString("\nResults\n======\n", false)); //Summary of Training
		
	    return eval;
	}
	
	public static void main(String[] args) {
	
		try {
			//Reading training arff or csv file
			CSVLoader trainloader = loader("caracteres-limpo.csv");
		
			Instances train = instancia(trainloader);
	
			//Instance of NN
			MultilayerPerceptron mlp = inicializaMLP(0.8, 0.2, 100, "3", train);
			
			avalia(mlp, train);
			
		}
		
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}
