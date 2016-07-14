package com.device.inspect.config.security.stateless;

import com.device.inspect.common.model.charater.Role;
import com.device.inspect.common.model.charater.RoleAuthority;
import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.repository.charater.RoleAuthorityRepository;
import com.device.inspect.common.repository.charater.RoleRepository;
import com.device.inspect.common.repository.charater.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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

	public final LoginUser loadUserByName(String name,String verify, Set<String> roleNames) throws UsernameNotFoundException {
        User user = userRepository.findByName(name);
        if (user == null) {
            throw new UsernameNotFoundException("user not found!");
        }
        if (!String.valueOf(user.getPassword()).equals(verify))
            throw new UsernameNotFoundException("user's password isn't correct!");
        
        Set<Role> roles = roleRepository.findByUserId(user.getId());

        List<Role> newRoles = new ArrayList<>(roleNames.size());

        for(Role role:roles) {
            if(roleNames.contains(role.getAuthority())) {
                newRoles.add(role);
            }
        }
//        if(newRoles.size() == 0) {
//            throw new UsernameNotFoundException("user not have correct role");
//        }

		final LoginUser loginUser =  new LoginUser(user);
		loginUser.setRoles(newRoles);
        detailsChecker.check(loginUser);
		return loginUser;
	}
}
