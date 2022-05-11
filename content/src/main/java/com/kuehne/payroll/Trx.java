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
public class Trx {

    private @Id
    @GeneratedValue
    Long id;


    private String senderUserName;
    private String receiverUserName;
    private BigDecimal balance = BigDecimal.ZERO;

    private @Version
    @JsonIgnore
    Long version;

    public Trx(String senderUserName, String receiverUserName, BigDecimal balance) throws Exception {
        this.senderUserName = senderUserName;
        this.receiverUserName = receiverUserName;
        this.balance = balance;
    }


    private Trx() {
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trx trx = (Trx) o;
        return Objects.equals(id, trx.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, receiverUserName, senderUserName, balance, version);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Trx{" +
                "id=" + id +
                ", version=" + version +
                '}';
    }


    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) throws Exception {
        if (balance.compareTo(BigDecimal.ZERO) < 0)
            throw new Exception("Balance must be non negative");
        this.balance = balance;
    }

    public String getSenderUserName() {
        return senderUserName;
    }

    public void setSenderUserName(String senderUserName) throws Exception {
        if(senderUserName==null )throw new Exception("Eat This");
        this.senderUserName = senderUserName;
    }

    public String getReceiverUserName() {
        return receiverUserName;
    }

    public void setReceiverUserName(String receiverUserName) {
        this.receiverUserName = receiverUserName;
    }
}

