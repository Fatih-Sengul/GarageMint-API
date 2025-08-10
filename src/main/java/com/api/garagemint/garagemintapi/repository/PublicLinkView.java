package com.api.garagemint.garagemintapi.repository;

import java.util.List;

public interface PublicLinkView {
    String getType();
    String getLabel();
    String getUrl();
    Integer getIdx();
    List<PublicLinkView> findByProfileIdAndIsPublicTrueOrderByIdxAsc(Long profileId);

}