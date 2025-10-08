package commerce;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopperRepository extends JpaRepository<Shopper, Long> {

    Optional<Shopper> findByEmail(String email);
}
