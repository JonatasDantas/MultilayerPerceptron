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
	
	/* O construtor da classe Leitor pergunta qual o csv vai ser lido e abastece inputs com seu conteudo */
	public Leitor() throws IOException {
		
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		Reader reader;
		System.out.println("AND, OR, XOR ou caracteres?");
		String tipo = bf.readLine();
		
		switch(tipo) {
			case "OR": 
				reader = Files.newBufferedReader(Paths.get("problemOR.csv").toAbsolutePath());//Le OR
				break;
			case "XOR": 
				reader = Files.newBufferedReader(Paths.get("problemXOR.csv").toAbsolutePath());//Le XOR
				break;
			case "AND": 
				reader = Files.newBufferedReader(Paths.get("problemAND.csv").toAbsolutePath());
				break;
			default:
				reader = Files.newBufferedReader(Paths.get("caracteres-limpo.csv").toAbsolutePath());//caracteres como default
		}
		
	    CSVReader csvReader = new CSVReaderBuilder(reader).build();
	    
	    try {
	    	inputs = csvReader.readAll();//Abastece a lista
	    }
	    catch (CsvException e) {		
	    	 e.printStackTrace();
	    }
	}
	
	/* Método que captura os dados de input e atribui a um ArrayList de double[] que serão usadas como dados de entrada */
	public ArrayList<double[]> leEntrada(){
		
		inputs.get(0)[0] = inputs.get(0)[0].substring(1);//Ajusta BUG na leitura
		ArrayList<double[]> entradas = new ArrayList<double[]>();
		
		/* Para cada String[] em input, transforma em double[], remove o último elemento do array (saida) e adiciona
		   em entradas */
		for(int i=0; i<inputs.size(); i++) { 
			String[] adicionado = inputs.get(i);
			double[] temp = Arrays.stream(adicionado).mapToDouble(Double::parseDouble).toArray();//String[] to double[]
			temp = ArrayUtils.remove(temp, temp.length-1);//Remove o último elemento do array (seria a saida esperada)
			entradas.add(temp);
		}
		
		return entradas;
	}
	
	/* Método captura o último elemento do String[] de cada elemento em input e atribui a 
	   saidasEspaeradas (double[]) */
	public double[] leSaidaEsperada() {
		
		double[] saidasEsperadas = new double[inputs.size()];//Inicializa o array com o tamanho da lista inputs

		/* Para cada elemento em inputs, captura o ultimo elemento do String array em questão e atribui 
		   a saidasEsperadas */
		for(int i=0; i<inputs.size(); i++) {
			saidasEsperadas[i] = Double.valueOf(inputs.get(i)[inputs.get(i).length-1]);
		}
		
		return saidasEsperadas;
	}
	
}


