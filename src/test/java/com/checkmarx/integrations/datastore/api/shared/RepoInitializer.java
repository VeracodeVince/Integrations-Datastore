package com.checkmarx.integrations.datastore.api.shared;

import com.checkmarx.integrations.datastore.models.Scm;
import com.checkmarx.integrations.datastore.models.ScmType;
import com.checkmarx.integrations.datastore.repositories.ScmRepository;
import com.checkmarx.integrations.datastore.repositories.ScmTypeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class RepoInitializer {
    private static final String DEFAULT_SCM_TYPE = "dummyScmType";

    private final ScmTypeRepository scmTypeRepo;
    private final ScmRepository scmRepo;
    private final ModelMapper modelMapper;

    public void createScms(List<Map<String, String>> scms) {
        scmTypeRepo.saveAndFlush(ScmType.builder().name(DEFAULT_SCM_TYPE).build());

        List<Scm> scmsToSave = scms.stream()
                .map(scm -> {
                    String effectiveTypeName = StringUtils.defaultString(scm.get("type"), DEFAULT_SCM_TYPE);
                    ScmType type = scmTypeRepo.getByName(effectiveTypeName);
                    Scm result = modelMapper.map(scm, Scm.class);
                    result.setType(type);
                    return result;
                })
                .collect(Collectors.toList());
        scmRepo.saveAll(scmsToSave);
        scmRepo.flush();
    }

    public void createEmptyScms(int count) {
        createScms(IntStream.range(0, count)
                .mapToObj(id -> new HashMap<String, String>())
                .collect(Collectors.toList()));
    }
}
