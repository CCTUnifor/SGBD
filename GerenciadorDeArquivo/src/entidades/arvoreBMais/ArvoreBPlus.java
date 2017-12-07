package entidades.arvoreBMais;

import entidades.blocos.RowId;

import java.util.LinkedList;
import java.util.Queue;

public class ArvoreBPlus {

    private Node root;
    private final int order;
    private final int partition;

    public ArvoreBPlus(int order) {
        this.order = order;
        this.partition = (this.order - 1) / 2;
        this.root = null;
    }

    public void insert(String valuesColumns, RowId rowId) {
        Key key = new Key(valuesColumns, rowId);
        if (this.isEmpty()) {
            Node node = new Node(this.order, this.order - 1);
            node.insertionKey(key);
            this.root = node;
        } else {
            findInsertion(this.root, -1, key);
        }
    }

    public RowId findKey(Key key) {
        Key k = find(this.root, -1, key);
        if (k != null)
            return k.getRowId();
        return null;
    }

    private Key find(Node node, int positionNodeChildren, Key key) {
        if (positionNodeChildren != -1) {
            if (node.getChildren(positionNodeChildren) == null) {
                if (key.compareToKey(node.getKey(positionNodeChildren)) == 0) {
                    return node.getKey(positionNodeChildren);
                } else {
                    return null;
                }
            } else {
                return find(node.getChildren(positionNodeChildren), -1, key);
            }
        } else {
            int i = 0;
            int position = -1;
            while (i < node.getIndexInsertionKeys()) {
                if (key.compareToKey(node.getKey(i)) <= 0) {
                    position = i;
                    break;
                }
                i++;
            }
            if (position != -1) {
                return find(node, position, key);
            } else {
                return find(node, i, key);
            }
        }
    }

    private void findInsertion(Node node, int positionNodeChildren, Key key) {
        if (positionNodeChildren != -1) {
            if (node.getChildren(positionNodeChildren) == null) {
                if (node.getIndexInsertionKeys() == node.getNumberMaxKeys()) {
                    split(node, null, null, key);
                } else {
                    node.insertionKey(key);
                }
            } else {
                findInsertion(node.getChildren(positionNodeChildren), -1, key);
            }
        } else {
            int i = 0;
            int position = -1;
            while (i < node.getIndexInsertionKeys()) {
                if (key.compareToKey(node.getKey(i)) < 0) {
                    position = i;
                    break;
                }
                i++;
            }
            if (position != -1) {
                findInsertion(node, position, key);
            } else {
                findInsertion(node, i, key);
            }

        }
    }

    private void split(Node node, Node nL, Node nR, Key pK) {

        Key keys[] = new Key[node.getNumberMaxKeys() + 1];
        for (int i = 0; i < keys.length - 1; i++) keys[i] = node.getKey(i);
        keys[keys.length - 1] = pK;
        sortArray(keys);
        Node nodeLeft = new Node(this.order, this.order - 1);
        Node nodeRight = new Node(this.order, this.order - 1);
        Key partitionKey = keys[this.partition];
        for (int i = 0; i <= this.partition; i++) nodeLeft.insertionKey(keys[i]);
        for (int i = this.partition + 1; i < keys.length; i++) nodeRight.insertionKey(keys[i]);
        if (node.isLeaf()) {
            nodeLeft.setNext(nodeRight);
            nodeRight.setPrevious(nodeLeft);
            if (node.getPrevious() != null) {
                nodeLeft.setPrevious(node.getPrevious());
                node.getPrevious().setNext(nodeLeft);
            }
            if (node.getNext() != null) {
                nodeRight.setNext(node.getNext());
                node.getNext().setPrevious(nodeRight);
            }
            nodeLeft.setLeaf(true);
            nodeRight.setLeaf(true);
        }

        if (node.getFather() == null) {
            if (node.pointersNull()) {
                node.clearNode();
                node.insertionKey(partitionKey);
                node.setChildren(node.getIndexInsertionKeys() - 1, nodeLeft);
                node.setChildren(node.getIndexInsertionKeys(), nodeRight);
                nodeLeft.setFather(node);
                nodeRight.setFather(node);
                nodeLeft.setNext(nodeRight);
                nodeRight.setPrevious(nodeLeft);
                nodeLeft.setLeaf(true);
                nodeRight.setLeaf(true);
            } else {
                Node newNode = new Node(this.order, this.order - 1);
                newNode.insertionKey(partitionKey);
                newNode.setChildren(0, nodeLeft);
                newNode.setChildren(1, nodeRight);
                nodeLeft.setFather(newNode);
                nodeRight.setFather(newNode);
                reallocationOfPointers(node, nodeRight, nodeLeft, nL, nR);
                this.root = newNode;
                node.clearNode();
                node.setNext(null);
                node.setPrevious(null);
                node.setLeaf(false);
                node.setFather(null);
            }
        } else {
            if (node.getFather().hasSpace()) {
                int positionInsertion = -1;
                for (int i = 0; i < node.getFather().getIndexInsertionKeys(); i++) {
                    if (partitionKey.compareToKey(node.getFather().getKey(i)) < 0) {
                        positionInsertion = i;
                        break;
                    }
                }
                if (positionInsertion != -1) {
                    for (int i = node.getFather().getIndexInsertionKeys(); i > positionInsertion; i--) {
                        node.getFather().setKey(i, node.getFather().getKey(i - 1));
                        Node aux = node.getFather().getChildren(i);//next do nodeRight
                        node.getFather().setChildren(i, null);
                        node.getFather().setChildren(i + 1, aux);
                    }
                    node.getFather().setKey(positionInsertion, partitionKey);
                    node.getFather().setChildren(positionInsertion, nodeLeft);
                    node.getFather().setChildren(positionInsertion + 1, nodeRight);
                } else {
                    node.getFather().setKey(node.getFather().getIndexInsertionKeys(), partitionKey);
                    node.getFather().setChildren(node.getFather().getIndexInsertionKeys(), nodeLeft);
                    node.getFather().setChildren(node.getFather().getIndexInsertionKeys() + 1, nodeRight);
                }
                node.getFather().incrementIndexInsertionKeys();
                nodeLeft.setFather(node.getFather());
                nodeRight.setFather(node.getFather());
                node.clearNode();
                node.setNext(null);
                node.setPrevious(null);
                node.setLeaf(false);
                node.setFather(null);
            } else {
                if (nL != null && nR != null) reallocationOfPointers(node, nodeRight, nodeLeft, nL, nR);
                Node nodeFather = node.getFather();
                node.clearNode();
                node.setNext(null);
                node.setPrevious(null);
                node.setLeaf(false);
                node.setFather(null);
                split(nodeFather, nodeLeft, nodeRight, partitionKey);
            }
        }
    }

    private void reallocationOfPointers(Node node, Node nodeRight, Node nodeLeft, Node nL, Node nR) {
        int positionSplit = 0;
        while (positionSplit < node.getIndexInsertionKeys() && node.getChildren(positionSplit).getKey(0) != null)
            positionSplit++;
        if (positionSplit == this.partition) {
            nodeLeft.setChildren(positionSplit, nL);
            nodeRight.setChildren(0, nR);
            nL.setFather(nodeLeft);
            nR.setFather(nodeRight);
            for (int i = 0; i < positionSplit; i++) {
                nodeLeft.setChildren(i, node.getChildren(i));
                node.getChildren(i).setFather(nodeLeft);
            }
            for (int i = positionSplit + 1, j = 1; i <= node.getIndexInsertionKeys(); i++, j++) {
                nodeRight.setChildren(j, node.getChildren(i));
                node.getChildren(i).setFather(nodeRight);
            }
        } else {
            if (positionSplit < this.partition) {
                nodeLeft.setChildren(positionSplit, nL);
                nodeLeft.setChildren(positionSplit + 1, nR);
                nL.setFather(nodeLeft);
                nR.setFather(nodeLeft);
            } else {
                nodeRight.setChildren((positionSplit - this.partition - 1), nL);
                nodeRight.setChildren((positionSplit - this.partition), nR);
                nL.setFather(nodeRight);
                nR.setFather(nodeRight);
            }
            for (int i = 0; i < nodeLeft.getIndexInsertionKeys(); i++) {
                if (nodeLeft.getChildren(i) != null && nodeLeft.getChildren(i).equals(null)) {
                    for (int j = 0; j < node.getIndexInsertionKeys(); j++) {
                        if (nodeLeft.getKey(i).equals(node.getKey(j))) {
                            nodeLeft.setChildren(i, node.getChildren(j));
                            node.getChildren(j).setFather(nodeLeft);
                            break;
                        }
                    }
                }
            }
            for (int i = 0; i < nodeRight.getIndexInsertionKeys(); i++) {
                if (nodeRight.getChildren(i) != null && nodeRight.getChildren(i).equals(null)) {
                    for (int j = 0; j < node.getIndexInsertionKeys(); j++) {
                        if (nodeRight.getKey(i).equals(node.getKey(j))) {
                            nodeRight.setChildren(i, node.getChildren(j));
                            node.getChildren(j).setFather(nodeRight);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void sortArray(Key[] array) {
        for (int i = 1; i < array.length; i++) {
            Key key = array[i];
            int j = i - 1;
            while (j >= 0 && array[j].compareToKey(key) > 0) array[j + 1] = array[j--];
            array[j + 1] = key;
        }
    }

    public void printTreeBPlus() {
        if (!this.isEmpty()) {
            Queue<Node> queue = new LinkedList<Node>();
            queue.add(this.root);
            Node tempNode = null;
            while (!queue.isEmpty()) {
                tempNode = queue.remove();
                for (int i = 0; i < this.order - 1; i++) {
                    System.out.print(" | " + tempNode.getKey(i).getValue() + " | ");
                    if (!tempNode.isLeaf()) {
                        for (int j = 0; j < this.order; j++) {
                            queue.add(tempNode.getChildren(j));
                        }
                    }
                }
            }

        }
    }

    public boolean isEmpty() {
        return this.root == null ? true : false;
    }

    public Node getRoot() {
        return root;
    }

    public int getOrder() {
        return order;
    }

    public int getPartition() {
        return partition;
    }
}
