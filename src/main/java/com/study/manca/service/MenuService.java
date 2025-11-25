package com.study.manca.service;

import com.study.manca.dto.MenuRequest;
import com.study.manca.dto.MenuResponse;
import com.study.manca.entity.Menu;
import com.study.manca.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;

    public List<MenuResponse> findAll() {
        return menuRepository.findAll().stream()
                .map(MenuResponse::from)
                .collect(Collectors.toList());
    }

    public List<MenuResponse> findAvailable() {
        return menuRepository.findByIsAvailableTrue().stream()
                .map(MenuResponse::from)
                .collect(Collectors.toList());
    }

    public List<MenuResponse> findByCategory(Menu.MenuCategory category) {
        return menuRepository.findByCategoryAndIsAvailableTrue(category).stream()
                .map(MenuResponse::from)
                .collect(Collectors.toList());
    }

    public MenuResponse findById(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found with id: " + id));
        return MenuResponse.from(menu);
    }

    @Transactional
    public MenuResponse create(MenuRequest request) {
        if (menuRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Menu already exists: " + request.getName());
        }

        Menu menu = request.toEntity();
        Menu savedMenu = menuRepository.save(menu);
        return MenuResponse.from(savedMenu);
    }

    @Transactional
    public MenuResponse update(Long id, MenuRequest request) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found with id: " + id));

        menu.setName(request.getName());
        menu.setCategory(request.getCategory());
        menu.setPrice(request.getPrice());
        menu.setDescription(request.getDescription());
        menu.setIsAvailable(request.getIsAvailable());

        return MenuResponse.from(menu);
    }

    @Transactional
    public MenuResponse updatePartial(Long id, MenuRequest request) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found with id: " + id));

        if (request.getName() != null) {
            menu.setName(request.getName());
        }
        if (request.getCategory() != null) {
            menu.setCategory(request.getCategory());
        }
        if (request.getPrice() != null) {
            menu.setPrice(request.getPrice());
        }
        if (request.getDescription() != null) {
            menu.setDescription(request.getDescription());
        }
        if (request.getIsAvailable() != null) {
            menu.setIsAvailable(request.getIsAvailable());
        }

        return MenuResponse.from(menu);
    }

    @Transactional
    public void delete(Long id) {
        if (!menuRepository.existsById(id)) {
            throw new IllegalArgumentException("Menu not found with id: " + id);
        }
        menuRepository.deleteById(id);
    }
}
