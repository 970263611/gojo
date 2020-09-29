package web.request;

import java.util.Enumeration;
import java.util.Iterator;

public class WebEnumeration<E> implements Enumeration<E> {

    private Iterator<E> iterator;

    public WebEnumeration(Iterator<E> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasMoreElements() {
        return iterator.hasNext();
    }

    @Override
    public E nextElement() {
        return iterator.next();
    }

}