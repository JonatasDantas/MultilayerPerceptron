package ia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class RedeNeural {
	
	static enum TipoNeuronio {I, H, O};//Camada input(I), hidden(H) ou output(O)
	private static double taxaAprendizado;
	public static int inputNeuronio;
	public static int hiddenNeuronio;
	private static double[] biasHidden;
	private static double[] biasOutput;
	private static Neuronio[] neuronios;
	public int epocas;
	public static ArrayList<double[]> saidasEsperadas;
	public static ArrayList<double[]> entradas;//Entradas lidas pelo Leitor
	
	/* Inicializa a taxa de aprendizado, o numero de epocas, as entradas e as saidas esperadas */
	public void inicializaVariaveis() throws IOException {
		Leitor leitor = new Leitor();

		taxaAprendizado = getRandomNoIntervalo(0.1, 1.0);//Numero aleatorio
		epocas = 100;
		saidasEsperadas = leitor.leSaidaEsperada();
		entradas = leitor.leEntrada();
	}
	
	/* Inicializa os neuronios com a quantidade em cada camada */ 
	public void inicializaNeuronios(int qtdInput, int qtdHidden, int qtdOutuput) {
		neuronios = new Neuronio[qtdInput + qtdHidden + qtdOutuput];//Abastece o array de neuronios com as camadas
		biasHidden = new double[qtdHidden];//Abastece o bias com o numero de neuronios na camada escondida
		biasOutput = new double[qtdOutuput];//Abastece o bias com o numero de neuronios na camada de saida
		
		inputNeuronio = qtdInput;//Numero de neuronios na camada escondida
		hiddenNeuronio = qtdHidden;//Numero de neuronios na camada de saida
		
		//Para cada neuronio, identifica qual o tipo e atribui a quantidade de pesos
		for(int i=0; i < (qtdInput + qtdHidden + qtdOutuput); i++) {
			if (i < qtdInput) {
				neuronios[i] = new Neuronio(TipoNeuronio.I,0);
			} else if (i < (qtdInput + qtdHidden)) {
				neuronios[i] = new Neuronio(TipoNeuronio.H, qtdInput);
			} else {
				neuronios[i] = new Neuronio(TipoNeuronio.O, qtdHidden);
			}
		}
		
		//Inicializa os bias com numeros aleatorios entre 0 e 1
		for (int i = 0; i < biasHidden.length; i++) {
			biasHidden[i] = getRandomNoIntervalo(0.0, 1.0);
		}

		for (int i = 0; i < biasOutput.length; i++) {
			biasOutput[i] = getRandomNoIntervalo(0.0, 1.0);
		}
	}
	
	//Getter do array de Neuronios
	public Neuronio[] getNeuronios() {
		return neuronios;
	}
	
	//toString 
	@Override
	public String toString() {
		return Arrays.toString(neuronios);
	}
	
	//Captura um numero aleatorio entre min e max
	private static double getRandomNoIntervalo(double min, double max){
		   double x = (Math.random()*((max-min)+1))+min;
		   return x;
	}
	
	/* Para cada neuronio da rede, identifica seu tipo e caso for da camada:
	 * Input - Apenas seta seu output
	 * Hidden e Output - Atribui o bias e seus pesos*output ao acumulador. 
	 					 Depois aplica a funcao de ativacao com a soma dos pesos */ 
	public RedeNeural forwardprop (double input[]) {
		double somaPesos = 0;
		
		for (int i=0; i<neuronios.length; i++) {
			switch(neuronios[i].getTiponeuronio()) {
				case I:
					neuronios[i].setOutput(input[i]);//Seta output do neuronio de entrada
					break;
				case H:
					somaPesos = biasHidden[i - inputNeuronio];//Captura o bias

					for (int j = 0; j < inputNeuronio; j++) {
						//Pondera o output atraves de cada peso e joga no acumulador
						somaPesos += neuronios[i].getPeso()[j] * neuronios[j].getOutput();//VALOR ESTRANHO AQUI! neuronios[j].getOutput()
					}
					
					//Aplica a funcao de ativacao com a soma das ponderacoes
					neuronios[i].aplicaFuncaoAtivacao(somaPesos);
					break;
				case O:
					somaPesos = biasOutput[i - (inputNeuronio + hiddenNeuronio)];//Captura o bias
					
					for (int j = inputNeuronio; j < (inputNeuronio + hiddenNeuronio); j++) {
						//Pondera o output atraves de cada peso e joga no acumulador
						somaPesos += neuronios[i].getPeso()[j - inputNeuronio] * neuronios[j].getOutput();
					}
					//Aplica a funcao de ativacao com a soma das ponderacoes
					neuronios[i].aplicaFuncaoAtivacao(somaPesos);
					break;
			}
		}
		
		return this;
	}
	
	public RedeNeural backpropError (double[] valEsperado) {
		// Passo 6 - backpropagation camada de saída.
		//Para cada neuronio da camada de saida
		
		int contador = 1;
		for (int i = neuronios.length -1; i >= inputNeuronio + hiddenNeuronio; i--) {
			//Seta o erro -> Derivada de (Valor esperado - Valor obtido)
			neuronios[i].setError((valEsperado[valEsperado.length - contador] - neuronios[i].getOutput()) * neuronios[i].calculaDerivada());
			//Nova entrada -> Entrada atual + taxa de aprendizado multiplicado pelo seu erro
			neuronios[i].setEntrada(neuronios[i].getEntrada() + taxaAprendizado * neuronios[i].getError());
			
			//Para cada peso obtido da camada escondida
			for (int j = 0; j < hiddenNeuronio; j++) {
				//Correcao de peso -> Peso atual + taxa de aprendizado * erro * output do neuronio da camada anterior
				neuronios[i].getPeso()[j] = neuronios[i].getPeso()[j] + taxaAprendizado * neuronios[i].getError() 
											* neuronios[j + hiddenNeuronio].getOutput();
			}
			//Atualiza bias -> Bias atual (Camada de saida) + taxa de aprendizada * erro obtido
			biasOutput[i - (inputNeuronio + hiddenNeuronio)] = biasOutput[i - (inputNeuronio + hiddenNeuronio)] + 
															   (taxaAprendizado * neuronios[i].getError());
			contador++;
		}

		// Passo 7 - backpropagation camada escondida.
		//Para cada neuronio da camada escondida
		for (int i = inputNeuronio; i < inputNeuronio + hiddenNeuronio; i++) {
			double somaErro = 0;//Acumulador

			for (int j = neuronios.length -1; j >= inputNeuronio + hiddenNeuronio; j--) {
				//Joga o erro ponderado de cada peso para somaErro
				somaErro += neuronios[j].getPeso()[i - inputNeuronio] * neuronios[j].getError();
			}
			//Seta Erro -> Erro acumulado * Derivada da funcao de ativacao
			neuronios[i].setError(somaErro * neuronios[i].calculaDerivada());
			//Nova entrada -> Entrada atual + taxa de aprendizado multiplicado pelo seu erro
			neuronios[i].setEntrada(neuronios[i].getEntrada() + taxaAprendizado * neuronios[i].getError());

			for (int j = 0; j < inputNeuronio; j++) {
				//Correcao de peso -> Peso atual + taxa de aprendizado * erro * output do neuronio da camada anterior
				neuronios[i].getPeso()[j] = neuronios[i].getPeso()[j] + taxaAprendizado * neuronios[i].getError() * 
											neuronios[j].getOutput();
			}
			//Atualiza bias -> Bias atual (Camada escondida) + taxa de aprendizada * erro obtido
			biasHidden[i - inputNeuronio] = biasHidden[i - inputNeuronio] + (taxaAprendizado * neuronios[i].getError());
		}

		return this;
	}
}
