package exceptions;

public class IndexNoExistentException extends Exception {
    public IndexNoExistentException(String indexName) {
        super("Index (" + indexName + ") not finded!");
    }
}
