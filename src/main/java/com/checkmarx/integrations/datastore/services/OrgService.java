package com.checkmarx.integrations.datastore.services;

import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.models.ScmOrg;
import com.checkmarx.integrations.datastore.repositories.ScmOrgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrgService {

    private final ScmOrgRepository scmOrgRepository;

    public ScmOrg getOrgBy(String scmName, String orgName) {
        return scmOrgRepository.getScmOrg(orgName, scmName);
    }

    public ScmOrg createScmOrg(ScmOrg scmOrg) {
        return scmOrgRepository.saveAndFlush(scmOrg);
    }

    public void deleteScmOrgById(Long id) {
        scmOrgRepository.deleteById(id);
    }

    public ScmOrg createOrGetScmOrgByName(Scm scm, String orgNam) {
        ScmOrg orgByName = getOrgBy(scm.getName(), orgNam);

        if (orgByName != null) {
            return orgByName;
        } else {
            ScmOrg scmOrg = ScmOrg.builder()
                    .name(orgNam)
                    .scm(scm)
                    .build();
            return createScmOrg(scmOrg);
        }
    }
}