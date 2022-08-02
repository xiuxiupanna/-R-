package cn.itcast.travel.exception;

/**
 * 用户名不能为空异常
 */
public class UserExistsException extends Exception {
    public UserExistsException(){

    }
    public UserExistsException(String errorMsg){
        super(errorMsg);
    }
}
