package com.api.garagemint.garagemintapi.mapper.auction;

import com.api.garagemint.garagemintapi.dto.auction.*;
import com.api.garagemint.garagemintapi.model.auction.*;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuctionMapper {

  AuctionResponseDto toDto(Auction a);

  @Mapping(target = "auctionId", source = "auction.id")
  BidResponseDto toDto(AuctionBid b);

  AuctionImageDto toDto(AuctionImage img);
  List<AuctionImageDto> toImageDtoList(List<AuctionImage> imgs);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateAuctionFromCreate(AuctionCreateRequest req, @MappingTarget Auction a);
}
