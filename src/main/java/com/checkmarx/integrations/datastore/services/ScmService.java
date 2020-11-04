package com.checkmarx.integrations.datastore.services;

import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.repositories.ScmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
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

	public Scm createOrGetScmByBaseUrl(String baseUrl) {
		Scm scmByBaseUrl = getScmByBaseUrl(baseUrl);

		if (scmByBaseUrl != null) {
			return scmByBaseUrl;
		} else {
			Scm scm = Scm.builder()
					.baseUrl(baseUrl)
					.build();
			return createScm(scm);
		}
	}

	public void createOrUpdateScm(Scm scm) {
		Scm scmToUpdate = createOrGetScmByBaseUrl(scm.getBaseUrl());
		scmToUpdate.setClientId(scm.getClientId());
		scmToUpdate.setClientSecret(scm.getClientSecret());
		scmRepository.save(scmToUpdate);
	}

	private Scm createScm(Scm scm) {
		return scmRepository.saveAndFlush(scm);
	}
}