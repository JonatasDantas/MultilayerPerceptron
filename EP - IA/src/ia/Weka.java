package ia;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import weka.core.converters.CSVLoader;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;

public class Weka {


	public static CSVLoader loader(String filepath) throws IOException {
		
		CSVLoader trainloader = new CSVLoader();
		
		trainloader.setSource(new File(filepath));
		trainloader.setNoHeaderRowPresent(true);
		
		return trainloader;
	}
	
	public static Instances instancia (CSVLoader loader) throws IOException {
		
		Instances train = loader.getDataSet();
		train.setClassIndex(train.numAttributes()-1);
		
		return train;
	}
	
	public static MultilayerPerceptron inicializaMLP (double taxaAprendizado, double momentum, String camadasEscondidas) throws Exception {
		
		MultilayerPerceptron mlp = new MultilayerPerceptron();
		
		//Setting Parameters

//		mlp.setGUI(true);
		mlp.setLearningRate(taxaAprendizado);
		mlp.setMomentum(momentum);
		mlp.setTrainingTime(1);
		mlp.setHiddenLayers(camadasEscondidas);

		return mlp;
	}
	
	public static Evaluation avalia (MultilayerPerceptron mlp, Instances data, OutputStrings os) throws Exception {

		Evaluation eval = new Evaluation(data);
		eval.evaluateModel(mlp, data);

		data.stream().forEach(d -> {
			try {
				Evaluation tmpEval = new Evaluation(data);
				os.outputDataResults += ("\ndado: " + d.toString().replaceAll("^.*,", "") +", resultado: " + tmpEval.evaluateModelOnce(mlp, d));
				if(tmpEval.errorRate()!=0) {
					os.outputDataResults += " ==> ERRADO";
				};
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		os.outputDataResults += "\n\n" + eval.toSummaryString();

	    //System.out.println("Taxa de erro: " + eval.errorRate()); //Printing Training Mean root squared Error
	    //System.out.println(eval.toSummaryString("\nResults\n======\n", false)); //Summary of Training
		//System.out.println(eval.getRevision());


	    return eval;
	}

	public static MultilayerPerceptron treinaRede(MultilayerPerceptron mlp, Instances buildClassifier, int epocas, OutputStrings os) throws Exception {
		mlp.initializeClassifier(buildClassifier);
		os.outputInitWeights += mlp.toString();
		for(int i=0; i<epocas; i++) {
			System.out.println("\n==============================================\n============ Modelo na época " + i + " ==============\n==============================================\n");
			System.out.println(mlp.toString());
			Evaluation eval = new Evaluation(buildClassifier);
			eval.evaluateModel(mlp, buildClassifier);
			os.outputEpochErrors += ("\ntaxa de erro na época " + (i+1) + ": " + eval.errorRate()*100 + "%");
			mlp.next();
		}
		os.outputFinalWeights += mlp.toString();
		return mlp;
	}
	
	public static void main(String[] args) {
		
		double taxaAprendizado = 0.5;
		double momentum = 0.2;
		int epocas = 1000;
		String camadasEscondidas = "30";
		
	
		try {
			String arquivoTreino = "C:/Users/windows/Desktop/Cursos - EACH/IA/Entrega_3/house-votes-84-treino.csv";
			/*if(arquivoTreino.length() == 0) {
				System.out.println("Por favor, informe um caminho para o arquivo de treino com \"-t <caminho_para_arquivo.csv>");
				return;
			}*/
			String arquivoExecucao = "C:/Users/windows/Desktop/Cursos - EACH/IA/Entrega_3/house-votes-84-teste.csv";
			/*if(arquivoTreino.length() == 0) {
				System.out.println("Por favor, informe um caminho para o arquivo de execução com \"-r <caminho_para_arquivo.cvs>");
				return;
			}
			String outputFolderLocation = Utils.getOption('o', args);
			if(outputFolderLocation.length() == 0) {
				System.out.println("Parâmetro de caminho de saída não informado: (-o), utilizando o caminho padrão (pasta atual)");
				;
			}*/
			String outputFolderLocation = ".";

			OutputStrings os = new OutputStrings();

			os.outputInitParams += "Taxa de aprendizado: " + taxaAprendizado;
			os.outputInitParams += "\nmomentum: " + momentum;
			os.outputInitParams += "\nCamadas escondidas, formato(numero de nos da primeira camada, numero de nos da segunda camada...): " + camadasEscondidas;
			os.outputInitParams += "\nÉpocas: " + epocas;

			//Reading training arff or csv file
			CSVLoader trainLoader = loader(arquivoTreino);
			CSVLoader runLoader = loader(arquivoExecucao);

			Instances train = instancia(trainLoader);
			Instances model = instancia(runLoader);

	
			//Instance of NN
			MultilayerPerceptron mlp = inicializaMLP(taxaAprendizado, momentum, camadasEscondidas);
			mlp = treinaRede(mlp, train, epocas, os);
			
			avalia(mlp, model, os);

			os.escreveArquivosDasStrings(outputFolderLocation);
			System.out.println("Treino e execução finalizados, cheque o diretorio \"" + Paths.get(outputFolderLocation).toAbsolutePath() + "\" para ver os resultados");

		}
		
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}

class OutputStrings {
	public String outputInitParams = "Parâmetros de inicialização =========\n\n";
	public String outputInitWeights = "Pesos iniciais =========\n\n";
	public String outputFinalWeights = "Pesos finais =========\n\n";
	public String outputEpochErrors = "Erros cometidos em cada época =========\n\n";
	public String outputDataResults = "Resultado para cada dado de teste com treino ============\n\n";

	public void escreveArquivosDasStrings(String outputFolderLocation) throws IOException{
		escreveArquivo("parametros_inicializacao.log", outputFolderLocation, outputInitParams);
		escreveArquivo("pesos_iniciais.log", outputFolderLocation, outputInitWeights);
		escreveArquivo("pesos_finais.log", outputFolderLocation, outputFinalWeights);
		escreveArquivo("erros_por_epoca.log", outputFolderLocation, outputEpochErrors);
		escreveArquivo("resultado treino.log", outputFolderLocation, outputDataResults);
	}

	public static void escreveArquivo(String nomeArquivo, String diretorioArquivo, String conteudo) throws IOException {

		Path path = Paths.get(diretorioArquivo, nomeArquivo);
		byte[] strToBytes = conteudo.getBytes();

		Files.write(path, strToBytes);

	}

}
