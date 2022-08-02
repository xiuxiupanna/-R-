package cn.itcast.travel.exception;

/**
 * 用户名不能为空异常
 */
public class PasswordErrorException extends Exception {
    public PasswordErrorException(){

    }
    public PasswordErrorException(String errorMsg){
        super(errorMsg);
    }
}
