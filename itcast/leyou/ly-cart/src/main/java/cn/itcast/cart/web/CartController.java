package cn.itcast.cart.web;

import cn.itcast.cart.config.JwtProperties;
import cn.itcast.cart.pojo.Cart;
import cn.itcast.cart.service.CartService;
import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 新增购物车
     *
     * @param cart
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart) {
        // 解析用户信息
        cartService.addCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();


    }

    /**
     * 查询当前用户购物车
     *
     * @param token
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Cart>> queryCartList(@CookieValue("LY_TOKEN") String token) {
        // 解析用户信息
        return ResponseEntity.ok(cartService.queryCartList());
    }

}
