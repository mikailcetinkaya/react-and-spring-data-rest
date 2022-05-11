/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kuehne.payroll;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;



@Component
public class SpringDataJpaUserDetailsService implements UserDetailsService {
	Logger logger = LoggerFactory.getLogger(SpringDataJpaUserDetailsService.class);

	private final ManagerRepository repository;
	private final EmployeeRepository employeeRepository;

	@Autowired
	public SpringDataJpaUserDetailsService(ManagerRepository repository,EmployeeRepository employeeRepository) {
		this.repository = repository;
		this.employeeRepository=employeeRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException  {
		try {
			Manager manager = this.repository.findByName(name);
			return new User(manager.getName(), manager.getPassword(),
					AuthorityUtils.createAuthorityList(manager.getRoles()));
		}catch (Exception e){

		}

		logger.debug(name);
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken("SYSTEM", "doesn't matter",
						AuthorityUtils.createAuthorityList(Roles.MANAGER)));
		Employee emp=this.employeeRepository.findByUserName(name);
		SecurityContextHolder.clearContext();
		if(emp==null) throw new IllegalArgumentException("Invalid Username");
		if(emp.getPassword()==null)
			throw new IllegalArgumentException("Get password from manager"
				+ (emp.getManager()==null?"":":"+emp.getManager().getName()));

		return new User(emp.getUserName(), emp.getPassword(),
					AuthorityUtils.createAuthorityList(Roles.EMPLOYEE));




	}

}

