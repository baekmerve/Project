package com.example.actionprice.home;

import java.io.IOException;
import java.util.Map;

/**
 * @author 연상훈
 * @created 2024-11-08 오전 11:05
 */
public interface HomeService {
  Map<String, String> fetchImages() throws IOException;
}
