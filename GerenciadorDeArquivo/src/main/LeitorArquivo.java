package main;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Scanner;
/**
 * Created by José Victor on 05/08/2017.
 */
public class LeitorArquivo {

    public static void main(String[] args) throws IOException {
        gerarArquivoBinario("teste.txt");
        //getContainer("dadosBinarios.bin");

    }

    private static void gerarArquivoBinario(String diretorio) throws IOException {
        RandomAccessFile randomaccessfile = new RandomAccessFile(diretorio, "r");
        RandomAccessFile randomaccessfileBinario = new RandomAccessFile("dadosBinarios.bin", "rw");
        FileChannel fileChannel = randomaccessfile.getChannel();
        FileChannel fileChannelBinario = randomaccessfileBinario.getChannel();
        MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
        MappedByteBuffer bufferBinario = fileChannelBinario.map(FileChannel.MapMode.READ_WRITE, 0, fileChannelBinario.size());
        buffer.load();
        bufferBinario.load();
        for (int i = 0; i < buffer.limit(); i++) bufferBinario.put(buffer.get());
        //for (int i = 0; i < bufferBinario.limit(); i++) System.out.println((char)bufferBinario.get());;
        buffer.clear(); // do something with the data and clear/compact it.
        bufferBinario.clear();
        fileChannel.close();
        fileChannelBinario.close();
        randomaccessfile.close();
        randomaccessfileBinario.close();
    }

    private static void lerArquivoBinario(String diretorio)
    {
        File arquivo = new File(diretorio);
        try( InputStream in = new FileInputStream(arquivo) ){
            Scanner scan = new Scanner(in);
            while( scan.hasNext() ){
                System.out.println( scan.nextLine() );
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
}

