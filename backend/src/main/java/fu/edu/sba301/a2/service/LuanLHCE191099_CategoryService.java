package fu.edu.sba301.a2.service;

import fu.edu.sba301.a2.dto.LuanLHCE191099_CategoryInput;
import fu.edu.sba301.a2.dto.LuanLHCE191099_CategoryResponse;
import fu.edu.sba301.a2.entity.LuanLHCE191099_Category;
import fu.edu.sba301.a2.exception.LuanLHCE191099_ApiException;
import fu.edu.sba301.a2.repository.LuanLHCE191099_CategoryRepository;
import fu.edu.sba301.a2.repository.LuanLHCE191099_NewsRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LuanLHCE191099_CategoryService {

    private final LuanLHCE191099_CategoryRepository categoryRepository;
    private final LuanLHCE191099_NewsRepository newsRepository;

    public LuanLHCE191099_CategoryService(
        LuanLHCE191099_CategoryRepository categoryRepository,
        LuanLHCE191099_NewsRepository newsRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.newsRepository = newsRepository;
    }

    public List<LuanLHCE191099_CategoryResponse> getAllCategories() {
        List<LuanLHCE191099_Category> categories = categoryRepository.findAll(Sort.by("id"));
        List<LuanLHCE191099_CategoryResponse> responses = new ArrayList<>();
        for (LuanLHCE191099_Category category : categories) {
            responses.add(toResponse(category));
        }
        return responses;
    }

    public List<LuanLHCE191099_CategoryResponse> searchCategories(String searchText) {
        String keyword = searchText.trim().toLowerCase(Locale.ROOT);
        List<LuanLHCE191099_Category> categories = categoryRepository.findAll(Sort.by("id"));
        List<LuanLHCE191099_CategoryResponse> responses = new ArrayList<>();

        for (LuanLHCE191099_Category category : categories) {
            if (category.getName().toLowerCase(Locale.ROOT).contains(keyword)) {
                responses.add(toResponse(category));
            }
        }
        return responses;
    }

    public LuanLHCE191099_CategoryResponse getCategoryById(Long categoryId) {
        return toResponse(findCategoryOrThrow(categoryId));
    }

    @Transactional
    public LuanLHCE191099_CategoryResponse createCategory(LuanLHCE191099_CategoryInput input) {
        LuanLHCE191099_Category category = new LuanLHCE191099_Category();
        applyInput(category, input);
        return toResponse(categoryRepository.saveAndFlush(category));
    }

    @Transactional
    public LuanLHCE191099_CategoryResponse updateCategory(
        Long categoryId,
        LuanLHCE191099_CategoryInput input
    ) {
        LuanLHCE191099_Category category = findCategoryOrThrow(categoryId);
        applyInput(category, input);
        return toResponse(categoryRepository.saveAndFlush(category));
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        LuanLHCE191099_Category category = findCategoryOrThrow(categoryId);
        if (newsRepository.existsByCategory_Id(categoryId)) {
            throw new LuanLHCE191099_ApiException(
                409,
                "This category is used by news articles and cannot be deleted."
            );
        }
        if (categoryRepository.existsByParent_Id(categoryId)) {
            throw new LuanLHCE191099_ApiException(409, "This category has child categories.");
        }
        categoryRepository.delete(category);
        categoryRepository.flush();
    }

    private void applyInput(LuanLHCE191099_Category category, LuanLHCE191099_CategoryInput input) {
        LuanLHCE191099_Category parent = null;
        if (input.parentId() != null) {
            parent = findCategoryOrThrow(input.parentId());
        }

        // Walk upward from the chosen parent. Reaching this category would create a cycle.
        LuanLHCE191099_Category ancestor = parent;
        while (ancestor != null) {
            if (ancestor.getId().equals(category.getId())) {
                throw new LuanLHCE191099_ApiException(
                    400,
                    "A category cannot be its own ancestor."
                );
            }
            ancestor = ancestor.getParent();
        }

        category.setName(input.name().trim());
        category.setDescription(input.description());
        category.setStatus(input.status());
        category.setParent(parent);
    }

    private LuanLHCE191099_Category findCategoryOrThrow(Long categoryId) {
        return categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new LuanLHCE191099_ApiException(404, "Category not found."));
    }

    private LuanLHCE191099_CategoryResponse toResponse(LuanLHCE191099_Category category) {
        Long parentId = null;
        String parentName = null;
        if (category.getParent() != null) {
            parentId = category.getParent().getId();
            parentName = category.getParent().getName();
        }

        // Read LAZY relationships inside the service transaction, then return a flat DTO.
        return new LuanLHCE191099_CategoryResponse(
            category.getId(),
            category.getName(),
            category.getDescription(),
            parentId,
            parentName,
            category.getStatus()
        );
    }
}
