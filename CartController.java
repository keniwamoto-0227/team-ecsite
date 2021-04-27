package jp.co.internous.sugar.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import jp.co.internous.sugar.model.domain.TblCart;
import jp.co.internous.sugar.model.domain.dto.CartDto;
import jp.co.internous.sugar.model.form.CartForm;
import jp.co.internous.sugar.model.mapper.TblCartMapper;
import jp.co.internous.sugar.model.session.LoginSession;

@Controller
@RequestMapping("/sugar/cart")
public class CartController {

	@Autowired
	private TblCartMapper cartMapper; 

	@Autowired
	private LoginSession loginSession; 
	
	private Gson gson = new Gson();
	
	@RequestMapping("/")
	public String index(Model m) {

		int userId = loginSession.isLoginFlag() ? loginSession.getUserId() : loginSession.getGuestUserId();
		
		List<CartDto> carts = cartMapper.findByUserId(userId);

		m.addAttribute("loginSession", loginSession);
		m.addAttribute("carts", carts);
		return "cart";
	}
	
	@RequestMapping("/add")
	public String addCart(CartForm f,Model m) {

		int userId = loginSession.isLoginFlag() ? loginSession.getUserId() : loginSession.getGuestUserId();
		
		f.setUserId(userId);

		TblCart cart = new TblCart(f);
		int result = 0;
		if(cartMapper.findCountByUserIdAndProductId(userId,f.getProductId()) > 0) {
			result = cartMapper.update(cart);
		} else {
			result = cartMapper.insert(cart);
		}
		if(result > 0) {
			List<CartDto> carts = cartMapper.findByUserId(userId);

			m.addAttribute("loginSession",loginSession);
			m.addAttribute("carts",carts);
		}
		return "cart";
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/delete")
	public boolean deleteCart(@RequestBody String checkedIdList) {
		int result = 0;
		
		Map<String, List<String>> map =gson.fromJson(checkedIdList,Map.class);
		List<String> checkedIds = map.get("checkedIdList");
		
		result = cartMapper.deleteById(checkedIds);
		return result > 0;
	}
}
