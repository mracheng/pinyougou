package com.pinyougou.service;

import java.util.ArrayList;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;

public class UserDetailServiceImpl implements UserDetailsService {

	//set注入
	private SellerService sellerService;
	
	
	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		TbSeller seller = sellerService.findOne(username);
		
		
		ArrayList<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
		grantedAuths.add(new SimpleGrantedAuthority("ROLE_SELLER"));
		
		if (seller!=null) {
			if (seller.getStatus().equals("1")) {
				return new User(username, seller.getPassword(), grantedAuths);
			}else {
				return null;
			}
			
		}else {
			return null;
		}
		
	}

}
