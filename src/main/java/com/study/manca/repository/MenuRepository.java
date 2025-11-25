package com.study.manca.repository;

import com.study.manca.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findByCategory(Menu.MenuCategory category);

    List<Menu> findByIsAvailableTrue();

    List<Menu> findByCategoryAndIsAvailableTrue(Menu.MenuCategory category);

    boolean existsByName(String name);
}
