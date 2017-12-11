package entidades.blocos;

import interfaces.IBinary;
import interfaces.IPrint;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Descritor implements IBinary, IPrint {
    private TipoDado tipoDado;
    private int tamanho;
    private String nome;

    public Descritor(String coluna) {
        String[] x = coluna.split("\\[");
        this.nome = x[0];
        if (x[1].contains("I"))
            this.tipoDado = TipoDado.INTEIRO;
        else if (x[1].contains("A"))
            this.tipoDado = TipoDado.STRING;
        else if (x[1].contains("P"))
            this.tipoDado = TipoDado.PATH;
        else if (x[1].contains("R"))
            this.tipoDado = TipoDado.ROOT;
        else if (x[1].contains("C"))
            this.tipoDado = TipoDado.COLLUMN;

        Pattern pat = Pattern.compile("\\(([0-9]+)\\)");
        Matcher mat = pat.matcher(coluna);
        if (mat.find()) {
            switch (tipoDado) {
                case INTEIRO:
                case ROOT:
                case COLLUMN:
                    this.tamanho = 4;
                case STRING:
                case PATH:
                    this.tamanho = Integer.parseInt(mat.group(1));
            }
        }
    }

    public Descritor(byte[] byteArray) {
        this.fromByteArray(byteArray);
    }

    public int getTamanhoTotalDescritor() {
        int tamanhoDescritorHeader = 4;
        int tamanhoTipoDados = 1;
        int tamanhoDoTamanho = 1;
        int tamanhoDoNome = ByteArrayUtils.stringToByteArray(this.nome).length;

        return tamanhoDescritorHeader + tamanhoTipoDados + tamanhoDoTamanho + tamanhoDoNome;
    }

    public String getNome() {
        return this.nome;
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayConcater bc = new ByteArrayConcater();
        bc.concat(ByteArrayUtils.intToBytes(this.getTamanhoTotalDescritor()))
                .concat(ByteArrayUtils.intTo1Bytes(tipoDado.ordinal()))
                .concat(ByteArrayUtils.intTo1Bytes(this.tamanho))
                .concat(ByteArrayUtils.stringToByteArray(this.nome));

        return bc.getFinalByteArray();
    }

    @Override
    public ArrayList<String> print() {
        ArrayList<String> finalParse = new ArrayList<String>();
        String parse = "";
        parse += nome;
        parse += "[";

        switch (this.tipoDado ){
            case INTEIRO:
                parse += "I";
            case STRING:
                parse += "A";
            case PATH:
                parse += "P";
            case ROOT:
                parse += "R";
        }

        parse += "]";
        parse += "(";
        parse += this.tamanho;
        parse += ")|";
        finalParse.add(parse);

        return finalParse;
    }

    @Override
    public Descritor fromByteArray(byte[] byteArray) {
        int tamanhoDescritorHeader = 4;
        this.tipoDado = ByteArrayUtils.byteArrayToEnum(ByteArrayUtils.subArray(byteArray, tamanhoDescritorHeader, 1), TipoDado.values());
        this.tamanho = ByteArrayUtils.byteArrayToInt(ByteArrayUtils.subArray(byteArray, tamanhoDescritorHeader + 1, 1));
        this.nome = ByteArrayUtils.byteArrayToString(ByteArrayUtils.subArray(byteArray, tamanhoDescritorHeader + 2, byteArray.length - 6));

        return this;
    }

    public TipoDado getTipoDado() {
        return tipoDado;
    }
    public int getTamanho() {
        return  this.tamanho;
    }
}

