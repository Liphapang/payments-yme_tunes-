package com.packt.accounting.dao;

import com.packt.accounting.model.User;
import java.util.List;

public interface UserDao {
    List<User> findAll();
}

