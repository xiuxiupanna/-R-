package cn.itcast.travel.exception;

/**
 *
 */
public class UserNoActiveException extends Exception {
    public UserNoActiveException(){

    }
    public UserNoActiveException(String errorMsg){
        super(errorMsg);
    }
}
