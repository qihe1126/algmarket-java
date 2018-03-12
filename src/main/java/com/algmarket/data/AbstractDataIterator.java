package com.algmarket.data;

import com.algmarket.APIException;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;


public abstract class AbstractDataIterator<T> implements Iterator<T> {
    protected DataDirectory dir;
    protected String marker;
    private int offset;
    private List<String> children;
    private boolean loadedFirstPage;

    protected AbstractDataIterator(DataDirectory dir) {
        this.dir = dir;
    }

    public boolean hasNext() {
        attemptToLoadFirstPage();

        return
                (children != null && offset < children.size()) ||
                        (children != null && offset >= children.size() && marker != null);
    }

    public T next() throws NoSuchElementException {
        attemptToLoadFirstPage();

        if(children == null) {
            throw new NoSuchElementException();
        }

        if(marker != null && offset >= children.size()) {
            try {
                loadNextPage();
            } catch(APIException ex) {
                throw new NoSuchElementException(ex.getMessage());
            }
        }

        if(offset < children.size()) {
            offset++;
            return newDataObjectInstance(dir.trimmedPath + "/" + children.get(offset-1));

        } else {
            throw new NoSuchElementException();
        }
    }

    private void attemptToLoadFirstPage() {
        if (!loadedFirstPage) {
            loadedFirstPage = true;
            try {
                loadNextPage();
            } catch(APIException ex) {
                throw new NoSuchElementException(ex.getMessage());
            }
        }
    }

    final protected void setChildrenAndMarker(List<String> newChildren, String marker) {
        if (children != null && offset < children.size())
            throw new IllegalStateException("Skipping elements");

        children = newChildren;
        offset = 0;
        this.marker = marker;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    abstract protected void loadNextPage() throws APIException;

    abstract protected T newDataObjectInstance(String dataUri);
}
