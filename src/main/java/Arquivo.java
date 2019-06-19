import org.apache.commons.lang3.SerializationUtils;

import java.io.*;

public class Arquivo implements Serializable{

    private File file;

    public Arquivo(File file) {
        this.file = file;
    }

    public void imprimirArquivo(){
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            while(br.ready()){
                String linha = br.readLine();
                System.out.println(linha);
            }
            br.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    public byte[] serializar() {
        return SerializationUtils.serialize(file);
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
