package com.nogueira.quota.repositories.jpa;

import com.nogueira.quota.models.User;
import com.nogueira.quota.repositories.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMySQLRepository extends JpaRepository<User, Long>, UserRepository {
}