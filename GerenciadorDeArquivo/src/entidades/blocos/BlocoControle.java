package entidades.blocos;

import entidades.GerenciadorArquivo;
import entidades.GerenciadorDeIO;
import exceptions.ContainerNoExistentException;
import factories.ContainerId;
import interfaces.IBinary;
import interfaces.IPrint;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BlocoControle implements IBinary, IPrint {
    private BlocoContainerHeader blocoHeader;
    private ArrayList<Descritor> descritores;

    public BlocoControle(int containerId) {
        this.descritores = new ArrayList<Descritor>();
        this.blocoHeader = new BlocoContainerHeader(containerId);
    }

    public BlocoControle(byte[] bytes) {
        this.blocoHeader = new BlocoContainerHeader(bytes);
        this.descritores = new ArrayList<Descritor>();
        if (bytes.length > 11)
            this.descritores.addAll(this.descritoresFromByteArray(ByteArrayUtils.subArray(bytes, 11, this.blocoHeader.getTamanhoDescritor())));
    }

    public BlocoContainerHeader getHeader() {
        return blocoHeader;
    }
    public int getContainerId() { return this.blocoHeader.getContainerId(); }

    @Override
    public byte[] toByteArray() {
        ByteArrayConcater bc = new ByteArrayConcater();
        bc.concat(this.blocoHeader.toByteArray())
                .concat(this.bytesDescritores());
        return bc.getFinalByteArray();
    }

    @Override
    public ArrayList<String> print() {
        ArrayList<String> parse = new ArrayList<String>();

        for (Descritor descritor : descritores) {
            parse.addAll(descritor.print());
        }
        return parse;
    }

    @Override
    public BlocoControle fromByteArray(byte[] byteArray) {

        this.blocoHeader.fromByteArray(ByteArrayUtils.subArray(byteArray, 0, 11));
        if (byteArray.length > 11)
            this.descritores.addAll(this.descritoresFromByteArray(ByteArrayUtils.subArray(byteArray, 11, this.blocoHeader.getTamanhoDescritor())));

        return this;
    }

    public void adicionarDescritor(Descritor descritor) throws ContainerNoExistentException {
        try {
            GerenciadorDeIO.atualizarBytes(GerenciadorArquivo.getDiretorio(this.getContainerId()), 11 + this.getHeader().getTamanhoDescritor(), descritor.toByteArray());
        } catch (FileNotFoundException e) {
            throw new ContainerNoExistentException("");
        }
    }
    public void adicionarDescritores(ArrayList<Descritor> descritores) {
        this.descritores.addAll(descritores);
    }

    public List<String> getColumnsName() {
        return this.descritores.stream()
                .filter(x -> x.getTipoDado() != TipoDado.PATH)
                .map(Descritor::getNome).collect(Collectors.toList());
    }

    public List<String> getIndexName() {
        return this.descritores.stream()
                .filter(x -> x.getTipoDado() == TipoDado.PATH)
                .map(Descritor::getNome).collect(Collectors.toList());
    }

    private byte[] bytesDescritores() {
        ByteArrayConcater bc = new ByteArrayConcater();
        for (Descritor descritor : this.descritores) {
            bc.concat(descritor.toByteArray());
        }
        return bc.getFinalByteArray();
    }

    private ArrayList<Descritor> descritoresFromByteArray(byte[] descritoresByteArray) {
        ArrayList<Descritor> descritores = new ArrayList<Descritor>();
        boolean whileTrue = true;
        int proximoIndex = 0;

        while(whileTrue) {
            int tamanho = ByteArrayUtils.byteArrayToInt(ByteArrayUtils.subArray(descritoresByteArray, proximoIndex, 4));
            descritores.add(new Descritor(ByteArrayUtils.subArray(descritoresByteArray, proximoIndex,  tamanho)));

            proximoIndex += tamanho;
            whileTrue = whileTrue && proximoIndex < descritoresByteArray.length;
        }

        return descritores;
    }
}
