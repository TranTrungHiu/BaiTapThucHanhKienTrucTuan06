package product.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import product.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
