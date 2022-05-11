package com.kuehne.payroll;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;

import java.util.Optional;


@PreAuthorize("isAuthenticated()")
public interface TrxRepository extends PagingAndSortingRepository<Trx, Long> {
    
    @Override
    @PreAuthorize("authentication?.name !=null")
    Trx save(@Param("trx") Trx trx);

    @Override
    @PreAuthorize("@trxRepository.findById(#id)?.senderUserName == authentication?.name")
    void deleteById(@Param("id") Long id);

    @Override
    @PreAuthorize("#trx?.senderUserName == authentication?.name")
    void delete(@Param("trx") Trx trx);

    Trx findByReceiverUserName(String userName);

}


