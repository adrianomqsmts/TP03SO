/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tpso3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import org.apache.commons.lang.SerializationUtils;



/**
 *
 * @author familia
 */
public class TPSO3 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        Object [] vec = new Object[10];
        vec[0] = "OI";
        vec[1] = 10;
        vec[2] = 2.5;

        SistemasDeArquivos arq = new SistemasDeArquivos(10, 4000);
        arq.teste();
        
    }
    
}
