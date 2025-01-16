package sk.tuke.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.tuke.domain.Event;
import sk.tuke.domain.enumeration.Status;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findAllByStatus(Pageable pageable, Status status);

    Page<Event> findAllByCity_Id(Pageable pageable, Long cityId);

    //Optional<Event> findByTitle(String title);

}
