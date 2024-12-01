
package com.fantasydrawer.ecommerce.backend.repository;

import com.fantasydrawer.ecommerce.backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	@Modifying
	@Query("UPDATE Product p SET p.version = p.version + 1 WHERE p.id = :id AND p.version = :version")
	int updateVersion(@Param("id") Long id, @Param("version") Long version);
}
