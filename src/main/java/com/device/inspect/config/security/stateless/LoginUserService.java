package com.device.inspect.config.security.stateless;

import com.device.inspect.common.model.charater.Role;
import com.device.inspect.common.model.charater.RoleAuthority;
import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.repository.charater.RoleAuthorityRepository;
import com.device.inspect.common.repository.charater.RoleRepository;
import com.device.inspect.common.repository.charater.UserRepository;
import com.device.inspect.common.util.transefer.ByteAndHex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class LoginUserService {

	@Autowired
	private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

	private final AccountStatusUserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();

	public final LoginUser loadUserByName(String name,String verify, String companyId,Set<String> roleNames) throws UsernameNotFoundException {
        User user = userRepository.findByName(name);
        if (user == null) {
            throw new UsernameNotFoundException("user not found!");
        }
        if (!String.valueOf(user.getPassword()).equals(verify))
            throw new UsernameNotFoundException("user's password isn't correct!");
        
        Role role = roleRepository.findByUserId(user.getId());

        List<Role> newRoles = new ArrayList<>(roleNames.size());
        if (null != role&&roleNames.contains(role.getAuthority()))
            newRoles.add(role);
        if (null==companyId&&!user.getRole().getRoleAuthority().getName().startsWith("SERVICE"))
            throw new UsernameNotFoundException("you're not a service manager!");
        if(null!=user.getRole().getRoleAuthority().getName()&&user.getRole().getRoleAuthority().getName().startsWith("SERVICE")){
            if (null!=companyId&&!"".equals(companyId))
                throw new UsernameNotFoundException("you are not a firm account!");
        }
        if (null!=companyId&&user.getRole().getRoleAuthority().getName().startsWith("FIRM")){
            String realId = "";
            try {
                realId = URLDecoder.decode(ByteAndHex.convertMD5(companyId),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (!user.getCompany().getId().toString().equals(realId))
                throw new UsernameNotFoundException("user's company isn't correct!");

        }


//        for(Role role:roles) {
//            if(roleNames.contains(role.getAuthority())) {
//                newRoles.add(role);
//            }
//        }
//        if(newRoles.size() == 0) {
//            throw new UsernameNotFoundException("user not have correct role");
//        }

		final LoginUser loginUser =  new LoginUser(user);
		loginUser.setRoles(newRoles);
        detailsChecker.check(loginUser);
		return loginUser;
	}
}
