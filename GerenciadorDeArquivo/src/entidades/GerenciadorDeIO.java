package entidades;

import entidades.blocos.BlocoContainer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class GerenciadorDeIO {
    public static void gravarBytes(String diretorio, byte[] bytes) throws IOException {
        File file = new File(diretorio);
        if (file.exists())
            file.delete();
        file.createNewFile();

        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        try {
            randomAccessFile.write(bytes);
            randomAccessFile.close();
        }catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }

    public static void gravarString(String diretorio, String conteudo) throws IOException {
        File file = new File(diretorio);
        if (file.exists())
            file.delete();
        file.createNewFile();

        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        try {

            randomAccessFile.writeUTF(conteudo);
            randomAccessFile.close();
        }catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }

    public static ArrayList<String> getStrings(String diretorio) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(diretorio, "r");

        ArrayList<String> linhas = new ArrayList<String>();

        String linhaAux = null;
        do {
            linhaAux = randomAccessFile.readLine();
            if (linhaAux != null) {
                linhas.add(linhaAux);
            }
        } while (linhaAux != null);

        randomAccessFile.close();

        return linhas;
    }

    public static byte[] getBytes(String diretorio) throws FileNotFoundException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(diretorio, "r");

        byte[] bytes = null;

        try {
            int tamanho = (int) randomAccessFile.length();
            if (tamanho == 0)
                return null;

            bytes = new byte[tamanho];
            for (int i = 0; i < tamanho; i++){
                bytes[i] = randomAccessFile.readByte();
            }
            randomAccessFile.close();

        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        return bytes;
    }
}
