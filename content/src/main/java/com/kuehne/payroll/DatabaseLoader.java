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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;



@Component
public class DatabaseLoader implements CommandLineRunner {

	private final EmployeeRepository employees;
	private final ManagerRepository managers;

	@Autowired
	public DatabaseLoader(EmployeeRepository employeeRepository,
						  ManagerRepository managerRepository) {

		this.employees = employeeRepository;
		this.managers = managerRepository;
	}

	@Override
	public void run(String... strings) throws Exception {

		Manager miko = this.managers.save(new Manager("miko", "yuji",
							"ROLE_MANAGER"));
		Manager yuji = this.managers.save(new Manager("yuji", "miko",
							"ROLE_MANAGER"));

		SecurityContextHolder.getContext().setAuthentication(
			new UsernamePasswordAuthenticationToken("miko", "doesn't matter",
				AuthorityUtils.createAuthorityList("ROLE_MANAGER")));

		this.employees.save(new Employee("Frodo", "Baggins","frodo", "ring bearer", miko));
		this.employees.save(new Employee("Bilbo", "Baggins", "bilbo","burglar", miko));
		this.employees.save(new Employee("Gandalf", "the Grey", "gangan","wizard", miko));

		SecurityContextHolder.getContext().setAuthentication(
			new UsernamePasswordAuthenticationToken("yuji", "doesn't matter",
				AuthorityUtils.createAuthorityList("ROLE_MANAGER")));

		this.employees.save(new Employee("Samwise", "Gamgee", "sammy","gardener", yuji));
		this.employees.save(new Employee("Merry", "Brandybuck", "merry","pony rider", yuji));
		this.employees.save(new Employee("Peregrin", "Took", "pere","pipe smoker", yuji));

		SecurityContextHolder.clearContext();
	}
}
