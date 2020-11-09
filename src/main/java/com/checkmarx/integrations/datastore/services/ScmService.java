package com.checkmarx.integrations.datastore.services;

import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.repositories.ScmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScmService {

    private final ScmRepository scmRepository;
    private final OrgService orgService;

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

	public ScmOrg createOrGetScmOrgByScmUrl(String scmUrl, String orgName) {
		Scm scmByBaseUrl = createOrGetScmByBaseUrl(scmUrl);
		log.trace("createOrGetScmByBaseUrl: Scm:{}", scmByBaseUrl);
		ScmOrg scmOrgByName = orgService.createOrGetScmOrgByName(scmByBaseUrl, orgName);
		log.trace("createOrGetScmOrgByName: scmOrgByName:{}", scmOrgByName);
		return scmOrgByName;
	}

	private Scm createOrGetScmByBaseUrl(String baseUrl) {
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

	private Scm createScm(Scm scm) {
		return scmRepository.saveAndFlush(scm);
	}
}