package org.nth.minimum.finder.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindRequest {

  @NotBlank(message = "Путь к файлу не может быть пустым")
  private String filePath;

  @Min(value = 1, message = "N должно быть больше или равно 1")
  private int n;
}
