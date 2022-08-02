package cn.lezijie.factory;

import cn.lezijie.controller.TypeController;

/**
 * 定义实例化工厂
 */
public class InstanceFactory {
    /**
     * 定义方法
     * @return
     */
    public TypeController createController() {
        return new TypeController();



    }




}
