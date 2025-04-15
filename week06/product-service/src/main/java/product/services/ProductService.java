package product.services;


import product.entities.Product;

import java.util.List;

public interface ProductService {
    public Product save(Product product);
    public Product findById(Long id);
    public List<Product> findAll();
	Product updateQuantity(Long id, int quantity);
}
