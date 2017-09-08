package exceptions;

public class ContainerNoExistent extends Exception {

    public ContainerNoExistent() {
        super("Não existe Container para adicionar o bloco.");
    }
}
