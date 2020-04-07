package exception.impl;

import exception.GojoException;

public class NoLoginException extends Exception implements GojoException {

    public NoLoginException(){
        super();
    }

    public NoLoginException(String msg){
        super(msg);
    }

}
