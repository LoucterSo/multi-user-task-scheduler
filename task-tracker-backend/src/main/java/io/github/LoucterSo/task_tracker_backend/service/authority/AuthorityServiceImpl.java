package io.github.LoucterSo.task_tracker_backend.service.authority;

import io.github.LoucterSo.task_tracker_backend.entity.user.Authority;
import io.github.LoucterSo.task_tracker_backend.repository.user.AuthorityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorityServiceImpl implements AuthorityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorityServiceImpl.class);
    private final AuthorityRepository authorityRepository;

    @Override
    public void save(Authority authority) {
        LOGGER.info("Saving authority with role {}", authority.getRole().name());
        authorityRepository.save(authority);
    }
}
