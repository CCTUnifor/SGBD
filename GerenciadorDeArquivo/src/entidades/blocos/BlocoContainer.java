package entidades.blocos;

import entidades.GerenciadorArquivo;
import entidades.GerenciadorDeIO;
import exceptions.ContainerNoExistentException;
import factories.ContainerId;
import interfaces.IBinary;
import interfaces.IPrint;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;
import utils.GlobalVariables;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class BlocoContainer implements IBinary, IPrint {
    private BlocoControle blocoControle;
    private ArrayList<BlocoDado> blocosDados;

    public BlocoContainer(int containerId) {
        this.blocoControle = new BlocoControle(containerId);
        this.blocosDados = new ArrayList<BlocoDado>();
    }

    public BlocoContainer(byte[] bytes) {
        this.blocoControle = new BlocoControle(bytes);
        this.blocosDados = new ArrayList<BlocoDado>();
        this.blocosDados.addAll(this.blocoDadosFromByteArray(bytes));
    }

    public int getContainerId () {
        return this.blocoControle.getHeader().getContainerId();
    }

    public BlocoControle getBlocoControle() {
        return blocoControle;
    }

    public ArrayList<BlocoDado> getBlocosDados() {
        return this.blocosDados;
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayConcater bc = new ByteArrayConcater();
        bc.concat(blocoControle.toByteArray())
                .concat(bytesBlocosDados());

        return bc.getFinalByteArray();
    }

    @Override
    public ArrayList<String> print() {
        ArrayList<String> parse = new ArrayList<String>();
        parse.addAll(this.blocoControle.print());
        parse.add("\n");

        for (BlocoDado bloco : blocosDados) {
            parse.addAll(bloco.print());
        }
        return parse;
    }

    @Override
    public  BlocoContainer fromByteArray(byte[] byteArray) {
        this.blocoControle.fromByteArray(byteArray);
        this.blocosDados.addAll(this.blocoDadosFromByteArray(byteArray));

        return this;
    }

    private ArrayList<BlocoDado> blocoDadosFromByteArray(byte[] byteArray) {
        ArrayList<BlocoDado> dados = new ArrayList<BlocoDado>();

        int indexOndeComecaOBlocoDeDados = blocoControle.getHeader().getTamanhoDescritor() + 11;
        for (int i = indexOndeComecaOBlocoDeDados; i < byteArray.length; i += this.blocoControle.getHeader().getTamanhoDosBlocos()) {
            BlocoDado bloco = new BlocoDado(ByteArrayUtils.subArray(byteArray, i, this.blocoControle.getHeader().getTamanhoDosBlocos()));
            dados.add(bloco);
        }

        return dados;
    }

    private byte[] bytesBlocosDados() {
        if (this.blocosDados.size() == 0)
            return null;

        ByteArrayConcater bc = new ByteArrayConcater();
        this.blocosDados.stream().forEach(bloco -> bc.concat(bloco.toByteArray()));
        return bc.getFinalByteArray();
    }

    public void adicionarBloco(BlocoDado bloco) {
        this.blocosDados.add(bloco);
    }

    public void atualizarProximoBlocoLivre() throws ContainerNoExistentException {
        String diretorio = GerenciadorArquivo.getDiretorio(this.getBlocoControle().getContainerId());
        try {
            GerenciadorDeIO.atualizarBytes(diretorio, 5, ByteArrayUtils.intToBytes(this.getBlocoControle().getHeader().getProximoBlocoLivre()));
        } catch (FileNotFoundException e) {
            throw new ContainerNoExistentException("");
        }
    }
}
