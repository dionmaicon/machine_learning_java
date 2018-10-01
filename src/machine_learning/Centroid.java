
package machine_learning;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author dion
 */
public class Centroid {
    private int id;
    
    private double[] numeric_atributes;

    public Centroid(int id, double[] numeric_atributes) {
        this.id = id;
        this.numeric_atributes = numeric_atributes;
    }

    public double[] getNumeric_atributes() {
        return numeric_atributes;
    }

    public int getId() {
        return id;
    }

    public void setNumeric_atributes(double[] numeric_atributes) {
        this.numeric_atributes = numeric_atributes;
    }

    @Override
    public String toString() {
        String string = "";
        for (int i = 0; i < numeric_atributes.length; i++) {
            string = string + this.numeric_atributes[i] + ", ";
        }
        string = string + "\n";
        return string;
    }
    
   
}

