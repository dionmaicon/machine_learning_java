/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package machine_learning;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
/**
 *
 * @author dion
 */
public abstract class Neuronio {
    private double[] pesos;
    private ArrayList<String> classes;

    public void setClasses(ArrayList<String> classes) {
        this.classes = classes;
    }
    
    public Neuronio(int qtdEntradas) {
        pesos = new double[qtdEntradas + 1];

        Random r = new Random();
        for (int i = 0; i < pesos.length; i++) {
                pesos[i] = r.nextDouble();
        }
    }

    protected abstract double ativacao(double sum);

    public double ativar(double[] entradas) {
        double sum = pesos[0];

        for (int i = 1; i < pesos.length; i++) {
                sum += pesos[i] * entradas[i - 1];
        }

        return ativacao(sum);
    }

    public double ajustar(double[] entradas, double erro, double taxa) {
            pesos[0] += taxa * erro;

            for (int i = 1; i < pesos.length; i++) {
                    pesos[i] += taxa * erro * entradas[i - 1];
            }

            return Math.abs(erro);
    }

    public List<Double> treinar(List<Instance> instances, double taxa,
                    double maxError, int maxLoop) {
        List<Double> ret = new ArrayList<>();
 
        double erroMedio, sObtida;
        int count = 0;
        do {
            erroMedio = 0;

            for (Instance instance : instances) {
                    sObtida = ativar(instance.getNumeric_atributes());
                    erroMedio += ajustar(instance.getNumeric_atributes(), getSaida(instance) - sObtida, taxa);
            }

            erroMedio /= instances.size();
            ret.add(erroMedio);
            count ++;
        } while (erroMedio > maxError && count < maxLoop);

        return ret;
    }

    public double[] getPesos() {
        return pesos;
    }
    
    public double getSaida(Instance instance){
        for (int i = 0; i < classes.size(); i++) {
            if(classes.get(i).equals(instance.getClasse())){
                return (double) i;
            }
        }
        return 0;
    }
    
}
