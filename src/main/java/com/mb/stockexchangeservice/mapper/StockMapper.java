package com.mb.stockexchangeservice.mapper;

import com.mb.stockexchangeservice.api.request.ApiStockRequest;
import com.mb.stockexchangeservice.api.response.ApiStockResponse;
import com.mb.stockexchangeservice.data.entity.Stock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StockMapper {

    ApiStockResponse map(Stock stock);

    List<ApiStockResponse> map(List<Stock> stocks);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdDateTime", ignore = true)
    @Mapping(target = "modifiedDateTime", ignore = true)
    @Mapping(target = "stockExchanges", ignore = true)
    Stock map(ApiStockRequest apiStockRequest);

    default Page<ApiStockResponse> map(Page<Stock> orders) {
        return orders.map(this::map);
    }
}
