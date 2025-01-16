package sk.tuke.web.rest;


import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sk.tuke.domain.User;
import sk.tuke.domain.enumeration.Status;
import sk.tuke.security.AuthoritiesConstants;
import sk.tuke.service.EventService;
import sk.tuke.service.dto.EventDTO;
import sk.tuke.service.dto.specific.EventCreateDTO;

import java.util.Optional;


@RestController
@RequestMapping("/api/event")
public class EventResource {

    private final Logger log = LoggerFactory.getLogger(EventResource.class);
    private final EventService eventService;

    public EventResource(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Creates a new Event.
     *
     * @param EventCreateDTO DTO of the event to create.
     * @return ResponseEntity with the created EventDTO and status 201 (Created).
     * @throws IllegalArgumentException if the eventDTO has a non-null ID.
     */
    @PostMapping("/new")
    @Secured({AuthoritiesConstants.ORGANIZATION})
    public ResponseEntity<EventDTO> createEvent(@RequestBody EventCreateDTO eventCreateDTO) {
        log.info("Request to create event: {}", eventCreateDTO);

        EventDTO eventDTO = new EventDTO();
        eventDTO.setName(eventCreateDTO.getName());
        eventDTO.setDescription(eventCreateDTO.getDescription());
        eventDTO.setDateTo(eventCreateDTO.getDateTo());
        eventDTO.setDateFrom(eventCreateDTO.getDateFrom());
        eventDTO.setCityId(eventCreateDTO.getCityId());
        eventDTO.setStatus(Status.ACTIVE);
        Long user = getUserId();
        eventDTO.setUserId(user);

        EventDTO result = eventService.save(eventDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Updates an existing event.
     *
     * @param updated contains information of event that needs to be updated.
     * @param id is id of the event.
     * @return esponseEntity with the updated eventDTO and status 200 (OK).
     */
    @Secured({AuthoritiesConstants.ORGANIZATION})
    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> updateEvent(@Valid @RequestBody EventCreateDTO updated, @PathVariable Long id) {
        log.info("Request to update event with ID: {}", id);

        Optional<EventDTO> eventOpt = eventService.findOne(id);

        if (eventOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Long userId = getUserId();
        EventDTO eventDTO = eventOpt.get();
        if (userId != eventDTO.getUserId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        eventDTO.setName(updated.getName());
        eventDTO.setDescription(updated.getDescription());
        eventDTO.setDateTo(updated.getDateTo());
        eventDTO.setDateFrom(updated.getDateFrom());
       // eventDTO.setCityID(updated.getCityID());

        EventDTO result = eventService.save(eventDTO);
        return ResponseEntity.ok(result);
    }



    /**
     * Retrieves an event by its ID.
     *
     * @param id ID of the event to retrieve.
     * @return ResponseEntity with the found eventDTO and status 200 (OK), or 404 (Not Found) if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEvent(@PathVariable Long id) {
        log.info("Request to retrieve event with ID: {}", id);

        return eventService.findOne(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Retrieves all events with pagination.
     *
     * @param pageable pagination information.
     * @return ResponseEntity with a paginated list of eventDTOs and status 200 (OK).
     */
    @GetMapping("/all/{cityId}")
    public ResponseEntity<Page<EventCreateDTO>> getAllEvents(@PathVariable Long cityId, Pageable pageable) {
        log.info("Request to retrieve all events with pagination");

        Page<EventCreateDTO> page = eventService.findAllByCity(pageable, cityId);

        return ResponseEntity.ok(page);

    }

    /**
     * Deletes an event by its ID.
     *
     * @param id ID of the event to delete.
     * @return ResponseEntity with status 204 (No Content).
     */
    @Secured({AuthoritiesConstants.ORGANIZATION})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        log.info("Request to delete event with ID: {}", id);

        Optional<EventDTO> eventOpt = eventService.findOne(id);

        if (eventOpt.isEmpty())
            return ResponseEntity.badRequest().build();

        Long userId = getUserId();
        EventDTO event = eventOpt.get();
        if (userId != event.getUserId())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        eventService.setDelete(event);
        return ResponseEntity.noContent().build();
    }

    private Long getUserId () {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return user.getId();
    }

}
