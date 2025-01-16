package sk.tuke.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.tuke.domain.CityAuthorities;

import java.util.List;

@Repository
public interface CityAuthoritiesRepository extends JpaRepository<CityAuthorities, Integer> {

    List<CityAuthorities> findAllByCityId(Long cityId);
}
