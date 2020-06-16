package ia;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import weka.core.Utils;
import weka.core.converters.CSVLoader;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;

public class Weka {


	// Função responsável por carregar um arquivo CSV através de um caminho (filePath)
	public static CSVLoader loader(String filepath) throws IOException {
		
		CSVLoader loader = new CSVLoader();

		loader.setSource(new File(filepath));
		loader.setNoHeaderRowPresent(true);
		
		return loader;
	}

	// Função responsável por transformar os carregamentos CSV em instâncias para serem lidas pelo classificador
	public static Instances instancia (CSVLoader loader) throws IOException {
		
		Instances train = loader.getDataSet();
		train.setClassIndex(train.numAttributes()-1);
		
		return train;
	}

	// Inicializa o Classificador do tipo MultiLayerPerceptron
	public static MultilayerPerceptron inicializaMLP (double taxaAprendizado, double momentum, String camadasEscondidas) throws Exception {
		
		MultilayerPerceptron mlp = new MultilayerPerceptron();

//		mlp.setGUI(true);
		mlp.setLearningRate(taxaAprendizado);
		mlp.setMomentum(momentum);
		mlp.setTrainingTime(1);
		mlp.setHiddenLayers(camadasEscondidas);

		return mlp;
	}

	// Função responsável por avaliar um conjunto de dados para o modelo treinado
	public static Evaluation avalia (MultilayerPerceptron mlp, Instances data, OutputStrings os) throws Exception {

		Evaluation eval = new Evaluation(data);
		eval.evaluateModel(mlp, data);

		//Passa por cada Dado para avaliar o resultado gerado
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

	    System.out.println(eval.toSummaryString("\nResults\n======\n", false)); //Summary of Training


	    return eval;
	}

	//Função responsável por treinar a rede neural
	public static MultilayerPerceptron treinaRede(MultilayerPerceptron mlp, Instances buildClassifier, int epocas, OutputStrings os) throws Exception {
		mlp.initializeClassifier(buildClassifier);
		os.outputInitWeights += mlp.toString();
		System.out.println("Inicializando o treino...\n\n");

		// Itera sobre cada época
		for(int i=0; i<epocas; i++) {
			System.out.println("\n==============================================\n============ Modelo na época " + i + " ==============\n==============================================\n");
			System.out.println(mlp.toString());
			Evaluation eval = new Evaluation(buildClassifier);
			eval.evaluateModel(mlp, buildClassifier);
			os.outputEpochErrors += ("\ntaxa de erro na época " + (i+1) + ": " + eval.errorRate()*100 + "%");
			System.out.println("\ntaxa de erro na época " + (i+1) + ": " + eval.errorRate()*100 + "%");
			mlp.next();
		}
		os.outputFinalWeights += mlp.toString();
		return mlp;
	}


	
	public static void main(String[] args) {

		try {

		    // Pega taxa de aprendizado do argumento -A ou mantem valor padrão (0.8)
		    double taxaAprendizado = 0.8;
            String taxaAprendizadoInput = (Utils.getOption('A', args));
            if(taxaAprendizadoInput.length() != 0) {
                taxaAprendizado = Double.parseDouble(taxaAprendizadoInput);
            }

            // Pega momentum do argumento -M ou mantem valor padrão (0.2)
            double momentum = 0.2;
            String momentumInput = (Utils.getOption('M', args));
            if(momentumInput.length() != 0) {
                momentum = Double.parseDouble(momentumInput);
            }

            // Pega o número de épocas do argumento -E ou mantem valor padrão (100)
            int epocas = 100;
            String epocasInput = (Utils.getOption('E', args));
            if(epocasInput.length() != 0) {
                epocas = Integer.parseInt(epocasInput);
            }

            // Pega padrão de camadas escondidas do argumento -C ou mantem valor padrão (6)
            String camadasEscondidas = (Utils.getOption('C', args));
            if(camadasEscondidas.length() == 0) {
                camadasEscondidas = "6";
            } else {
                if(!Pattern.compile("^([0-9]+,)*[0-9]+$").matcher(camadasEscondidas).matches()) {
                    throw new NumberFormatException("formato inválido para camadas, tente seguir o padrão ^([0-9]+,)*[0-9]+$");
                }
            }

            // Pega caminho do arquivo a ser utilizado no treino
			String arquivoTreino = Utils.getOption('t', args);
			if(arquivoTreino.length() == 0) {
				System.out.println("Por favor, informe um caminho para o arquivo de treino com \"-t <caminho_para_arquivo.csv>\"");
				return;
			}

			//Pega caminho do arquivo a ser utilizado na execução
			String arquivoExecucao = Utils.getOption('r', args);
			if(arquivoTreino.length() == 0) {
				System.out.println("Por favor, informe um caminho para o arquivo de execução com \"-r <caminho_para_arquivo.cvs>\"");
				return;
			}

			// Pega diretório de despejo dos logs
			String outputFolderLocation = Utils.getOption('o', args);
			if(outputFolderLocation.length() == 0) {
				System.out.println("Parâmetro de caminho de saída não informado: (-o), utilizando o caminho padrão (pasta atual)");
				outputFolderLocation = ".";
			}

			// Instancia classe anônima para gravar os Logs
			OutputStrings os = new OutputStrings();

			// Define atributos iniciais da classe anônima
			os.outputInitParams += "Taxa de aprendizado: " + taxaAprendizado;
			os.outputInitParams += "\nmomentum: " + momentum;
			os.outputInitParams += "\nCamadas escondidas, formato(numero de nos da primeira camada, numero de nos da segunda camada...): " + camadasEscondidas;
			os.outputInitParams += "\nÉpocas: " + epocas;

			// Lê arquivos de teste com função responsável pelo carregamento
			CSVLoader trainLoader = loader(arquivoTreino);
			CSVLoader runLoader = loader(arquivoExecucao);

			// Cria as instâncias de treino e de modelo, necessárias para o treinamento do algoritmo
			Instances train = instancia(trainLoader);
			Instances model = instancia(runLoader);

	
			// Instancia o classificador passando os parâmetros iniciais
			MultilayerPerceptron mlp = inicializaMLP(taxaAprendizado, momentum, camadasEscondidas);
			mlp = treinaRede(mlp, train, epocas, os);

			// Avalia o modelo passado para execução
			avalia(mlp, model, os);

			// Dá output nos logs de execução
			os.escreveArquivosDasStrings(outputFolderLocation);
			System.out.println("Treino e execução finalizados, cheque o diretorio \"" + Paths.get(outputFolderLocation).toAbsolutePath() + "\" para ver os resultados");

		}
		catch(NumberFormatException ne) {
			System.out.println("Formato de número inválido!");
			ne.printStackTrace();
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
