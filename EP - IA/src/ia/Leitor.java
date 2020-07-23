package ia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

public class Leitor {
	
	private static List<String[]> inputs;//Lista de String[] que vai receber o conteudo do csv
	public static String tipo;
	public static boolean dtset = false;
	
	
	/* O construtor da classe Leitor pergunta qual o csv vai ser lido e abastece inputs com seu conteudo */
	public Leitor() throws IOException {

		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		Reader reader;
		System.out.println("AND, OR, XOR, caracteres ou dataset?");
		//System.out.println("treinar ou testar dataset?");
		tipo = bf.readLine();
		
		switch(tipo) {
			case "OR": 
				reader = Files.newBufferedReader(Paths.get("problemOR.csv").toAbsolutePath());//Le OR
				break;
			case "XOR": 
				reader = Files.newBufferedReader(Paths.get("problemXOR.csv").toAbsolutePath());//Le XOR
				break;
			case "AND": 
				reader = Files.newBufferedReader(Paths.get("problemAND.csv").toAbsolutePath());//Le AND
				break;
			case "caracteres":
				reader = Files.newBufferedReader(Paths.get("caracteres-limpo.csv").toAbsolutePath());//Le caracteres
				break;
			default:
				reader = Files.newBufferedReader(Paths.get("house-votes-84.csv").toAbsolutePath());//Dataset como default
				dtset = true;
				break;
		}
		
	    CSVReader csvReader = new CSVReaderBuilder(reader).build();
	    
	    try {
	    	inputs = csvReader.readAll();//Abastece a lista
	    }
	    catch (CsvException e) {		
	    	 e.printStackTrace();
	    }
	}
	
	/* M�todo que captura os dados de input e atribui a um ArrayList de double[] que ser�o usadas como dados de entrada */
	public ArrayList<double[]> leEntrada(){
		
		if(!dtset)inputs.get(0)[0] = inputs.get(0)[0].substring(1);//Ajusta BUG na leitura
		ArrayList<double[]> entradas = new ArrayList<double[]>();
		
		/* Para cada String[] em input, transforma em double[], remove o �ltimo elemento do array (saida) e adiciona
		   em entradas */
		for(int i=0; i<inputs.size(); i++) { 
			String[] adicionado = inputs.get(i);
			adicionado = ArrayUtils.remove(adicionado, adicionado.length-1);
			double[] temp = Arrays.stream(adicionado).mapToDouble(Double::parseDouble).toArray();//String[] to double[]
			entradas.add(temp);
		}
		
		return entradas;
	}
	
	/* M�todo captura o �ltimo elemento do String[] de cada elemento em input e atribui a 
	   saidasEspaeradas (double[]) */
	public ArrayList<double[]> leSaidaEsperada() {
		ArrayList<double[]> saidasEsperadas = new ArrayList<double[]>();
		//boolean caracteres = (!tipo.equals("AND") && !tipo.equals("OR") && !tipo.equals("XOR"));
		 
		if (tipo.equals("AND") || tipo.equals("OR") || tipo.equals("XOR")) {
            System.out.println("Entrou nao caracteres");
            for(int i=0; i<inputs.size(); i++) {
                    double[] saida = {Double.valueOf(inputs.get(i)[inputs.get(i).length-1])};
                    saidasEsperadas.add(saida);
            }
		}
		else if(tipo.equals("caracteres")) {
            System.out.println("Entrou caracteres");
            for(int i=0; i<inputs.size(); i++) {
                    String saidaLetra = inputs.get(i)[inputs.get(i).length-1];
                    double[] saida = {0, 0, 0, 0, 0, 0, 0};

                    switch (saidaLetra) {
                            case "A":
                                    saida[0] = 1;
                                    break;
                            case "B":
                                    saida[1] = 1;
                                    break;
                            case "C":
                                    saida[2] = 1;
                                    break;
                            case "D":
                                    saida[3] = 1;
                                    break;
                            case "E":
                                    saida[4] = 1;
                                    break;
                            case "J":
                                    saida[5] = 1;
                                    break;
                            case "K":
                                    saida[6] = 1;
                                    break;
                    }
                    //System.out.println(saidaLetra);
                    saidasEsperadas.add(saida);
            }
		} 
		else {
			System.out.println("Entrou dataset");
			for(int i=0; i<inputs.size(); i++) {
				String saidaLetra = inputs.get(i)[inputs.get(i).length-1];
				double[] saida = {0, 0};
				
				switch (saidaLetra) {
					case "republican":
						saida[0] = 1;
						break;
					case"democrat":
						saida[1] = 1;
						break;
				
				}
				//System.out.println(saidaLetra);
				saidasEsperadas.add(saida);
			}
		}
		
		return saidasEsperadas;
	}


	
}


