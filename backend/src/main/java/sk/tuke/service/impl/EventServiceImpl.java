package sk.tuke.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.tuke.domain.Event;
import sk.tuke.domain.enumeration.Status;
import sk.tuke.repository.EventRepository;
import sk.tuke.service.EventService;
import sk.tuke.service.dto.EventDTO;
import sk.tuke.service.dto.specific.EventCreateDTO;
import sk.tuke.service.mapper.EventMapper;
import sk.tuke.service.mapper.specific.EventCreateMapper;

import java.util.Optional;

@Service
@Slf4j
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventCreateMapper eventCreateMapper;

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper, EventCreateMapper eventCreateMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.eventCreateMapper = eventCreateMapper;
    }

    /**
     * Delete the "id" event.
     *
     * @param id the id of the entity.
     */
    @Override
    public void setDelete(EventDTO event) {
        log.debug("Request to deleting event with ID: {}", event.getId());
        event.setStatus(Status.DELETED);
        eventRepository.saveAndFlush(eventMapper.toEntity(event));
    }

    /**
     * Save an event.
     *
     * @param eventDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public EventDTO save(EventDTO eventDTO) {
        log.debug("Request to saving event with details: {}", eventDTO);
        Event event = eventMapper.toEntity(eventDTO);
        event = eventRepository.save(event);

        return eventMapper.toDto(event);
    }

    /**
     * Get all the events.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    @Override
    public Page<EventCreateDTO> findAllByCity(Pageable pageable, Long cityId) {
        log.debug("Request to find all events");
        return eventRepository.findAllByCity_Id(pageable,cityId)
            .map(eventCreateMapper::toDto);
    }

    /**
     * Finds all petitions with pagination.
     *
     * @param pageable pagination debugrmation.
     * @return a paginated list of PetitionDTOs.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EventDTO> findAllActive(Pageable pageable) {
        log.debug("Request to find all active events");
        return eventRepository.findAllByStatus(pageable, Status.ACTIVE)
            .map(eventMapper::toDto);
    }



    /**
     * Gets the event by id.
     *
     * @param id the entity id
     * @return Optional with found entity
     * or {@link Optional#EMPTY} if not found
     */
    @Override
    public Optional<EventDTO> findOne(Long id) {
        log.debug("Request to find event with ID: {}", id);
        return eventRepository.findById(id)
            .map(eventMapper::toDto);
    }
}
