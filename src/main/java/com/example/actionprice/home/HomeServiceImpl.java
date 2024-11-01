package com.example.actionprice.home;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class HomeServiceImpl implements HomeService {

  // 이미지들을 static에 다 저장하고 경로를 입력해야 함
  private final String[] imagePaths = {
      "static/image/cookie.jpg",
      "static/image/egg.jpg",
      "static/image/meat.jpg",
      "static/image/veggie.jpg",
      "static/image/salted.jpg",
  };

  @Override
  public Map<String, String> fetchImages() throws IOException {
    Map<String, String> images = new HashMap<>();

    for (String imagePath : imagePaths) {
      log.info("image path: " + imagePath);
      ClassPathResource image = new ClassPathResource(imagePath);
      byte[] imageBytes = Files.readAllBytes(image.getFile().toPath());
      String base64Image = java.util.Base64.getEncoder().encodeToString(imageBytes);

      // 마지막 슬래시("/") 뒤부터 맨 뒤의 점(".") 까지
      String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
      imageName = imageName.substring(0, imageName.lastIndexOf("."));

      images.put(imageName, base64Image);
    }

    return images;
  }
}
