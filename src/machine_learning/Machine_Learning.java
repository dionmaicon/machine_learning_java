/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package machine_learning;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.JOptionPane;

/**
 *
 * @author dion
 */
public class Machine_Learning {
    
    private Map<Integer, ArrayList<String>> atributos_convertidos;
    private Map<Integer, ArrayList<Instance>> clusters;
    private Map<Integer, ArrayList<Instance>> ultimos_clusters;
    private ArrayList<Instance> instances;
    private ArrayList<Centroid> centroids;
    private ArrayList<Instance> teste;
    private double[] min, max, average;
    private static double _numeric_strict;
    private double _numeric_strict_max = 1000;
    private double _numeric_strict_min = -1000;
    private ArrayList<String> classes;
    private int[][] matriz_confusao;
    
    public static void setNumeric_strict(double _numeric_strict) {
        Machine_Learning._numeric_strict = _numeric_strict;
    }
    
    
    public void kMeans(int k, int numero_passos, String caminho_arquivo, boolean tem_classe){
        this.atributos_convertidos = new HashMap<>();
        this.centroids = new ArrayList<>();
        this.instances = new ArrayList<>();
        this.clusters = new HashMap<>();
        this.ultimos_clusters = new HashMap<>();
        
        boolean moveu_centroid = true;
        int iteracao = 0;
        
        if(lerArquivo(caminho_arquivo, tem_classe)){
           // showInstances();
            
            
            
           // showMinMaxAverage();
            minMaxAverage();
            normalizar();
            
            initCentroids(k);
            
            
            showCentroids();
            
            double[] values = new double[centroids.size()];
            while(iteracao < numero_passos){
                for (int i = 0; i < instances.size(); i++) {
                    double menor = 0;
                    int indice = 0;
                    for (int j = 0; j < centroids.size(); j++) {
                        double distance = distance(instances.get(i).getNumeric_atributes(), centroids.get(j).getNumeric_atributes());
                        values[j] = distance;
                        menor = distance;
                        indice = j;
                    }
                    
                    for (int j = 0; j < centroids.size(); j++) {
                        if(values[j] < menor){
                            menor = values[j];
                            indice = j;
                        }
                    } 
                    
                    if(clusters.isEmpty() ){
                        ArrayList<Instance> list = new ArrayList<>();
                        list.add(instances.get(i));
                        clusters.put(indice, list);

                    }else if( clusters.get(indice) == null){
                        ArrayList<Instance> list = new ArrayList<>();
                        list.add(instances.get(i));
                        clusters.put(indice, list);
                        
                    }else{
                        ArrayList<Instance> list = clusters.get(indice);
                        list.add(instances.get(i));
                        clusters.put(indice, list);
                    }
                }
                moveu_centroid = calcularCentroids();
                iteracao++;
                
                showCentroids();
                System.out.println("----------------------------------------------------------------------");
                if(!moveu_centroid){
                    System.out.println("Centroid movimentou: " + moveu_centroid +", Numero de passos: " + iteracao);
                    System.out.println("Algoritmo Executou com Sucesso!!");
                    prepareClustersImpressao();
                    return;
                }
            }
        }
    }
    
    public void kNN(int k, String caminho_arquivo,String caminho_arquivo2, boolean tem_classe){
        this.atributos_convertidos = new HashMap<>();
        this.centroids = new ArrayList<>();
        this.instances = new ArrayList<>();
        this.teste = new ArrayList<>();
        this.clusters = new HashMap<>();
        double tp = 0;
        double total = 0;
        int t = 0;
        
        if(lerArquivo(caminho_arquivo, tem_classe == true && lerArquivo2(caminho_arquivo2, tem_classe) == true)){
            minMaxAverage();
            normalizar();
                 
            iniciarClasses();
            t = classes.size();
            
            this.matriz_confusao = new int[t][t] ;
            
            showClasses();
            
            for (Instance instanceTeste : teste) {
                ArrayList<Classificacao> classificados = new ArrayList<>();
                for (Instance instanceTreinamento : instances) {
                    double distance = distance(instanceTeste.getNumeric_atributes(), instanceTreinamento.getNumeric_atributes());
                    classificados.add(new Classificacao(instanceTreinamento.getClasse(), distance));
                }
                
            Collections.sort(classificados, new Comparator<Classificacao> (){
                @Override
                public int compare(Classificacao cl1, Classificacao cl2) {
                    if (cl1.getDistancia()< cl2.getDistancia()) return -1;
                    if (cl1.getDistancia()> cl2.getDistancia()) return 1;
                    return 0;
                }
            });
            
            List<Classificacao> list = classificados.subList(0, k);
            
            //geClasse é utilizado para pegar a classe que mais aparece na list, fornecida como parametro
           
            String classe = getClasse(list);
            total++;
            System.out.println("Classe no conjunto de teste: "+ instanceTeste.getClasse() +", classificada como " + classe); //Classificação final da instancia teste
            
            addMatrizConfusao(instanceTeste.getClasse(), classe);
            
            if(instanceTeste.getClasse().equals(classe)){
                tp++;
            }
            
            System.out.println("---------------------------------------------------------------------------");    
            
            }
            
            
        }else{
            System.out.println("Não foi possível iniciar os 2 conjuntos");
        }
        double taxa = tp/total;
        System.out.println("Verdadeiros positivos: "+ tp +", total de instancias de teste: "+  total + ", taxa de acerto: " + taxa * 100 + "%");
        showMatrizConfusao();
    }
    
    private String getClasse(List<Classificacao> list) {
        ArrayList<Contador> contadores = new ArrayList<>();
        for (String classe : classes) {
            contadores.add(new Contador(classe));
        }
        
        for (Contador contador : contadores) {
            for (Classificacao classificacao : list) {
                if(classificacao.getClasse().equals(contador.getClasse())){
                    contador.setCont(contador.getCont()+1);
                }
            }
        }
        
        Collections.sort(contadores, new Comparator<Contador> (){
                @Override
                public int compare(Contador cl1, Contador cl2) {
                    if (cl1.getCont() < cl2.getCont()) return -1;
                    if (cl1.getCont()> cl2.getCont()) return 1;
                    return 0;
                }
        });
                
        return contadores.get(contadores.size()-1).getClasse();
    }
    
    private boolean calcularCentroids(){
        int length = instances.get(0).getNumeric_atributes().length;
        double[] sum = new double[length];
        double[] aux_average;
        boolean trocou_centroid = false;
        
        System.out.println("Clusters SIZE: "+ clusters.size());
        for (Map.Entry<Integer, ArrayList<Instance>> entry : clusters.entrySet()) {
            trocou_centroid = false;
            aux_average = new double[length];
            Integer key = entry.getKey();
            ArrayList<Instance> values = entry.getValue();
            
            System.out.println("Numbers of Instances cluster "+ key +": "+ values.size());
            
            for (int i = 0; i < length; i++) {
                sum[i] = 0;
            }
            
            for (Instance instance : values) {
                for (int i = 0; i < length; i++) {
                    sum[i] = sum[i] + instance.getNumeric_atributes()[i];
                }
            }
            
            for (int i = 0; i < length; i++) {
                aux_average[i] = sum[i] / values.size();
            }
            
            Centroid centroid = centroids.get(key);
            System.out.println("Calc Centroid: "+ key + ", " + centroid.toString());
            
            if(changeCentroid(centroid.getNumeric_atributes(), aux_average)){
                trocou_centroid = true;
                centroid.setNumeric_atributes(aux_average);
                centroids.set(centroid.getId(), centroid);
            }
        }
        
        clusters.clear();
        return trocou_centroid;
    }
    //Retorna false se os valores para o centroids mudaram
    private boolean changeCentroid(double[] numeric_atributes, double[] aux_average) {
        for (int i = 0; i < aux_average.length; i++) {    
            if (aux_average[i] != numeric_atributes[i]) {
                return true;
            }
        }
        return false;
    }

    private boolean lerArquivo(String caminho, boolean tem_classe) {
        String linha;
        BufferedReader	inReader = null;
        String classe= "";
        
        try {
            inReader = new BufferedReader(new FileReader(caminho));
             
        } catch( FileNotFoundException e ) {
            JOptionPane.showMessageDialog(null,"Não pode abrir em " + caminho
                    ,"Arquivo Não Encontrado",JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            boolean  tem_data = false;        
            while((linha = inReader.readLine())!= null) {
                if(linha.contains("@data")) tem_data = true;
                
                if(tem_data && tem_classe){
                    String[] st = linha.split(",");
                    double[] atributos_numericos = new double[st.length - 1];
                    String[] atributos_texto = new String[st.length - 1];
                    
                    if(st.length > 5){
                    
                        for (int i =0; i < st.length; i++){
                            if (i < (st.length - 1)){
                                if(isDouble(st[i])){
                                    atributos_numericos[i] = Double.parseDouble(st[i]);
                                }else{
                                    atributos_numericos[i] = nominalToDouble(i, st[i].replaceAll("'", ""));
                                }
                                atributos_texto[i] = st[i].replaceAll("'", "");
                            }else{
                                classe = st[i];
                            }
                        }
                        
                        this.instances.add(new Instance(instances.size(), atributos_numericos, atributos_texto, classe));
                    }
                }
            }
            if(!tem_data){
            
               System.err.println("Arquivo Incompatível");
            }
            try {
                inReader.close();
            } catch (IOException ex) {
            
                System.err.println("Erro na leitura"+ ex);
                return false;
            }
        } catch (IOException ex) {
            
            return false;
        }
       
     return true;
    }
    private boolean lerArquivo2(String caminho, boolean tem_classe) {
        String linha;
        BufferedReader	inReader = null;
        String classe= "";
        
        try {
            inReader = new BufferedReader(new FileReader(caminho));
             
        } catch( FileNotFoundException e ) {
            JOptionPane.showMessageDialog(null,"Não pode abrir em " + caminho
                    ,"Arquivo Não Encontrado",JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            boolean  tem_data = false;        
            while((linha = inReader.readLine())!= null) {
                if(linha.contains("@data")) tem_data = true;
                
                if(tem_data && tem_classe){
                    String[] st = linha.split(",");
                    double[] atributos_numericos = new double[st.length - 1];
                    String[] atributos_texto = new String[st.length - 1];
                    
                    if(st.length > 5){
                    
                        for (int i =0; i < st.length; i++){
                            if (i < (st.length - 1)){
                                if(isDouble(st[i])){
                                    atributos_numericos[i] = Double.parseDouble(st[i]);
                                }else{
                                    atributos_numericos[i] = nominalToDouble(i, st[i].replaceAll("'", ""));
                                }
                                atributos_texto[i] = st[i].replaceAll("'", "");
                            }else{
                                classe = st[i];
                            }
                        }
                        
                        this.teste.add(new Instance(instances.size(), atributos_numericos, atributos_texto, classe));
                    }
                }
            }
            if(!tem_data){
            
               System.err.println("Arquivo Incompatível");
            }
            try {
                inReader.close();
            } catch (IOException ex) {
            
                System.err.println("Erro na leitura"+ ex);
                return false;
            }
        } catch (IOException ex) {
            
            return false;
        }
       
     return true;
    }
    
  public  boolean isDouble(String s) {
    boolean amIValid = false;
    try {
        Double.parseDouble(s);
        amIValid = true;
    } catch (NumberFormatException e) {
       
    }
        return amIValid;
    }

    private double nominalToDouble(int i, String nominal) {
        if(nominal.equals("?")){
            return _numeric_strict;
        }
        if(atributos_convertidos.isEmpty() ){
            ArrayList<String> list = new ArrayList<>();
            list.add(nominal);
            atributos_convertidos.put(i, list);
            return 1.0;
        }else if( atributos_convertidos.get(i) == null){
            ArrayList<String> list = new ArrayList<>();
            list.add(nominal);
            atributos_convertidos.put(i, list);
            return  1.0;
        }else{
            ArrayList<String> list = atributos_convertidos.get(i);
            boolean achou = false;
            int count = 1;
            for (String string : list) {
                if(string.equals(nominal)){
                    achou = true;
                    break;
                }
                count++;    
            }
            if(!achou){
                list.add(nominal);
                atributos_convertidos.replace(i, list);
                return (double) list.size();
            }
            return count;
        }
     
    }

    public void showInstances(){
        System.out.println("------------ Show Instances --------");
        for (Instance instance : instances) {
            //System.out.println("ID: " + instance.getId());
            System.out.println(instance.toString());
        }
    }
    
    public void showCentroids(){
        System.out.println("------------ Show Centroids --------");
        for(Centroid centroid : centroids) {
            System.out.println("Centroid: " + centroid.getId() + ",  "+ centroid.toString());
        }
    }

    private void initCentroids(int k) {
        for(int i = 0; i < k; i++){
            int randomNum = ThreadLocalRandom.current().nextInt(0, instances.size());
            centroids.add(new Centroid(i, instances.get(randomNum).getNumeric_atributes()));
        }
    }
    
    private void minMaxAverage(){
        Instance temporary_instance = instances.get(0);
        int length = temporary_instance.getNumeric_atributes().length;
        min = new double[length];
        max = new double[length];
        average = new double[length];
        double[] sum = new double[length];
        int[] count = new int[length];
                
        for (int i = 0; i < length; i++) {
            if(temporary_instance.getNumeric_atributes()[i] != _numeric_strict){
                min[i] = temporary_instance.getNumeric_atributes()[i];
                max[i] =  temporary_instance.getNumeric_atributes()[i];
            }else{
                min[i] = _numeric_strict_max;
                max[i] =  _numeric_strict_min;
            }
        }
        
        
        for (int i = 0; i < length; i++) {
            sum[i] = 0;
            count[i] = 0;
        }
        
        for (Instance instance : instances) {
            for (int i = 0; i < instance.getNumeric_atributes().length; i++) {
                if(instance.getNumeric_atributes()[i] != _numeric_strict){
                    sum[i] = sum[i] + instance.getNumeric_atributes()[i];
                    count[i] = count[i] + 1;
                }
                
                if(instance.getNumeric_atributes()[i] > max[i] && instance.getNumeric_atributes()[i] != _numeric_strict){
                    max[i] = instance.getNumeric_atributes()[i];
                }
                
                if(instance.getNumeric_atributes()[i] < min[i] && instance.getNumeric_atributes()[i] != _numeric_strict){
                    min[i] = instance.getNumeric_atributes()[i];
                }
                
            }
        }
        for (int i = 0; i < length; i++) {
            average[i] = sum[i] / count[i];
        }
        
        for (Instance instance : instances) {
            for (int i = 0; i < instance.getNumeric_atributes().length; i++) {
                if(instance.getNumeric_atributes()[i] == _numeric_strict){
                    instance.getNumeric_atributes()[i] = average[i];
                }
            }
        }
    }
    
    private void showMinMaxAverage(){
         System.out.println("------------ Show Min, Max and Average for each atribute --------");
        int lenght = instances.get(0).getNumeric_atributes().length;
        for (int i = 0; i < lenght; i++) {
            System.out.println("Atributo: "+ (i + 1 )+", Min: " + min[i] + ", Max: " +max[i] + ", Average: "+ average[i] );
        }
    
    }

    private double distance(double[] vec1, double[] vec2) {
        double distance = 0;
	for (int i = 0; i < vec1.length; i++)
		distance += Math.abs(vec1[i] - vec2[i]);
	return distance;
    }
    
    private void showClusters(){
        for (Map.Entry<Integer, ArrayList<Instance>> entry : clusters.entrySet()) {
            Integer key = entry.getKey();
            ArrayList<Instance> value = entry.getValue();
            System.out.println("Show cluster k: " + key);
            for (Instance instance : value) {
                   System.out.print("Instance: " + instance.toString());
            }
        }
    }

    private void prepareClustersImpressao(){
        for (Map.Entry<Integer, ArrayList<Instance>> entry : ultimos_clusters.entrySet()) {
            Integer key = entry.getKey();
            System.out.println("------------------Imprimindo Cluster:" + key + "--------------------");
            ArrayList<Instance> list = entry.getValue();
            for (Instance instance : list) {
                instance.toString();
            }
            System.out.println("--------------------------------------------------------------------");
        }
    }

    private void showTeste() {
        System.out.println("------------ Show Instances of Test --------");
        for (Instance instance : teste) {
            System.out.println(instance.toString());
        }
    }

    private void normalizar() {
        for (Instance instance : instances) {
            double[] atributos = instance.getNumeric_atributes();
            for (int i = 0; i < atributos.length; i++) {
                double valor = (double) (atributos[i] - min[i]) / (max[i] - min[i]);
                atributos[i] = valor;

            }
            instance.setNumeric_atributes(atributos);
        }
        if(teste != null){
            for (Instance instance : teste) {
            double[] atributos = instance.getNumeric_atributes();
            for (int i = 0; i < atributos.length; i++) {
                 atributos[i] = (double) (atributos[i] - min[i]) / (max[i] - min[i]);
            }
            instance.setNumeric_atributes(atributos);
        }
    }    
        
    }
    private void iniciarClasses(){
        this.classes = new ArrayList<>();
        classes.add(instances.get(0).getClasse());
        for (int i = 1; i < instances.size(); i++) {
            if(!classes.contains(instances.get(i).getClasse())){
                classes.add(instances.get(i).getClasse());
            }
        }
    }
    
    private void showClasses() {
        for (String string : classes) {
            System.out.println(string +"\n");
        }
    }

    public void rnPerceptron(String caminho_arquivo, String caminho_arquivo2, boolean tem_classe) {
        this.atributos_convertidos = new HashMap<>();
        this.instances = new ArrayList<>();
        this.teste = new ArrayList<>();
        int print = 1;
        int t = 0;
        
        if(lerArquivo(caminho_arquivo, tem_classe == true && lerArquivo2(caminho_arquivo2, tem_classe) == true)){
            minMaxAverage();
            normalizar();
            
            Neuronio n = new NeuronioDegrau(this.instances.get(0).getNumeric_atributes().length);
            iniciarClasses();
            n.setClasses(classes);
            iniciarClasses();
            t = classes.size();
            
            this.matriz_confusao = new int[t][t] ;
            
            
            for (Instance e : instances) {
                    n.ativar(e.getNumeric_atributes());
            }

            List<Double> erro = n.treinar(instances, 0.1, 0, 1000);
            System.out.println("Instancias Treinadas: -------------------------\n");
            for (Instance e : instances) {
                    System.out.print(n.ativar(e.getNumeric_atributes())+ "\t");
                    if(print++ % 10 == 0) System.out.println();
            }
            System.out.println();

            System.out.println("Printar a curva de erro para as iterações:");
            print = 1;
            for (Double d : erro) {
                    System.out.println("Iteração: "+ print +", erro: " + d + " ");
                    print++;
            }
            System.out.println("");
            
            System.out.print("Pesos gerados: [");
            for (int i = 0; i < n.getPesos().length; i++) {
                System.out.print(n.getPesos()[i] + "\t");
            }
            System.out.print("]");
            System.out.println("");
            
            System.out.println("Instancias do Teste: ----------------------------\n");
            double tp = 0;
            double total = 0;
            for (Instance e : teste) {
                    //System.out.print(n.ativar(e.getNumeric_atributes())+ "\t");
                    System.out.println("Classificado como: " + n.ativar(e.getNumeric_atributes()) + ", esperado: " + n.getSaida(e));       
                    addMatrizConfusao(classes.get((int) n.ativar(e.getNumeric_atributes())), classes.get((int) n.getSaida(e) ));
                    if(n.ativar(e.getNumeric_atributes()) == n.getSaida(e)){
                        tp++;
                    }
                    total++;
            }         
            double taxa = tp/total;
            System.out.println("Verdadeiros positivos: "+ tp +", total de instancias de teste: "+  total + ", taxa de acerto: " + taxa * 100 + "%");
        }   showMatrizConfusao();
    }
    
    public int getSaida(String classe){
        for (int i = 0; i < classes.size(); i++) {
            if(classes.get(i).equals(classe)){
                return i;
            }
        }
        return 0;
    }

    private void addMatrizConfusao(String classe_1, String classe_2) {
       int i = getSaida(classe_1);
       int j = getSaida(classe_2);
        matriz_confusao[i][j] = matriz_confusao[i][j] + 1;
    }
    private void showMatrizConfusao(){
        System.out.println("-------------------------------");
        System.out.print("Real/Predito | ");
        
        for (int i = 0; i < classes.size(); i++) {
            System.out.print(classes.get(i) + " | ");
        }
        System.out.println("");
        for (int i = 0; i < classes.size(); i++) {
            System.out.print( classes.get(i)+ "|\t");
            for (int j = 0; j < classes.size(); j++) {
                System.out.print( matriz_confusao[i][j] + "|");
            }
            System.out.println("");
        }
        System.out.println("-------------------------------");
    }

    public void rnPerceptron(String caminho, String caminho2, double taxa, int iteracoes, boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public class Classificacao implements Comparator<Classificacao>{
    private String classe;
    private double distancia;

    private Classificacao(String classe, double distancia) {
        this.classe = classe;
        this.distancia = distancia;
        }

        public String getClasse() {
            return classe;
        }

        public void setClasse(String classe) {
            this.classe = classe;
        }

        public double getDistancia() {
            return distancia;
        }

        public void setDistancia(double distancia) {
            this.distancia = distancia;
        }

        @Override
        public int compare(Classificacao cl1, Classificacao cl2) {
            if (cl1.getDistancia()< cl2.getDistancia()) return -1;
            if (cl1.getDistancia()> cl2.getDistancia()) return 1;
            return 0;
        }

        @Override
        public String toString() {
         return "Classificação de distancia para instancia "+ classe + ", distancia de " + distancia;
        };

    }
    
    private class Contador{
        private String classe;
        private int cont;

        public Contador(String classe) {
            this.classe = classe;
            this.cont = 0;
        }

        public String getClasse() {
            return classe;
        }

        public void setClasse(String classe) {
            this.classe = classe;
        }

        public int getCont() {
            return cont;
        }

        public void setCont(int cont) {
            this.cont = cont;
        }
    }
    
}
