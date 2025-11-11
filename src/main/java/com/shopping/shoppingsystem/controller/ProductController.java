package com.shopping.shoppingsystem.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopping.shoppingsystem.model.Category;
import com.shopping.shoppingsystem.model.Product;
import com.shopping.shoppingsystem.repository.CategoryRepository;
import com.shopping.shoppingsystem.repository.ProductRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/produtos") // ROTA BASE MAIS ESPECÍFICA (opcional, mas boa prática)
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private static final int PAGE_SIZE = 5;

    // --- OTIMIZAÇÃO: CARREGAMENTO AUTOMÁTICO DE CATEGORIAS ---
    // Este método é executado antes de qualquer @GetMapping ou @PostMapping
    // e adiciona "categories" ao Model, evitando repetição de código.
    @ModelAttribute("categories")
    public List<Category> getCategories() {
        return categoryRepository.findAll(Sort.by("name"));
    }

    // --- 1. HOME: LISTAGEM DE PRODUTOS (READ) ---
    // Mapeia: GET / (http://localhost:8080/produtos) - Rota ajustada
    @GetMapping
    public String listProducts(
            Model model,
            @RequestParam(defaultValue = "0") int page) {

        Pageable pageConfig = PageRequest.of(page, PAGE_SIZE, Sort.by("name"));
        Page<Product> productPage = productRepository.findAll(pageConfig);

        model.addAttribute("productPage", productPage);
        return "home"; // Assumindo que 'home.html' agora lista os produtos
    }

    // --- 2. EXIBIR FORMULÁRIO DE NOVO PRODUTO (CREATE - GET) ---
    // Mapeia: GET /produtos/novo
    @GetMapping("/novo")
    public String showProductForm(Model model) {
        model.addAttribute("produto", new Product());
        // 'categories' já está no Model via @ModelAttribute
        return "product/product-form";
    }

    // --- 3. SALVAR PRODUTO (CREATE - POST) ---
    // Mapeia: POST /produtos/cadastrar
    @PostMapping("/cadastrar")
    public String addProduct(
            @Valid @ModelAttribute("produto") Product product,
            BindingResult result,
            RedirectAttributes attributes) {

        if (result.hasErrors()) {
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.produto", result);
            attributes.addFlashAttribute("produto", product);
            return "redirect:/produtos/novo";
        }

        productRepository.save(product);
        attributes.addFlashAttribute("successMessage", "Produto salvo com sucesso!");
        return "redirect:/produtos"; // Redireciona para a nova rota base
    }

    // --- 4. VISUALIZAR DETALHES DO PRODUTO (READ - GET) ---
    // Mapeia: GET /produtos/detalhes/{id}
    @GetMapping("/detalhes/{id}")
    public String showProductDetails(@PathVariable("id") Long id, Model model) {

        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ID de Produto inválido: " + id));

        model.addAttribute("productDetails", product);
        return "product/product-detail";
    }

    // --- 5. EXIBIR FORMULÁRIO DE EDIÇÃO (UPDATE - GET) ---
    // Mapeia: GET /produtos/editar/{id}
    @GetMapping("/editar/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {

        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ID de Produto inválido: " + id));

        model.addAttribute("produto", product);
        // 'categories' já está no Model via @ModelAttribute
        return "product/product-form";
    }

    // --- 6. ATUALIZAR (UPDATE - POST) ---
    // Mapeia: POST /produtos/atualizar/{id}
    @PostMapping("/atualizar/{id}")
    public String updateProduct(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("produto") Product product,
            BindingResult result,
            RedirectAttributes attributes) {

        // ESSA LINHA É CRUCIAL, MAS FOI REMOVIDA NO SEU ÚLTIMO CÓDIGO!
        // Ela garante que o objeto 'product' a ser salvo tenha o ID correto,
        // transformando a operação em UPDATE.
        product.setId(id);

        if (result.hasErrors()) {
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.produto", result);
            attributes.addFlashAttribute("produto", product);
            return "redirect:/produtos/editar/" + id;
        }

        productRepository.save(product);
        attributes.addFlashAttribute("successMessage", "Produto atualizado com sucesso!");
        return "redirect:/produtos";
    }

    // --- 7. EXCLUIR (DELETE - POST) ---
    // Mapeia: POST /produtos/excluir/{id}
    @PostMapping("/excluir/{id}")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes attributes) {

        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ID de Produto inválido: " + id));

        productRepository.delete(product);

        attributes.addFlashAttribute("successMessage", "Produto excluído com sucesso!");
        return "redirect:/produtos";
    }
}
