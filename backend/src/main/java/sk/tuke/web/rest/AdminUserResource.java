package sk.tuke.web.rest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sk.tuke.config.Constants;
import sk.tuke.domain.User;
import sk.tuke.domain.enumeration.ActiveStatus;
import sk.tuke.repository.UserRepository;
import sk.tuke.security.AuthoritiesConstants;
import sk.tuke.service.dto.UserInfoDTO;
import sk.tuke.service.impl.MailService;
import sk.tuke.service.impl.UserServiceImpl;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

import java.util.Optional;

/**
 * REST controller for managing users.
 * <p>
 * This class accesses the {@link sk.tuke.domain.User} entity, and needs to fetch its collection of authorities.
 * <p>
 * For a normal use-case, it would be better to have an eager relationship between User and Authority,
 * and send everything to the client side: there would be no View Model and DTO, a lot less code, and an outer-join
 * which would be good for performance.
 * <p>
 * We use a View Model and a DTO for 3 reasons:
 * <ul>
 * <li>We want to keep a lazy association between the user and the authorities, because people will
 * quite often do relationships with the user, and we don't want them to get the authorities all
 * the time for nothing (for performance reasons). This is the #1 goal: we should not impact our users'
 * application because of this use-case.</li>
 * <li> Not having an outer join causes n+1 requests to the database. This is not a real issue as
 * we have by default a second-level cache. This means on the first HTTP call we do the n+1 requests,
 * but then all authorities come from the cache, so in fact it's much better than doing an outer join
 * (which will get lots of data from the database, for each HTTP call).</li>
 * <li> As this manages users, for security reasons, we'd rather have a DTO layer.</li>
 * </ul>
 * <p>
 * Another option would be to have a specific JPA entity graph to handle this case.
 */
@RestController
@RequestMapping("/api/admin")
@Secured({AuthoritiesConstants.ADMIN})
public class AdminUserResource {

    private static final Logger LOG = LoggerFactory.getLogger(AdminUserResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserServiceImpl userServiceImpl;

    private final UserRepository userRepository;

    private final MailService mailService;

    public AdminUserResource(UserServiceImpl userServiceImpl, UserRepository userRepository, MailService mailService) {
        this.userServiceImpl = userServiceImpl;
        this.userRepository = userRepository;
        this.mailService = mailService;
    }


//    /**
//     * {@code POST  /admin/users}  : Creates a new user.
//     * <p>
//     * Creates a new user if the login and email are not already used, and sends an
//     * mail with an activation link.
//     * The user needs to be activated on creation.
//     *
//     * @param userDTO the user to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the login or email is already in use.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     * @throws BadRequestAlertException {@code 400 (Bad Request)} if the login or email is already in use.
//     */
//    @PostMapping("/users")
//    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
//    public ResponseEntity<User> createUser(@Valid @RequestBody AdminUserDTO userDTO) throws URISyntaxException {
//        LOG.debug("REST request to save User : {}", userDTO);
//
//        if (userDTO.getId() != null) {
//            throw new BadRequestAlertException("A new user cannot already have an ID", "userManagement", "idexists");
//            // Lowercase the user login before comparing with database
//        } else if (userRepository.findOneByLogin(userDTO.getLogin().toLowerCase()).isPresent()) {
//            throw new LoginAlreadyUsedException();
//        } else if (userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).isPresent()) {
//            throw new EmailAlreadyUsedException();
//        } else {
//            User newUser = userService.createUser(userDTO);
//            mailService.sendCreationEmail(newUser);
//            return ResponseEntity.created(new URI("/api/admin/users/" + newUser.getLogin()))
//                .headers(
//                    HeaderUtil.createAlert(applicationName, "A user is created with identifier " + newUser.getLogin(), newUser.getLogin())
//                )
//                .body(newUser);
//        }
//    }

//    /**
//     * {@code PUT /admin/users} : Updates an existing User.
//     *
//     * @param userDTO the user to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated user.
//     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already in use.
//     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already in use.
//     */
//    @PutMapping({ "/users", "/users/{login}" })
//    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
//    public ResponseEntity<AdminUserDTO> updateUser(
//        @PathVariable(name = "login", required = false) @Pattern(regexp = Constants.LOGIN_REGEX) String login,
//        @Valid @RequestBody AdminUserDTO userDTO
//    ) {
//        LOG.debug("REST request to update User : {}", userDTO);
//        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
//        if (existingUser.isPresent() && (!existingUser.orElseThrow().getId().equals(userDTO.getId()))) {
//            throw new EmailAlreadyUsedException();
//        }
//        existingUser = userRepository.findOneByLogin(userDTO.getLogin().toLowerCase());
//        if (existingUser.isPresent() && (!existingUser.orElseThrow().getId().equals(userDTO.getId()))) {
//            throw new LoginAlreadyUsedException();
//        }
//        Optional<AdminUserDTO> updatedUser = userService.updateUser(userDTO);
//
//        return ResponseUtil.wrapOrNotFound(
//            updatedUser,
//            HeaderUtil.createAlert(applicationName, "A user is updated with identifier " + userDTO.getLogin(), userDTO.getLogin())
//        );
//    }

    /**
     * {@code GET /admin/users} : get all users with all the details - calling this are only allowed for the administrators.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
     */
    @GetMapping("/users")
    public ResponseEntity<Page<UserInfoDTO>> getAllUsers(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get all User for an admin");

        final Page<UserInfoDTO> page = userServiceImpl.getAllManagedUsers(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page, headers, HttpStatus.OK);
    }


    /**
     * {@code GET /admin/users/:login} : get the "login" user.
     *
     * @param login the login of the user to find.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the "login" user, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/users/email/{login}")
    public ResponseEntity<UserInfoDTO> getUser(@PathVariable("login") @Pattern(regexp = Constants.LOGIN_REGEX) String login) {
        LOG.debug("REST request to get User by email: {}", login);
        return ResponseUtil.wrapOrNotFound(userServiceImpl.findUserByLogin(login));
    }

    /**
     * {@code GET /admin/users/:id} : get the "id" user.
     *
     * @param id the id of the user to find.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the "id" user, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<UserInfoDTO> getUser(@PathVariable("id") @Min(value = 0, message = "Wrong id value") Long id) {
        LOG.debug("REST request to get User by id: {}", id);
        return ResponseUtil.wrapOrNotFound(userServiceImpl.findUserById(id));
    }

    /**
     * {@code DELETE /admin/users/:id} : delete the "id" User.
     *
     * @param id the id of the user to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") @Min(value = 0, message = "Wrong id value") Long id) {
        LOG.debug("REST request to delete User: {}", id);
        userServiceImpl.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET  /organization/pending : Retrieves a paginated list of users with a PENDING status.
     *
     * <p>This endpoint returns a paginated list of users or organizations whose status is currently set to PENDING.
     * The response includes pagination headers.</p>
     *
     * @param pageable the pagination information provided by the client.
     * @return a {@link ResponseEntity} containing a {@link Page} of {@link UserInfoDTO} objects
     * representing the pending users, along with pagination headers and an HTTP status of OK (200).
     */
    @GetMapping("/organization/pending")
    public ResponseEntity<Page<UserInfoDTO>> getPendingOrganizations(Pageable pageable) {
        LOG.debug("REST request to get all users with status PENDING");

        final Page<UserInfoDTO> page = userServiceImpl.getPendingOrganizations(pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page, headers, HttpStatus.OK);
    }

    /**
     * GET  /organization/activate/{id} : Activates an organization with a specified ID if it has a PENDING status.
     *
     * <p>This endpoint activates an organization identified by the given ID, provided its status is PENDING.
     * If the organization does not exist, a 404 Not Found response is returned.
     * If the organization's status is not PENDING, a 400 Bad Request response is returned.</p>
     *
     * @param id the ID of the organization to be activated; must be a non-negative value.
     * @return a {@link ResponseEntity} with an HTTP status of OK (200) if the activation is successful,
     *         404 Not Found if the organization is not found, or 400 Bad Request if the organization's status
     *         is not PENDING.
     */
    @GetMapping("/organization/activate/{id}")
    public ResponseEntity<Void> activatePendingOrganization(@PathVariable @Min(value = 0, message = "Invalid ID") Long id) {
        LOG.debug("REST request to activate organization with id: {}", id);

        Optional<User> orgOpt = userServiceImpl.findUserByIdSpecific(id);
        if (orgOpt.isEmpty())
            return ResponseEntity.notFound().build();

        User org = orgOpt.get();
        if (org.getActiveStatus() != ActiveStatus.PENDING)
            return ResponseEntity.badRequest().build();
        userServiceImpl.activateOrganization(org);
        return ResponseEntity.ok().build();
    }

}
