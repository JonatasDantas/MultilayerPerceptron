package ia;

import java.io.*;
import java.util.*;

public class Driver {
	
	/* Printa o resulta de acordo com os inputs */
	public static void printaResultado(List<double[]> resultado) {
		System.out.println("  Input 1    |    Input 2    |    Esperado    |    Resultado  ");
		System.out.println("--------------------------------------------------------------");
		for(int i=0; i < RedeNeural.entradas.size(); i++) {
			System.out.println("-----------------------------------------------");
			for (int j = 0; j < RedeNeural.saidasEsperadas.get(i).length; j++) {
				System.out.print("  " + RedeNeural.saidasEsperadas.get(i)[j] + "       |  " + String.format("%.5f", resultado.get(i)[j]) + "  \n");
            }
		}
	}
	
	public static void main(String[] args) throws IOException {
		RedeNeural redeNeural = new RedeNeural();//Cria nova rede neural
		
		redeNeural.inicializaVariaveis();//Inicializa taxa de Aprendizado, numero de epocas, inputs e entradas
		int qtdSaida;
		if (RedeNeural.entradas.get(0).length > 4) {
			qtdSaida = 7;
		} else {
			qtdSaida = 1;
		}

		//Inicializa os neuronios e cada camada da rede
		redeNeural.inicializaNeuronios(RedeNeural.entradas.get(0).length, RedeNeural.entradas.get(0).length, qtdSaida);
		
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		boolean flag = true;

		while(flag) {
			System.out.println("rodar, treinar ou sair?");
			String comando;
			try {
				comando = bf.readLine();
				switch(comando) {
				
				case "rodar":
					ArrayList<double[]> resultado = new ArrayList<>();

					//Para cada Array de input
					for(int i=0; i < RedeNeural.entradas.size(); i++) {
						Neuronio[] neuronios = redeNeural.forwardprop(RedeNeural.entradas.get(i))
									   .getNeuronios();
						
						double[] result = new double[neuronios.length - (RedeNeural.inputNeuronio + RedeNeural.hiddenNeuronio)];
						
						for (int j = RedeNeural.inputNeuronio + RedeNeural.hiddenNeuronio; j < neuronios.length; j++) {
							result[j - (RedeNeural.inputNeuronio + RedeNeural.hiddenNeuronio)] = neuronios[j].getOutput();
						}

						resultado.add(result);
					};

					printaResultado(resultado);

					break;
					
				case "treinar":
					for(int i=0; i < redeNeural.epocas; i++) {
						System.out.println("[Epoca " + (i+1) +"]");//Epoca comeca em 1
						//Padrao de print
						System.out.println("[Tipo Neuronio, peso 1, peso 2, entrada, output]");
						//Para cada Array de input
						for(int j=0; j < RedeNeural.entradas.size(); j++) {
							//Aplica o backpropagation em cada array de input a partir das saidas esperadas
							System.out.println(redeNeural.forwardprop(RedeNeural.entradas.get(j))
														 .backpropError(RedeNeural.saidasEsperadas.get(j)));
						}
					};
					System.out.println("[Acabou o treino!]");
					break;
					
				case "sair":
					flag = false;
					System.out.println("\nAdeus!");
					break;	
				}
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.exit(0);
	}
	
	

}
