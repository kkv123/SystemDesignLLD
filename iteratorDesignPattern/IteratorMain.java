package iteratorDesignPattern;

import java.util.List;

interface Iterator<T> {
    T next();
    boolean hasNext();
}

class ListIterator<T> implements Iterator<T>{
    List<T> list;

    public ListIterator(List<T> ls){
        list= ls;
    }

    @Override
    public T next() {
        T last= list.getLast();

        return last;
    }

    @Override
    public boolean hasNext() {
        return list!=null;
    }
}
public class IteratorMain {

    public static void main(String[] args) {
        System.out.println("this is working as expected");
    }
}
