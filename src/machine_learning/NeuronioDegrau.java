/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package machine_learning;

/**
 *
 * @author dion
 */

public class NeuronioDegrau extends Neuronio {

   public NeuronioDegrau(int qtdEntradas) {
		super(qtdEntradas);
	}

	protected double ativacao(double sum) {
            if (sum <= 0) {
                    return 0;
            } else {
                    return 1;
            }
	}
        
        protected double ativacao(double sum, int numero_classes) {
            
            if (sum <= 0.25) {
                    return 0;
            } else if(sum > 0.25 && sum <= 0.50) {
                    return 1;
            }else if(sum > 0.50 && sum <= 0.75) {
                    return 2;
            }else  {
                    return 3;
            }
            
	}
}
