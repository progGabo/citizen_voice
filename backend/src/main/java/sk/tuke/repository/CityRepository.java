package sk.tuke.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.tuke.domain.City;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    Page<City> findAll(Pageable pageable);
}
