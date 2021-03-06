package com.device.inspect.config.security.stateless;

import com.device.inspect.common.influxdb.InfluxDBManager;
import com.device.inspect.common.model.charater.Role;
import com.device.inspect.common.model.charater.RoleAuthority;
import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.repository.charater.RoleAuthorityRepository;
import com.device.inspect.common.repository.charater.RoleRepository;
import com.device.inspect.common.repository.charater.UserRepository;
import com.device.inspect.common.util.transefer.ByteAndHex;
import com.device.inspect.common.util.transefer.UserRoleDifferent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class LoginUserService {

    protected static Logger logger = LogManager.getLogger(LoginUserService.class);

	@Autowired
	private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

	private final AccountStatusUserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();

    /**
     * 根据ByteAndHex中的convertMD5()方法，对companyId进行一次加密
     * 两次解密，并通过UrlDecode对companyId按照UTF-8的编码进行解码
     * @param name 用户名
     * @param verify
     * @param companyId
     * @param roleNames
     * @return
     * @throws UsernameNotFoundException
     */
	public final LoginUser loadUserByName(String name,String verify, String companyId,Set<String> roleNames) throws UsernameNotFoundException {
        //判断是否有companyId
        if (companyId!=null&&!"".equals(companyId))
            name= name+"@"+companyId;
        User user = userRepository.findByName(name);
        if (user == null) {
            throw new UsernameNotFoundException("user not found!");
        }

        // check whether login retry times has exceed maximum limit
        if(user.getLastPasswordErrorDate() != null && (new Date().getTime() -  user.getLastPasswordErrorDate().getTime()) < 24*60*60*1000 && user.getPasswordErrorRetryTimes() == 3 ){
            throw new UsernameNotFoundException(String.format("user's password isn't correct! %d", user.getPasswordErrorRetryTimes()));
        }

        // check whether password is correct
        if (!String.valueOf(user.getPassword()).equals(verify)){
            if(user.getLastPasswordErrorDate() == null || new Date().getTime() - user.getLastPasswordErrorDate().getTime() > 24*60*60*1000){
                user.setLastPasswordErrorDate(new Date());
                user.setPasswordErrorRetryTimes(1);
            }
            else{
                user.setPasswordErrorRetryTimes(user.getPasswordErrorRetryTimes() + 1);
            }
            userRepository.save(user);
            throw new UsernameNotFoundException(String.format("user's password isn't correct! %d", user.getPasswordErrorRetryTimes()));
        }
        else{
            if(user.getPasswordErrorRetryTimes() == null || user.getPasswordErrorRetryTimes() != 0 ){
                user.setPasswordErrorRetryTimes(0);
                userRepository.save(user);
            }
        }
        // check whether password is expired (longer than 90 days)
        if(user.getLatestPasswordUpdateTime() == null){
            user.setLatestPasswordUpdateTime(new Date());
            userRepository.save(user);
        }

	    long password_used_time = new Date().getTime() - user.getLatestPasswordUpdateTime().getTime();
        if( password_used_time - new Long(90*24*60*60)*1000 > 0 ){
            throw new UsernameNotFoundException("password is expired");
        }

        List<Role> roles = roleRepository.findByUserId(user.getId());
        user.setRoles(roles);
        List<Role> newRoles = new ArrayList<>(roleNames.size());

        if (null==companyId&&!UserRoleDifferent.userStartWithService(user))
            throw new UsernameNotFoundException("you're not a service manager!");
        if(null!=user.getRoles()&&UserRoleDifferent.userStartWithService(user)){
            if (null!=companyId&&!"".equals(companyId))
                throw new UsernameNotFoundException("you are not a firm account!");
        }
        if (null!=companyId&&UserRoleDifferent.userStartWithFirm(user)){
            
            if (!user.getCompany().getCompanyId().equals(companyId))
                throw new UsernameNotFoundException("user's company isn't correct!");

        }


        for(Role role:roles) {
            if(roleNames.contains(role.getAuthority())) {
                newRoles.add(role);
            }
        }
        if(newRoles.size() == 0) {
            throw new UsernameNotFoundException("user not have correct role");
        }

		final LoginUser loginUser =  new LoginUser(user);
		loginUser.setRoles(newRoles);
        detailsChecker.check(loginUser);

		return loginUser;
	}
}
