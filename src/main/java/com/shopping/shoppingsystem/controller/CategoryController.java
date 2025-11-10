package com.shopping.shoppingsystem.controller;

import com.shopping.shoppingsystem.model.Category;
import com.shopping.shoppingsystem.repository.CategoryRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private static final int PAGE_SIZE = 5;

    // --- 1. LISTAGEM DE CATEGORIAS E EXIBIÇÃO DO FORMULÁRIO INICIAL (READ) ---
    // Mapeia: GET /categorias
    @GetMapping
    public String listCategories(
            Model model,
            @RequestParam(defaultValue = "0") int page) {

        Pageable pageConfig = PageRequest.of(page, PAGE_SIZE, Sort.by("name"));
        Page<Category> categoryPage = categoryRepository.findAll(pageConfig);
        model.addAttribute("categoryPage", categoryPage);

        // Retorna o template de listagem (category-list.html)
        return "category/category-list";
    }

    // --- 2. EXIBIR FORMULÁRIO DE NOVA CATEGORIA (CREATE - GET) ---
    // Mapeia: GET /categorias/cadastrar (NOVA ROTA SIMPLIFICADA)
    @GetMapping("/cadastrar")
    public String showCategoryForm(Model model) {
        model.addAttribute("categoria", new Category());
        return "category/category-form";
    }

    // --- 3. SALVAR CATEGORIA (CREATE - POST) ---
    // Mapeia: POST /categorias/cadastrar
    @PostMapping("/cadastrar")
    public String addCategory(
            @Valid @ModelAttribute("categoria") Category category,
            BindingResult result,
            RedirectAttributes attributes) {

        if (result.hasErrors()) {
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.categoria", result);
            attributes.addFlashAttribute("categoria", category);
            // REDIRECT CORRIGIDO: Volta para o formulário /categorias/cadastrar
            return "redirect:/categorias/cadastrar";
        }

        categoryRepository.save(category);
        attributes.addFlashAttribute("successMessage", "Categoria salva com sucesso!");
        return "redirect:/categorias"; // REDIRECT PARA A LISTAGEM
    }

    // --- 4. EXIBIR FORMULÁRIO DE EDIÇÃO (UPDATE - GET) ---
    // Mapeia: GET /categorias/editar/{id}
    @GetMapping("/editar/{id}")
    public String showEditForm(
            @PathVariable("id") Long id,
            Model model) {

        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ID de Categoria inválido: " + id));

        model.addAttribute("categoria", category);
        return "category/category-form"; // Reutiliza o formulário de cadastro
    }

    // --- 5. ATUALIZAR (UPDATE - POST) ---
    // Mapeia: POST /categorias/atualizar/{id}
    @PostMapping("/atualizar/{id}")
    public String updateCategory(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("categoria") Category category,
            BindingResult result,
            RedirectAttributes attributes) {

        category.setId(id);
        if (result.hasErrors()) {
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.categoria", result);
            attributes.addFlashAttribute("categoria", category);
            // REDIRECT CORRIGIDO: Volta para a edição correta
            return "redirect:/categorias/editar/" + id;
        }

        categoryRepository.save(category);
        attributes.addFlashAttribute("successMessage", "Categoria atualizada com sucesso!");
        return "redirect:/categorias"; // REDIRECT PARA A LISTAGEM
    }

    // --- 6. EXCLUIR (DELETE - POST) ---
    // Mapeia: POST /categorias/excluir/{id}
    @PostMapping("/excluir/{id}")
    public String deleteCategory(
            @PathVariable("id") Long id,
            RedirectAttributes attributes) {

        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ID de Categoria inválido: " + id));

        categoryRepository.delete(category);

        attributes.addFlashAttribute("successMessage", "Categoria excluída com sucesso!");
        return "redirect:/categorias"; // REDIRECT PARA A LISTAGEM
    }
}
