package entidades;

import entidades.blocos.BlocoContainer;
import utils.ByteArrayUtils;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public class GerenciadorDeIO {
    public static void gravarBytes(String diretorio, byte[] bytes) throws FileNotFoundException {
        try {
            makeFiles(diretorio);
        } catch (IOException e) {
            throw new FileNotFoundException();
        }

        File file = new File(diretorio);
        if (file.exists())
            file.delete();

        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        try {
            randomAccessFile.write(bytes);
            randomAccessFile.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void gravarString(String diretorio, String conteudo) throws IOException {
        File file = new File(diretorio);
        if (!file.exists())
            file.createNewFile();

        try (PrintWriter out = new PrintWriter(file)) {
            out.print(Arrays.toString(ByteArrayUtils.stringToByteArray(conteudo)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void gravarString(String diretorio, ArrayList<String> conteudo) throws IOException {
        File file = new File(diretorio);
        if (file.exists())
            file.delete();
        file.createNewFile();

        try (PrintWriter out = new PrintWriter(file)) {
            conteudo.forEach(linha -> out.print(Arrays.toString(ByteArrayUtils.stringToByteArray(linha))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
            for (int i = 0; i < tamanho; i++) {
                bytes[i] = randomAccessFile.readByte();
            }
            randomAccessFile.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return bytes;
    }

    public static void atualizarBytes(String diretorio, int offset, byte[] bytes) throws FileNotFoundException {
        File file = new File(diretorio);
        if (!file.exists())
            throw new FileNotFoundException("Tabela não encontrada: " + diretorio);

        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        try {
            randomAccessFile.seek(offset);
            randomAccessFile.write(bytes);
            randomAccessFile.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static byte[] getBytes(String diretorio, int start, int length) throws FileNotFoundException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(diretorio, "r");
        byte[] bytes = null;
        try {
            int tamanho = length;
            if (tamanho == 0)
                return null;

            bytes = new byte[tamanho];
            randomAccessFile.seek(start);
            for (int i = 0; i < tamanho; i++) {
                bytes[i] = randomAccessFile.readByte();
            }
            randomAccessFile.close();

        } catch (IOException e) {
            return null;
        }
        return bytes;
    }

    public static void makeDirs(String path) throws IOException {
        try {
            Files.createDirectories(FileSystems.getDefault().getPath(path));
        } catch (FileAlreadyExistsException ignored) {
        }
    }

    public static void makeFiles(String path) throws IOException {
        try {
            Files.createFile(FileSystems.getDefault().getPath(path));
        } catch (FileAlreadyExistsException ignored) {
        }
    }
}
