package com.api.garagemint.garagemintapi.mapper.cars;

import com.api.garagemint.garagemintapi.dto.cars.*;
import com.api.garagemint.garagemintapi.model.cars.*;
import org.mapstruct.*;

import java.time.Instant;
import java.util.List;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    imports = {ListingType.class, Condition.class, ListingStatus.class}
)
public interface ListingMapper {

  /* ========= Entity -> Response ========= */

  // Core listing alanlarını DTO'ya taşır (seller/images/tags/brandName/seriesName service'te set edilecek)
  @Mapping(target = "seller", ignore = true)
  @Mapping(target = "images", ignore = true)
  @Mapping(target = "tags", ignore = true)
  @Mapping(target = "brandName", ignore = true)
  @Mapping(target = "seriesName", ignore = true)

  // enum → String
  @Mapping(target = "type", expression = "java(entity.getType()==null?null:entity.getType().name())")
  @Mapping(target = "status", expression = "java(entity.getStatus()==null?null:entity.getStatus().name())")
  @Mapping(target = "condition", expression = "java(entity.getCondition()==null?null:entity.getCondition().name())")

  // Instant → ISO-8601 String
  @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "iso")
  @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "iso")
  ListingResponseDto toResponseDto(Listing entity);

  /* ========= Create DTO -> Entity ========= */

  // Create sırasında sellerUserId, status, isActive service’te set edilecek.
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "sellerUserId", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "isActive", ignore = true)
  // enum-like String → Enum (null toleranslı)
  @Mapping(target = "type", expression = "java(req.getType()==null?null:ListingType.valueOf(req.getType().toUpperCase()))")
  @Mapping(target = "condition", expression = "java(req.getCondition()==null?null:Condition.valueOf(req.getCondition().toUpperCase()))")
  @Mapping(target = "price", source = "price")
  @Mapping(target = "brandId", source = "brandId")
  @Mapping(target = "seriesId", source = "seriesId")
  Listing toEntity(ListingCreateRequest req);

  /* ========= Update DTO -> mevcut Entity ========= */

  // Sadece gelen alanları günceller (null’lar dokunmaz); enum-stringleri parse ederiz.
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "type", expression = "java(req.getType()==null?entity.getType():ListingType.valueOf(req.getType().toUpperCase()))")
  @Mapping(target = "condition", expression = "java(req.getCondition()==null?entity.getCondition():Condition.valueOf(req.getCondition().toUpperCase()))")
  @Mapping(target = "status", expression = "java(req.getStatus()==null?entity.getStatus():ListingStatus.valueOf(req.getStatus().toUpperCase()))")
  void updateEntity(@MappingTarget Listing entity, ListingUpdateRequest req);

  /* ========= Basit yardımcı map’ler ========= */

  ListingImageDto toDto(ListingImage image);
  List<ListingImageDto> toImageDtoList(List<ListingImage> images);

  TagDto toDto(Tag tag);
  List<TagDto> toTagDtoList(List<Tag> tags);

  @Named("iso")
  default String iso(Instant i) { return i == null ? null : i.toString(); }
}

