package ca.ubc.cs.commandrecommender.Exception;

/**
 * Created by Spencer on 6/20/2014.
 */
public class DBConnectionException extends Exception{
    public DBConnectionException(String msg){
        super(msg);
    }

    public DBConnectionException(Throwable cause){
        super(cause);
    }
}
