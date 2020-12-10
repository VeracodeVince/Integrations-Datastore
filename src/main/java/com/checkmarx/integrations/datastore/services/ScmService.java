package com.checkmarx.integrations.datastore.services;

import com.checkmarx.integrations.datastore.controllers.exceptions.ScmNotFoundException;
import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.repositories.ScmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.checkmarx.integrations.datastore.utils.ErrorConstsMessages.SCM_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScmService {

    private final ScmRepository scmRepository;

	public void deleteScm(Long id) {
		scmRepository.deleteById(id);
	}

	public List<Scm> getAllScms() {
		return scmRepository.findAll();
	}

	public Scm getScmByBaseUrl(String baseUrl) {
		return scmRepository.getScmByBaseUrl(baseUrl);
	}

	public void createOrUpdateScm(Scm scm) {
		Scm scmToUpdate = createOrGetScmByBaseUrl(scm.getBaseUrl());
		scmToUpdate.setClientId(scm.getClientId());
		scmToUpdate.setClientSecret(scm.getClientSecret());
		scmRepository.save(scmToUpdate);
	}

	public Scm getScmByScmUrl(String scmUrl) {
		return Optional.ofNullable(scmRepository.getScmByBaseUrl(scmUrl))
					.orElseThrow(() -> new ScmNotFoundException(String.format(SCM_NOT_FOUND, scmUrl)));
	}

	private Scm createOrGetScmByBaseUrl(String baseUrl) {
		Scm scmByBaseUrl = getScmByBaseUrl(baseUrl);

		if (scmByBaseUrl != null) {
			log.trace("createOrGetScmByBaseUrl: scmByBaseUrl exists:{}", scmByBaseUrl);
			return scmByBaseUrl;
		} else {
			log.trace("createOrGetScmByBaseUrl: creating new scmByBaseUrl");
			Scm scm = Scm.builder()
					.baseUrl(baseUrl)
					.build();
			return createScm(scm);
		}
	}

	private Scm createScm(Scm scm) {
		return scmRepository.saveAndFlush(scm);
	}
}