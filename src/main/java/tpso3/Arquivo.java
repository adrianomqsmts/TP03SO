package tpso3;

import java.io.*;

public class Arquivo implements Serializable{

    private File file;

    public Arquivo(File file) {
        this.file = file;
    }

    public void imprimirArquivo(File file){
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

    public byte[] getBytes() {
        int             len     = (int)file.length();
        byte[]          sendBuf = new byte[len];
        FileInputStream inFile;
        try {
            inFile = new FileInputStream(file);
            inFile.read(sendBuf, 0, len);
        } catch (FileNotFoundException fnfex) {

        } catch (IOException ioex) {

        }
        return sendBuf;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
