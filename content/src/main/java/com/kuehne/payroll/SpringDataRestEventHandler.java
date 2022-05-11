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
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.math.BigDecimal;


@Component
@RepositoryEventHandler
public class SpringDataRestEventHandler {

	Logger logger = LoggerFactory.getLogger(SpringDataRestEventHandler.class);
	private final ManagerRepository managerRepository;
	private final EmployeeRepository employeeRepository;
	@Autowired
	public SpringDataRestEventHandler(ManagerRepository managerRepository,EmployeeRepository employeeRepository) {
		this.managerRepository  = managerRepository;
		this.employeeRepository = employeeRepository;

	}

	@HandleBeforeCreate
	@HandleBeforeSave
	public void applyUserInformationUsingSecurityContext(Employee employee) {

		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		Manager manager = this.managerRepository.findByName(name);
		if (manager == null) {
			Manager newManager = new Manager();
			newManager.setName(name);
			newManager.setRoles(new String[]{Roles.MANAGER});
			manager = this.managerRepository.save(newManager);
		}
		employee.setManager(manager);
	}

	@Transactional
	@HandleBeforeCreate
	@HandleBeforeSave
	public void applyUserInformationUsingSecurityContext(Trx trx) throws Exception {
		logger.info(trx.getSenderUserName());
		logger.info(trx.getReceiverUserName());
		String name = SecurityContextHolder.getContext().getAuthentication().getName();

		if(trx.getReceiverUserName()==null) throw new Exception("Bad Receiver");
		if(trx.getSenderUserName()==null) throw new Exception("Bad Sender");
		if(trx.getSenderUserName().equals(trx.getReceiverUserName())) throw new Exception("Sender Receiver is SAME");
		if(trx.getBalance()==null || trx.getBalance().compareTo(BigDecimal.ZERO)<0) throw new Exception("Bad Amount");

		Employee receiver = this.employeeRepository.findByUserName(trx.getReceiverUserName());
		Employee sender = this.employeeRepository.findByUserName(trx.getSenderUserName());

		Manager manager = this.managerRepository.findByName(trx.getSenderUserName());

		receiver.setBalance(receiver.getBalance().add(trx.getBalance()));

		if(manager==null)
		{
			if(sender.getBalance().compareTo(trx.getBalance())<0)
				throw new Exception("Insufficient Funds");
			sender.setBalance(sender.getBalance().subtract(trx.getBalance()));
			this.employeeRepository.save(sender);
			SecurityContextHolder.getContext().setAuthentication(
					new UsernamePasswordAuthenticationToken("SYSTEM", "doesn't matter",
							AuthorityUtils.createAuthorityList(Roles.MANAGER)));

			this.employeeRepository.save(receiver);
			SecurityContextHolder.getContext().setAuthentication(
					new UsernamePasswordAuthenticationToken(name, "doesn't matter",
							AuthorityUtils.createAuthorityList(Roles.EMPLOYEE)));
		}
		else{
			this.employeeRepository.save(receiver);
		}


	}
}

