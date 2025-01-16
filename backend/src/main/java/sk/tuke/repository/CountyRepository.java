package sk.tuke.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.tuke.domain.County;

@Repository
public interface CountyRepository extends JpaRepository<County, Integer> {
}
