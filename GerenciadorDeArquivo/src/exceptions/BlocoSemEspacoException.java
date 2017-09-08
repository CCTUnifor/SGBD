package exceptions;

public class BlocoSemEspacoException extends Throwable {
    public BlocoSemEspacoException(){
        super("Bloco não tem mais espaço");
    }
}
