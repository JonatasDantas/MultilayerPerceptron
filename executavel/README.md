## Para executar

É necessário executar com o JRE na versão 11, para isso, basta apontar a variável de ambiente %JAVA_HOME% para a pasta de instalação do jdk 11 Ex: `C:\Program Files\Java\jdk-11\` e executar o comando a seguir:

`"%JAVA_HOME%\bin\java" -jar MultilayerPerceptron.jar -t caracteres-limpo.csv -r caracteres-ruido.csv`

### Argumentos:

* `-t <caminho do arquivo de treino>` - Especifica caminho do arquivo de treino ***(Obrigatório)***
* `-r <caminho do arquivo de execução>` - Especifica caminho do arquivo de execução ***(Obrigatório)***
* `-o <caminho do diretório de output>` - Especifica o diretório em que o programa fará output dos logs, ***padrão: diretório atual***
* `-A <taxa de aprendizado>` - Especifica a taxa de aprendizado do modelo, ***padrão: 0.8***
* `-M <momentum>` - Especifica o momentum a ser utilizado no modelo, ***padrão: 0.2***
* `-E <número de épocas>` - Especifica o número de épocas a serem utilizadas no treinamento, ***padrão: 100***
* `-C <camadas>` - Especifica o modelo das camadas escondidas do modelo, *(ex: "6,5,8", "8", "6,5")*, ***padrão: 6***