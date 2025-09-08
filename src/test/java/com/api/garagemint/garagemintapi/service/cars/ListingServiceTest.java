package com.api.garagemint.garagemintapi.service.cars;

import com.api.garagemint.garagemintapi.dto.cars.ListingCreateRequest;
import com.api.garagemint.garagemintapi.dto.cars.ListingResponseDto;
import com.api.garagemint.garagemintapi.mapper.cars.ListingMapper;
import com.api.garagemint.garagemintapi.model.cars.*;
import com.api.garagemint.garagemintapi.repository.cars.*;
import com.api.garagemint.garagemintapi.repository.profiles.ProfileRepository;
import com.api.garagemint.garagemintapi.service.exception.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ListingServiceTest {

    private ListingRepository listingRepo;
    private ListingImageRepository imageRepo;
    private ListingTagRepository listingTagRepo;
    private TagRepository tagRepo;
    private BrandRepository brandRepo;
    private SeriesRepository seriesRepo;
    private ProfileRepository profileRepo;
    private ListingMapper mapper;

    private ListingService service;

    @BeforeEach
    void setUp() {
        listingRepo = mock(ListingRepository.class);
        imageRepo = mock(ListingImageRepository.class);
        listingTagRepo = mock(ListingTagRepository.class);
        tagRepo = mock(TagRepository.class);
        brandRepo = mock(BrandRepository.class);
        seriesRepo = mock(SeriesRepository.class);
        profileRepo = mock(ProfileRepository.class);
        mapper = mock(ListingMapper.class);

        service = Mockito.spy(new ListingService(
                listingRepo, imageRepo, listingTagRepo, tagRepo, brandRepo, seriesRepo, profileRepo, mapper));
    }

    @Test
    void createSuccess() {
        ListingCreateRequest req = ListingCreateRequest.builder()
                .title("My Listing")
                .type(ListingType.SALE)
                .price(BigDecimal.TEN)
                .currency("USD")
                .build();

        Listing entity = Listing.builder()
                .type(ListingType.SALE)
                .price(BigDecimal.TEN)
                .currency("USD")
                .build();

        Listing saved = Listing.builder()
                .id(1L)
                .type(ListingType.SALE)
                .price(BigDecimal.TEN)
                .currency("USD")
                .build();

        ListingResponseDto resp = ListingResponseDto.builder().id(1L).build();

        when(listingRepo.countBySellerUserIdAndStatus(anyLong(), eq(ListingStatus.ACTIVE))).thenReturn(0L);
        when(mapper.toEntity(req)).thenReturn(entity);
        when(listingRepo.save(entity)).thenReturn(saved);
        doReturn(resp).when(service).assembleResponse(1L);
        doNothing().when(imageRepo).deleteByListingId(anyLong());
        doNothing().when(listingTagRepo).deleteByIdListingId(anyLong());

        ListingResponseDto out = service.create(5L, req);

        assertNotNull(out);
        assertEquals(1L, out.getId());
        verify(listingRepo).save(entity);
    }

    @Test
    void createFailsWhenMaxActiveListingsReached() {
        ListingCreateRequest req = ListingCreateRequest.builder()
                .title("My Listing")
                .type(ListingType.SALE)
                .price(BigDecimal.TEN)
                .currency("USD")
                .build();

        when(listingRepo.countBySellerUserIdAndStatus(anyLong(), eq(ListingStatus.ACTIVE))).thenReturn(3L);

        assertThrows(BusinessRuleException.class, () -> service.create(10L, req));
        verify(listingRepo, never()).save(any());
    }
}
