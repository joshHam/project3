package org.zerock.natureRent.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.natureRent.dto.ProductDTO;
import org.zerock.natureRent.dto.PageRequestDTO;
import org.zerock.natureRent.dto.PageResultDTO;
import org.zerock.natureRent.entity.Product;
import org.zerock.natureRent.entity.ProductImage;
import org.zerock.natureRent.repository.ProductImageRepository;
import org.zerock.natureRent.repository.ProductRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Log4j2
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository; //final

    private final ProductImageRepository imageRepository; //final

    @Transactional
    @Override
    public Long register(ProductDTO productDTO) {

        Map<String, Object> entityMap = dtoToEntity(productDTO);
        Product product = (Product) entityMap.get("product");
        List<ProductImage> productImageList = (List<ProductImage>) entityMap.get("imgList");

        productRepository.save(product);

        productImageList.forEach(productImage -> {
            imageRepository.save(productImage);
        });

        return product.getMno();
    }

    @Override
    public PageResultDTO<ProductDTO, Object[]> getList(PageRequestDTO requestDTO) {

        Pageable pageable = requestDTO.getPageable(Sort.by("mno").descending());

        Page<Object[]> result = productRepository.getListPage(pageable);

        log.info("==============================================");
        result.getContent().forEach(arr -> {
            log.info(Arrays.toString(arr));
        });


        Function<Object[], ProductDTO> fn = (arr -> entitiesToDTO(
                (Product)arr[0] ,
                (List<ProductImage>)(Arrays.asList((ProductImage)arr[1])),
                (Double) arr[2],
                (Long)arr[3])
        );

        return new PageResultDTO<>(result, fn);
    }

    @Override
    public ProductDTO getProduct(Long mno) {

        List<Object[]> result = productRepository.getProductWithAll(mno);

        Product product = (Product) result.get(0)[0];

        List<ProductImage> productImageList = new ArrayList<>();

        result.forEach(arr -> {
            ProductImage  productImage = (ProductImage)arr[1];
            productImageList.add(productImage);
        });

        Double avg = (Double) result.get(0)[2];
        Long reviewCnt = (Long) result.get(0)[3];

        return entitiesToDTO(product, productImageList, avg, reviewCnt);
    }

    @Override
    public List<LocalDateTime> getRentedDatesByProductId(Long mno) {
        log.info("Fetching rented dates for product: " + mno);

//        List<Object[]> rentalPeriods = productRepository.findRentalPeriodsByProductId(mno);

//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        log.info("Fetched rental periods: " + rentalPeriods);
        // 기간별 날짜 리스트 생성
        List<LocalDateTime> rentedDates = rentalPeriods.stream()
                .flatMap(period -> Stream.of((LocalDateTime) period[0], (LocalDateTime) period[1]))
                .collect(Collectors.toList());

        log.info("Converted rented dates: " + rentedDates);

//        List<String> rentedDates = rentalPeriods.stream()
//                .flatMap(period -> {
//                    LocalDateTime start = (LocalDateTime) period[0];
//                    LocalDateTime end = (LocalDateTime) period[1];
//                    return start.toLocalDate().datesUntil(end.toLocalDate().plusDays(1)).map(date -> date.format(formatter));
//                })
//                .collect(Collectors.toList());


//        List<LocalDateTime> rentedDates = new ArrayList<>();
//        for (Object[] period : rentalPeriods) {
//            LocalDateTime start = (LocalDateTime) period[0];
//            LocalDateTime end = (LocalDateTime) period[1];
//
//            // start부터 end까지의 날짜를 모두 리스트에 추가
//            while (!start.isAfter(end)) {
//                rentedDates.add(start);
//                start = start.plusDays(1); // 하루씩 추가
//            }
//        }

//        return rentedDates;
        return rentalRepository.findRentedDatesByProductId(productId);
    }

}


















