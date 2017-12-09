package exceptions;

public class ContainerNoExistentException extends Exception {

    public ContainerNoExistentException(String tableName) {
        super("Table ("+tableName+") not finded!");
    }
}
