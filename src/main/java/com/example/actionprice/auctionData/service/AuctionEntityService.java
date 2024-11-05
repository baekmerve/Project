package com.example.actionprice.auctionData.service;

import com.example.actionprice.auctionData.entity.AuctionBaseEntity;
import com.example.actionprice.auctionData.originAuctionData.originApiRequestObj.OriginAuctionDataRow;

public interface AuctionEntityService {
  AuctionBaseEntity saveEntityByCategory(OriginAuctionDataRow row, String date, String marketName, String category);
}
