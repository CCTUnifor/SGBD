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
        String[] x = coluna.split( "\\[");
        this.nome = x[0];
        this.tipoDado = x[1].contains("I") ? TipoDado.INTEIRO : TipoDado.STRING;

        Pattern pat = Pattern.compile("\\(([0-9]+)\\)");
        Matcher mat = pat.matcher(coluna);
        if (mat.find()){
            if (this.tipoDado == TipoDado.INTEIRO)
                this.tamanho = 4;
            else
                this.tamanho = Integer.parseInt(mat.group(1));
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
        if (this.tipoDado == TipoDado.INTEIRO)
            parse += "I";
        else
            parse += "A";
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
}

