/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package machine_learning;

import gui.Main_Screen;

/**
 *
 * @author dion
 */
public class Main {
    public static void main(String args[]) {
        Main_Screen ms = new Main_Screen();
        Machine_Learning m = new Machine_Learning();
        
        ms.setMachine(m);
        ms.setVisible(true);
        
    }
}
