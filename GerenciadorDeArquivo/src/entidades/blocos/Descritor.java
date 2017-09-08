package entidades.blocos;

import interfaces.IBinary;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Descritor implements IBinary {
    private TipoDado tipoDado;
    private String nome;
    private int tamanho;

    public Descritor(String coluna) {
        String[] x = coluna.split( "\\[");
        this.nome = x[0];
        this.tipoDado = x[1].contains("I") ? TipoDado.INTEIRO : TipoDado.STRING;

        Pattern pat = Pattern.compile("\\(([0-9]+)\\)");
        Matcher mat = pat.matcher(coluna);
        if (mat.find())
            this.tamanho = Integer.parseInt(mat.group(1));
    }

    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }

    @Override
    public <T> T fromByteArray(byte[] byteArray) {
        return null;
    }
}
