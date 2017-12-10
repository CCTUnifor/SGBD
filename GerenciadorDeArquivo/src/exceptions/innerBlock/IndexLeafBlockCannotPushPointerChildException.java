package exceptions.innerBlock;

public class IndexLeafBlockCannotPushPointerChildException extends Exception {
    public IndexLeafBlockCannotPushPointerChildException() {
        super("Leaf index block cannot push child pointer!");
    }
}
