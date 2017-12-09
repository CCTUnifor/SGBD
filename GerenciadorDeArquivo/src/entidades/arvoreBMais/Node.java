package entidades.arvoreBMais;


public class Node {

    private int numberMaxPointersNode;
    private int numberMaxKeys;
    private Key[] keys; // "tupla"
    private Node father;
    private Node[] childrens; // "lista de ponteiro de nodes filhos"
    private boolean leaf;
    private Node next;
    private Node previous;
    private int indexInsertionKeys;

    public Node(int numberMaxPointersNode, int numberMaxKeys) {
        this.numberMaxPointersNode = numberMaxPointersNode;
        this.numberMaxKeys = numberMaxKeys;
        this.keys = new Key[this.numberMaxKeys];
        this.father = null;
        this.childrens = new Node[numberMaxPointersNode];
        this.leaf = false;
        this.next = null;
        this.previous = null;
        this.indexInsertionKeys = 0;
    }

    public Node getPrevious() {
        return previous;
    }

    public void setPrevious(Node previous) {
        this.previous = previous;
    }

    public int getNumberMaxPointersNode() {
        return numberMaxPointersNode;
    }

    public void setNumberMaxPointersNode(int numberMaxPointersNode) {
        this.numberMaxPointersNode = numberMaxPointersNode;
    }

    public int getNumberMaxKeys() {
        return numberMaxKeys;
    }

    public void setNumberMaxKeys(int numberMaxKeys) {
        this.numberMaxKeys = numberMaxKeys;
    }

    public Key[] getKeys() {
        return keys;
    }

    public void setKeys(Key[] keys) {
        this.keys = keys;
    }

    public void setKey(int positionInsertion, Key key) {
        if (positionInsertion > -1 && positionInsertion < this.numberMaxKeys) this.keys[positionInsertion] = key;
    }

    public Node getFather() {
        return father;
    }

    public void setFather(Node father) {
        this.father = father;
    }

    public Node[] getChildrens() {
        return childrens;
    }

    public boolean pointersNull() {
        int pos = -1;
        for (int i = 0; i < this.childrens.length; i++) {
            if (this.childrens[i] != null) {
                pos = i;
                break;
            }
        }
        return pos == -1 ? true : false;
    }

    public void setChildrens(Node[] childrens) {
        this.childrens = childrens;
    }

    public void setChildren(int indexChildren, Node node) {
        if (indexChildren > -1 && indexChildren < this.numberMaxPointersNode) this.childrens[indexChildren] = node;
    }

    public boolean isLeaf() {
        return this.childrens.length == 0;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public int getIndexInsertionKeys() {
        return indexInsertionKeys;
    }

    public void incrementIndexInsertionKeys() {
        this.indexInsertionKeys++;
    }

    public boolean hasSpace() {
        return this.indexInsertionKeys < this.numberMaxKeys ? true : false;
    }

    public Node getChildren(int indexChildren) {
        return (indexChildren > -1 && indexChildren < this.numberMaxPointersNode) ? this.childrens[indexChildren] : null;
    }

    public Key getKey(int indexKey) {
        return (indexKey > -1 && indexKey < this.numberMaxKeys) ? this.keys[indexKey] : null;
    }

    public void insertionKey(Key key) {
        if (this.indexInsertionKeys < this.numberMaxKeys) {
            if (this.indexInsertionKeys == 0) {
                this.keys[this.indexInsertionKeys] = key;
            } else {
                int positionInsertion = -1;
                for (int i = 0; i < this.indexInsertionKeys; i++) {
                    if (key.compareToKey(this.keys[i]) < 0) {
                        positionInsertion = i;
                        break;
                    }
                }
                if (positionInsertion != -1) {
                    for (int i = indexInsertionKeys; i > positionInsertion; i--) {
                        this.keys[i] = this.keys[i - 1];
                    }
                    this.keys[positionInsertion] = key;
                } else {
                    this.keys[this.indexInsertionKeys] = key;
                }
            }
            this.indexInsertionKeys++;
        }
    }

    public void insertionNodeChildren(Node node, int indexInsertionChildrens) {
        if (indexInsertionChildrens > -1 && indexInsertionChildrens < this.numberMaxPointersNode) {
            this.childrens[indexInsertionChildrens] = node;
        }
    }

    public void clearNode() {
        for (int i = 0; i < this.childrens.length; i++) this.childrens[i] = null;
        for (int i = 0; i < this.keys.length; i++) this.keys[i] = null;
        this.indexInsertionKeys = 0;
    }

    public int getNumberNodeInsertion()
    {
        int i = 0;
        while(i < this.childrens.length && this.childrens[i++] != null);
        return i-1;
    }
}
