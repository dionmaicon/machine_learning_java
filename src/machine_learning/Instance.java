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
public class Instance {
    private int id;
    private double[] numeric_atributes;
    private String[] text_atributes;
    private String classe;
    
    public Instance(int id, double[] numeric_atributes, String[] text_atributes, String classe) {
        this.id = id;
        this.numeric_atributes = numeric_atributes;
        this.text_atributes = text_atributes;
        this.classe = classe;
    }
    
    public String getClasse(){
        return classe;
    }
    public void setClasse(String classe){
        this.classe = classe;
    }
    public int getId() {
        return id;
    }

    
    public String[] getText_atributes() {
        return text_atributes;
    }

    public double[] getNumeric_atributes() {
        return numeric_atributes;
    }

    public void setNumeric_atributes(double[] numeric_atributes) {
        this.numeric_atributes = numeric_atributes;
    }

    public void setText_atributes(String[] text_atributes) {
        this.text_atributes = text_atributes;
    }

    @Override
    public String toString() {
        String string = "";
        for (int i = 0; i < numeric_atributes.length; i++) {
            
            string = string + this.numeric_atributes[i] + ", ";
        }
        string = string + " "+ this.classe +"\n";
        return string;
    }
    
    
    
}
