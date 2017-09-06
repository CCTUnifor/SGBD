package entidades;

import entidades.blocos.BlocoContainer;
import entidades.blocos.BlocoDado;
import exceptions.ContainerNoExistent;
import interfaces.IFileManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorArquivo implements IFileManager {

    private int containerIdCount = 0;
    private int blocoIdCount = 1;
    private List<BlocoContainer> containers;

    public GerenciadorArquivo() {
        this.containers = new ArrayList<BlocoContainer>();
    }

    @Override
    public BlocoDado criarBlocoDeDado(int containerId) throws ContainerNoExistent {
        if (this.containerIdCount == 0 || containerId == 0)
            throw new ContainerNoExistent();
        BlocoContainer container = this.containers.get(containerId - 1);
        BlocoDado bloco = new BlocoDado(containerId, this.blocoIdCount++);
        return bloco;
    }

    @Override
    public BlocoDado criarBlocoDeDado(int containerId, ArrayList<Object> dados) throws ContainerNoExistent {
        BlocoDado bloco = new BlocoDado(containerId, this.blocoIdCount++, dados);

        return bloco;
    }

    @Override
    public BlocoDado criarBlocoDeDado(byte[] bytes) throws ContainerNoExistent {
        BlocoDado bloco = new BlocoDado(bytes);
        if (bloco.getHeader().getContainerId() < this.containerIdCount)
            throw new ContainerNoExistent();

        return bloco;
    }

    @Override
    public BlocoContainer criarBlocoContainer() {
        BlocoContainer container = new BlocoContainer(++this.containerIdCount);
        return container;
    }

    @Override
    public void gravarArquivo(byte[] bytes, String diretorio) throws FileNotFoundException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(diretorio, "rw");
        try {
            randomAccessFile.write(bytes);
            randomAccessFile.close();
        }catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public byte[] lerArquivo(String diretorio) throws FileNotFoundException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(diretorio, "r");
        byte[] bytes = null;
        int tamanhoArquivo;

        try {
            tamanhoArquivo = (int) randomAccessFile.length();
            bytes = new byte[tamanhoArquivo];
            String x = randomAccessFile.readLine();
            randomAccessFile.read(bytes);
            randomAccessFile.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return bytes;
    }
}