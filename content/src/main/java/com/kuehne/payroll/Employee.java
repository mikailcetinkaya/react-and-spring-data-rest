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

import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jdk.internal.dynalink.support.NameCodec;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Entity
public class Employee {

	private @Id @GeneratedValue Long id;
	private String firstName;
	private String lastName;
	@Column(unique=true)
	private String userName;
	private String description;
	private BigDecimal balance=BigDecimal.TEN;
	private String password;

	private @Version @JsonIgnore Long version;

	private @ManyToOne Manager manager;

	private Employee() {}

	public Employee(String firstName, String lastName,String userName, String description, Manager manager) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.description = description;
		this.manager = manager;
		this.userName=userName;
		this.password=Manager.PASSWORD_ENCODER.encode("123");

	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Employee employee = (Employee) o;
		return Objects.equals(id, employee.id) &&
			Objects.equals(firstName, employee.firstName) &&
			Objects.equals(lastName, employee.lastName) &&
			Objects.equals(description, employee.description) &&
			Objects.equals(version, employee.version) &&
			Objects.equals(manager, employee.manager);
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, firstName, lastName, description, version, manager);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Manager getManager() {
		return manager;
	}

	public void setManager(Manager manager) {
		this.manager = manager;
	}

	@Override
	public String toString() {
		return "Employee{" +
			"id=" + id +
			", firstName='" + firstName + '\'' +
			", lastName='" + lastName + '\'' +
			", description='" + description + '\'' +
			", version=" + version +
			", manager=" + manager +
			'}';
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) throws Exception {
		if(balance.compareTo(BigDecimal.ZERO)<0)
			throw new Exception("Balance must be non negative");
		this.balance = balance;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = this.password!=null &&this.password.equals(password)? password : Manager.PASSWORD_ENCODER.encode(password);
	}
}

