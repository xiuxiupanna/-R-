package cn.itcast.travel.exception;

/**
 * 用户名不能为空异常
 */
public class UserNotExistsException extends Exception {
    public UserNotExistsException(){

    }
    public UserNotExistsException(String errorMsg){
        super(errorMsg);
    }
}
