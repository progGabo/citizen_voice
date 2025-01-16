package sk.tuke.web.rest;

import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sk.tuke.domain.Address;
import sk.tuke.domain.City;
import sk.tuke.domain.User;
import sk.tuke.repository.CityRepository;
import sk.tuke.repository.UserRepository;
import sk.tuke.security.AuthoritiesConstants;
import sk.tuke.service.UserService;
import sk.tuke.service.dto.ArticleDTO;
import sk.tuke.service.dto.UserInfoDTO;
import sk.tuke.service.impl.MailService;
import sk.tuke.service.impl.UserServiceImpl;
import sk.tuke.service.mapper.AddressMapper;
import sk.tuke.service.mapper.UserInfoMapper;
import tech.jhipster.web.util.ResponseUtil;

import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Secured({AuthoritiesConstants.ORGANIZATION, AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN})
public class UserResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserService userService;

    private final UserInfoMapper userInfoMapper;

    private final AddressMapper addressMapper;
    private final CityRepository cityRepository;

    public UserResource(UserService userService,
                        UserInfoMapper userInfoMapper, AddressMapper addressMapper,
                        CityRepository cityRepository) {
        this.userService = userService;
        this.addressMapper = addressMapper;
        this.userInfoMapper = userInfoMapper;
        this.cityRepository = cityRepository;
    }

    /**
     * GET  /detail : Retrieves information about the currently logged-in user or organization.
     *
     * <p>This endpoint returns the details of the authenticated user, admin or organization,
     * if available in the current security context. The user information is retrieved based
     * on the user ID extracted from the security context.</p>
     *
     * @return a {@link ResponseEntity} containing {@link UserInfoDTO} with the details
     * of the currently logged-in user, wrapped in an {@link ResponseUtil#wrapOrNotFound(Optional)}
     * response. If the user is not found, a 404 Not Found response is returned.
     */

    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER, AuthoritiesConstants.ORGANIZATION})
    @PostMapping("/update")
    public ResponseEntity<UserInfoDTO> updateSettings(@RequestBody UserInfoDTO userInfoDTO, @RequestParam(required = false) Long cityId) {
        Optional<User> userOpt = userService.findUserByIdSpecific(getUserId());
        if (userOpt.isEmpty())
            return ResponseEntity.badRequest().build();

        User user = userOpt.get();

        if (!Objects.equals(getUserId(), user.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if (userInfoDTO.getFirstName() != null)
            user.setFirstName(userInfoDTO.getFirstName());
        if (userInfoDTO.getLastName() != null)
            user.setLastName(userInfoDTO.getLastName());
        if (userInfoDTO.getEmail() != null)
            user.setEmail(userInfoDTO.getEmail());
        if (userInfoDTO.getAddress() != null) {
            if (cityId == null) {
                return ResponseEntity.badRequest().build();
            }
            Optional<City> city = cityRepository.findById(cityId);
            if (city.isEmpty())
                return ResponseEntity.badRequest().build();

            Address address = addressMapper.toEntity(userInfoDTO.getAddress());
            address.setCity(city.get());
            user.setAddress(address);
        }
        if (userInfoDTO.getTitle() != null)
            user.setTitle(userInfoDTO.getTitle());
        if (userInfoDTO.getAlertsOn() != null)
            user.setAlertsOn(userInfoDTO.getAlertsOn());
        if (userInfoDTO.getDateOfBirth() != null)
            user.setDateOfBirth(userInfoDTO.getDateOfBirth());

        UserInfoDTO result = userService.updateSettings(user);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/detail")
    public ResponseEntity<UserInfoDTO> getUserInfo() {
        LOG.debug("REST request to get info about currently logged User/Org");

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();

        return ResponseUtil.wrapOrNotFound(userService.findUserById(user.getId()));
    }

    private Long getUserId () {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return user.getId();
    }
}
