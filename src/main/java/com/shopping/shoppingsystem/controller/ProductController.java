package com.shopping.shoppingsystem.controller;

import com.shopping.shoppingsystem.model.Category;
import com.shopping.shoppingsystem.model.Product;
import com.shopping.shoppingsystem.repository.CategoryRepository;
import com.shopping.shoppingsystem.repository.ProductRepository;
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
import java.util.List;

@Controller
@RequestMapping("/") // ROTA BASE: Mapeia para a Home
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private static final int PAGE_SIZE = 5;

    // --- 1. HOME: LISTAGEM DE PRODUTOS (READ) ---
    // Mapeia: GET / (http://localhost:8080/)
    @GetMapping
    public String home(
            Model model,
            @RequestParam(defaultValue = "0") int page) {

        Pageable pageConfig = PageRequest.of(page, PAGE_SIZE, Sort.by("name"));
        Page<Product> productPage = productRepository.findAll(pageConfig);

        // Buscamos todas as categorias para o <select> do formulário (caso o formulário esteja na home)
        // Mesmo com o formulário separado, é bom ter esta lista para referência ou filtros futuros.
        List<Category> categories = categoryRepository.findAll(Sort.by("name"));

        model.addAttribute("productPage", productPage);
        model.addAttribute("categories", categories);

        // Retorna o template 'home.html' (que fica em templates/)
        return "home";
    }

    // --- 2. EXIBIR FORMULÁRIO DE NOVO PRODUTO (CREATE - GET) ---
    // Mapeia: GET /produtos/novo
    @GetMapping("/produtos/novo")
    public String showProductForm(Model model) {
        model.addAttribute("produto", new Product());
        // Necessário para o <select>
        model.addAttribute("categories", categoryRepository.findAll(Sort.by("name")));

        // Retorna o template "product-form.html"
        return "product/product-form";
    }

    // --- 3. SALVAR PRODUTO (CREATE - POST) ---
    // Mapeia: POST /produtos/cadastrar
    @PostMapping("/produtos/cadastrar")
    public String addProduct(
            @Valid @ModelAttribute("produto") Product product,
            BindingResult result,
            RedirectAttributes attributes) {

        if (result.hasErrors()) {
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.produto", result);
            attributes.addFlashAttribute("produto", product);
            return "redirect:/produtos/novo"; // Retorna para a página do formulário
        }

        productRepository.save(product);
        attributes.addFlashAttribute("successMessage", "Produto salvo com sucesso!");
        return "redirect:/"; // REDIRECT PARA A HOME
    }

    // --- 4. VISUALIZAR DETALHES DO PRODUTO (READ - GET) ---
    // Mapeia: GET /produtos/detalhes/{id}
    @GetMapping("/produtos/detalhes/{id}")
    public String showProductDetails(
            @PathVariable("id") Long id,
            Model model) {

        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ID de Produto inválido: " + id));

        model.addAttribute("productDetails", product);

        // Retorna o novo template de detalhes
        return "product/product-detail";
    }


    // --- 5. EXIBIR FORMULÁRIO DE EDIÇÃO (UPDATE - GET) ---
    // Mapeia: GET /produtos/editar/{id}
    @GetMapping("/produtos/editar/{id}")
    public String showEditForm(
            @PathVariable("id") Long id,
            Model model) {

        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ID de Produto inválido: " + id));

        List<Category> categories = categoryRepository.findAll(Sort.by("name"));

        model.addAttribute("produto", product);
        model.addAttribute("categories", categories);
        return "product/product-form"; // Reutiliza o formulário de cadastro
    }

    // --- 6. ATUALIZAR (UPDATE - POST) ---
    // Mapeia: POST /produtos/atualizar/{id}
    @PostMapping("/produtos/atualizar/{id}")
    public String updateProduct(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("produto") Product product,
            BindingResult result,
            RedirectAttributes attributes) {

        product.setId(id);

        if (result.hasErrors()) {
            // Se houver erro na edição, mantemos o ID na URL para o redirect funcionar
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.produto", result);
            attributes.addFlashAttribute("produto", product);
            return "redirect:/produtos/editar/" + id;
        }

        productRepository.save(product);
        attributes.addFlashAttribute("successMessage", "Produto atualizado com sucesso!");
        return "redirect:/"; // REDIRECT PARA A HOME
    }

    // --- 7. EXCLUIR (DELETE - POST) ---
    // Mapeia: POST /produtos/excluir/{id}
    @PostMapping("/produtos/excluir/{id}")
    public String deleteProduct(
            @PathVariable("id") Long id,
            RedirectAttributes attributes) {

        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ID de Produto inválido: " + id));

        productRepository.delete(product);

        attributes.addFlashAttribute("successMessage", "Produto excluído com sucesso!");
        return "redirect:/"; // REDIRECT PARA A HOME
    }
}
