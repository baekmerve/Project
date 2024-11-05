package com.example.actionprice.AuctionData.service;

import com.example.actionprice.AuctionData.entity.AuctionBaseEntity;
import com.example.actionprice.AuctionData.originAuctionData.originApiRequestObj.OriginAuctionDataRow;

public interface AuctionEntityService {
  AuctionBaseEntity saveEntityByCategory(OriginAuctionDataRow row, String date, String marketName, String category);
}
