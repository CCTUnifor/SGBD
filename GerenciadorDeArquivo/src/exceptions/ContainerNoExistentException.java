package exceptions;

import java.io.FileNotFoundException;

public class ContainerNoExistentException extends FileNotFoundException {

    public ContainerNoExistentException(String tableName) {
        super("Table ("+tableName+") not finded!");
    }
}
