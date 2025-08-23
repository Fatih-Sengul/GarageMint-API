package com.api.garagemint.garagemintapi.repository.projection;

/** Projection örneği (opsiyonel kullanım) */
public interface PublicLinkView {
    String getType();
    String getLabel();
    String getUrl();
    Integer getIdx();
}
