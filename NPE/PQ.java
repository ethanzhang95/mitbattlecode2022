package NPE;

public class PQ{
    public Node[] queue;
    public int queueSize = 0;

    // Does not use index 0
    public PQ(){
        queue = new Node[20];
    }
    // arrayLength must be > 1
    public PQ(int arrayLength){
        queue = new Node[arrayLength];
    }
    public boolean isEmpty(){
        return queueSize == 0;
    }
    public void swap(int a, int b){
        Node temp = queue[a];
        queue[a] = queue[b];
        queue[b] = temp;
    }
    public void push(Node value){
        queueSize++;
        if (queueSize == queue.length) {
            Node[] newQueue = new Node[queue.length * 2];
            for (int i = 0; i < queueSize; i++)
                newQueue[i] = queue[i];
            queue = newQueue;
        }
        queue[queueSize] = value;
        heapUp(queueSize);
    }
    public void heapUp(int k){
        int parent = k/2;
        if (parent == 0) {
            return;
        } else if(queue[parent].compareTo(queue[k])>0) {
            swap(parent, k);
            heapUp(parent);
        }
    }
    public void heapDown(int k, int size){
        int left=2*k, right=2*k+1;
        if (left==size && queue[k].compareTo(queue[size])>0){
            swap(k,size);
        } else if (right <= size){
            int min_pos;
            if (queue[left].compareTo(queue[right])<=0){
                min_pos = left;
            } else {
                min_pos = right;
            }
            if (queue[min_pos].compareTo(queue[k]) < 0){
                swap(k, min_pos);
                heapDown(min_pos, size);
            }
        }
    }
    public void reheap(){
        for (int k=queueSize/2; k>=0; k--){
            heapDown(k, queueSize);
        }
    }
    public Node pop(){
        if (isEmpty()){
            return null;
        }
        swap(1, queueSize);
        Node output = queue[queueSize];
        queue[queueSize--] = null;
        heapDown(1, queueSize);
        return output;
    }
    public String toString(){
        String output = "";
        for (int i = 1; i<=queueSize; i++){
            output += "("+queue[i].obj+","+queue[i].value+") ";
        }
        return output;
    }
}

class Node implements Comparable{
    public Object obj;
    public double value;
    public Node(Object obj, double value){
        this.obj = obj;
        this.value = value;
    }
    public int compareTo(Object node) {
        return Double.compare(value, ((Node)node).value);
    }
    public boolean equals(Object node){
        return compareTo(node) == 0;
    }
}

