package com.api.garagemint.garagemintapi.repository.cars;

import com.api.garagemint.garagemintapi.dto.cars.ListingFilterRequest;
import com.api.garagemint.garagemintapi.model.cars.Condition;
import com.api.garagemint.garagemintapi.model.cars.Listing;
import com.api.garagemint.garagemintapi.model.cars.ListingStatus;
import com.api.garagemint.garagemintapi.model.cars.ListingType;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ListingQueryRepositoryImpl implements ListingQueryRepository {

  @PersistenceContext
  private EntityManager em;

  @Override
  public Page<Listing> search(ListingFilterRequest f, Pageable pageable) {
    CriteriaBuilder cb = em.getCriteriaBuilder();

    CriteriaQuery<Listing> cq = cb.createQuery(Listing.class);
    Root<Listing> root = cq.from(Listing.class);

    List<Predicate> ps = new ArrayList<>();

    // Moderasyon ve durum varsayılanları
    if (f.getStatus() == null || f.getStatus().isBlank()) {
      ps.add(cb.equal(root.get("status"), ListingStatus.ACTIVE));
    } else {
      ps.add(cb.equal(root.get("status"), ListingStatus.valueOf(f.getStatus().toUpperCase())));
    }
    ps.add(cb.isTrue(root.get("isActive")));

    if (f.getSellerUserId() != null) ps.add(cb.equal(root.get("sellerUserId"), f.getSellerUserId()));
    if (f.getBrandIds() != null && !f.getBrandIds().isEmpty()) ps.add(root.get("brandId").in(f.getBrandIds()));
    if (f.getSeriesIds() != null && !f.getSeriesIds().isEmpty()) ps.add(root.get("seriesId").in(f.getSeriesIds()));
    if (f.getTheme() != null && !f.getTheme().isBlank()) ps.add(cb.equal(cb.lower(root.get("theme")), f.getTheme().toLowerCase()));
    if (f.getScale() != null && !f.getScale().isBlank()) ps.add(cb.equal(root.get("scale"), f.getScale()));
    if (f.getCondition() != null && !f.getCondition().isBlank()) ps.add(cb.equal(root.get("condition"), Condition.valueOf(f.getCondition().toUpperCase())));
    if (f.getLimitedEdition() != null) ps.add(cb.equal(root.get("limitedEdition"), f.getLimitedEdition()));
    if (f.getType() != null && !f.getType().isBlank()) ps.add(cb.equal(root.get("type"), ListingType.valueOf(f.getType().toUpperCase())));
    if (f.getLocation() != null && !f.getLocation().isBlank()) ps.add(cb.like(cb.lower(root.get("location")), "%" + f.getLocation().toLowerCase() + "%"));
    if (f.getYearFrom() != null) ps.add(cb.greaterThanOrEqualTo(root.get("year"), f.getYearFrom()));
    if (f.getYearTo() != null) ps.add(cb.lessThanOrEqualTo(root.get("year"), f.getYearTo()));
    if (f.getPriceMin() != null) ps.add(cb.greaterThanOrEqualTo(root.get("price"), f.getPriceMin()));
    if (f.getPriceMax() != null) ps.add(cb.lessThanOrEqualTo(root.get("price"), f.getPriceMax()));

    cq.where(ps.toArray(Predicate[]::new));

    Path<?> sortPath = switch (f.getSortBy()) {
      case "price" -> root.get("price");
      case "year"  -> root.get("year");
      default -> root.get("createdAt");
    };
    cq.orderBy("ASC".equalsIgnoreCase(f.getSortDir()) ? cb.asc(sortPath) : cb.desc(sortPath));

    TypedQuery<Listing> q = em.createQuery(cq);
    q.setFirstResult((int) pageable.getOffset());
    q.setMaxResults(pageable.getPageSize());
    List<Listing> content = q.getResultList();

    CriteriaQuery<Long> countQ = cb.createQuery(Long.class);
    Root<Listing> countRoot = countQ.from(Listing.class);
    countQ.select(cb.count(countRoot)).where(ps.toArray(Predicate[]::new));
    Long total = em.createQuery(countQ).getSingleResult();

    return new PageImpl<>(content, pageable, total);
  }
}
