package sk.tuke.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.tuke.service.dto.EventDTO;
import sk.tuke.service.dto.specific.EventCreateDTO;

import java.util.Optional;

public interface EventService {

    /**
     * Delete the "id" event.
     *
     * @param id the id of the entity.
     */
    void setDelete(EventDTO event);

    /**
     * Save an event.
     *
     * @param eventDTO the entity to save.
     * @return the persisted entity.
     */
    EventDTO save(EventDTO eventDTO);

    /**
     * Get all the events.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */

    Page<EventDTO> findAllActive(Pageable pageable);

    /**
     * Gets the event by id.
     *
     * @param id the entity id
     * @return Optional with found entity
     * or {@link Optional#EMPTY} if not found
     */
    Optional<EventDTO> findOne(Long id);

    Page<EventCreateDTO> findAllByCity(Pageable pageable, Long cityId);


}

