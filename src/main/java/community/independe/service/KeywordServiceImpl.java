package community.independe.service;

import community.independe.domain.keyword.Keyword;
import community.independe.domain.keyword.KeywordDto;
import community.independe.repository.keyword.KeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class KeywordServiceImpl implements KeywordService{

    private final KeywordRepository keywordRepository;

    @Override
    public List<KeywordDto> findKeywordsByGroup() {
        return keywordRepository.findKeywordsByGroup();
    }

    @Override
    @Transactional
    public Long saveKeywordWithCondition(String condition, String keyword) {

        Keyword createKeyword = Keyword.builder()
                .condition(condition)
                .keyword(keyword)
                .build();
        Keyword savedKeyword = keywordRepository.save(createKeyword);

        return savedKeyword.getId();
    }
}
