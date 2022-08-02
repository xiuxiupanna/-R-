package cn.itcast.cart.service;

import cn.itcast.cart.interceptors.UserInterceptor;
import cn.itcast.cart.pojo.Cart;
import com.leyou.auth.pojo.UserInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    private static final String KEY_PREFIX = "cart:uid:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 新增购物车
     * @param cart
     *
     */
    public void addCart(Cart cart) {
        UserInfo userInfo = UserInterceptor.getUserInfo();
        // 准备key
        String key = KEY_PREFIX + userInfo.getId();
        // 获取operation 对象,并且绑定key,剩下的就是操作map
        BoundHashOperations<String, String , String > boundHashOps = redisTemplate.boundHashOps(key);
        // 准备hashKey，也就是商品id
        String hKey = cart.getSkuId().toString();
        // 查询购物车
        // 判断是否存在
        if (boundHashOps.hasKey(hKey)) {
            // 存在则先取出
            Cart cacheCart = JsonUtils.toBean(boundHashOps.get(hKey), Cart.class);
            // 修改数量
            cart.setNum(cacheCart.getNum() + cart.getNum());
        }
        // 写回redis
        boundHashOps.put(hKey, JsonUtils.toString(cart));

    }

    /**
     * 查询购物车集合
     * @return
     */
    public List<Cart> queryCartList() {
        UserInfo userInfo = UserInterceptor.getUserInfo();
        // 准备key
        String key = KEY_PREFIX + userInfo.getId();
        // 先判断是否存在用户信息
        if (redisTemplate.hasKey(key) == null || !redisTemplate.hasKey(key)) {
            throw new LyException(ExceptionEnum.CART_NOT_FOUND);

        }
        // 获取operation 对象,并且绑定key,剩下的就是操作map
        BoundHashOperations<String, String , String > boundHashOps = redisTemplate.boundHashOps(key);
        // 查询用户所有购物车
        List<String> values = boundHashOps.values();
        if (boundHashOps.size() <= 0) {
            throw new LyException(ExceptionEnum.CART_NOT_FOUND);

        }
        // 购物车有数据，转为Cart对象
        List<Cart> cartList = values.stream().map(v -> JsonUtils.toBean(v, Cart.class)).collect(Collectors.toList());
        return cartList;
    }
}
