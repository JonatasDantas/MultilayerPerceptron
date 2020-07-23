package ia;

import java.io.*;
import java.util.*;

public class Driver {
	
	static String[] caract = {"A", "B", "C", "D", "E", "J", "K"};
	static String[] data = {"republican","democrat"};
//	static int limite;
//	static int minimo = 0; 
	
	/* Printa o resulta de acordo com os inputs */
	public static void printaResultado(ArrayList<double[]> resultado) {
		System.out.println("  Input 1    |    Input 2    |    Esperado    |    Resultado  ");
		System.out.println("--------------------------------------------------------------");
		for(int i=0; i < RedeNeural.entradas.size(); i++) {
			for(int j=0; j < RedeNeural.entradas.get(0).length; j++) {
				System.out.print("     " + RedeNeural.entradas.get(i)[j] + "     |   ");
				
			}
			for (int j = 0; j < RedeNeural.saidasEsperadas.get(i).length; j++) {
				System.out.print("  " + RedeNeural.saidasEsperadas.get(i)[j] + "       |  " + 
				String.format("%.5f", resultado.get(i)[j]) + "  \n");
			}
		}
	}
	
	/* Printa o resulta de acordo com os inputs para caracteres */
	public static void printaCaractere(List<double[]> resultado) {
		
		System.out.println(" Input    |    Resultado  ");
		System.out.println("---------------------------");
		for(int i=0; i < RedeNeural.entradas.size(); i++) {
			System.out.println("-------------------------------------");
			for (int j = 0; j < RedeNeural.saidasEsperadas.get(i).length; j++) {
				if(Leitor.tipo.equals("caracteres"))
					System.out.print("  " + RedeNeural.saidasEsperadas.get(i)[j] + " - " + caract[j] + "       |  " + String.format("%.5f", resultado.get(i)[j]) + "  \n");
				else System.out.print("  " + RedeNeural.saidasEsperadas.get(i)[j] + " - " + data[j] + "  |  " + String.format("%.5f", resultado.get(i)[j]) + "  \n");
			}
			System.out.println("\n Caractere de saida: " + capturaLetra(resultado.get(i)) + "\n");
		}
		
	}
	
	
	/*Captura o resultado mais próximo do esperado (1) e retorna seu caractere */ 
	public static String capturaLetra(double[] array) {
		double max = 0.0;//aqui a variável max recebe o valor do primeiro item do array
		String letra ="";
		for (int i = 0; i < array.length; i++) { 
			if (array[i] > max){   
				max = array[i];
				if(Leitor.tipo.equals("caracteres")) letra = caract[i];
				else letra = data[i];
	         }
	     }  
	   return letra;
	}
	
		
		
	public static void main(String[] args) throws IOException {
		RedeNeural redeNeural = new RedeNeural();//Cria nova rede neural
		
		redeNeural.inicializaVariaveis();//Inicializa taxa de Aprendizado, numero de epocas, inputs e entradas
		int qtdSaida;
		if (Leitor.tipo.equals("caracteres")) {
			qtdSaida = 7;//1 neuronio para cada caractere
		} 
		else if (Leitor.dtset) qtdSaida = 2;
		else qtdSaida = 1;//AND, OR e XOR
	

		//Inicializa os neuronios e cada camada da rede
		redeNeural.inicializaNeuronios(RedeNeural.entradas.get(0).length, RedeNeural.entradas.get(0).length, qtdSaida);
		List<double[]> fixinput = RedeNeural.entradas;
		
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		boolean flag = true;

		while(flag) {
			System.out.println("rodar, treinar ou sair?");
			String comando;
			
			try {
				comando = bf.readLine();
				switch(comando) {
				
				case "rodar":
					RedeNeural.entradas = fixinput;
					if(Leitor.dtset) RedeNeural.entradas = RedeNeural.entradas.subList(304, RedeNeural.entradas.size());//Recebe 30% da tabela
					System.out.println(RedeNeural.entradas.size());
					ArrayList<double[]> resultado = new ArrayList<>();
					//Para cada Array de input
					for(int i=0; i < RedeNeural.entradas.size(); i++) {
						//Aplica o fowardprop e captura a saida de cada input
						Neuronio[] neuronios = redeNeural.forwardprop(RedeNeural.entradas.get(i))
									   .getNeuronios();
						double[] result = new double[neuronios.length - (RedeNeural.inputNeuronio + RedeNeural.hiddenNeuronio)];
						//System.out.println("Result: "+result.length);
						
						for (int j = RedeNeural.inputNeuronio + RedeNeural.hiddenNeuronio; j < neuronios.length; j++) {
							result[j - (RedeNeural.inputNeuronio + RedeNeural.hiddenNeuronio)] = neuronios[j].getOutput();
							
						}

						resultado.add(result);
					};
					
					if(RedeNeural.entradas.size() > 4) printaCaractere(resultado);
					else printaResultado(resultado);

					break;
					
				case "treinar":
					RedeNeural.entradas = fixinput;
					if(Leitor.dtset) RedeNeural.entradas = RedeNeural.entradas.subList(0,304);//Recebe 70% da tabela
					
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
						System.out.println(RedeNeural.entradas.size());
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
