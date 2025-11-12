package org.nth.minimum.finder.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.nth.minimum.finder.dto.FindRequest;
import org.nth.minimum.finder.dto.FindResponse;
import org.nth.minimum.finder.exception.Error;
import org.nth.minimum.finder.service.NthMinimumFinderService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Nth Minimum Finder", description = "API для поиска N-ного минимального числа в XLSX файле")
public class NthMinimumFinderController {

  private final NthMinimumFinderService nthMinimumFinderService;

  @PostMapping("/find-min-nth-number")
  @Operation(
      summary = "Найти N-ное минимальное число",
      description = "Возвращает N-ное минимальное число из XLSX файла. Файл должен содержать целые числа в первом столбце.",
      responses = {
          @ApiResponse(responseCode = "200", description = "Успешный поиск",
              content = @Content(schema = @Schema(implementation = FindResponse.class))),
          @ApiResponse(responseCode = "400", description = "Некорректный запрос (неверный путь к файлу или некорректный N)",
              content = @Content(schema = @Schema(implementation = Error.class))),
          @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
              content = @Content(schema = @Schema(implementation = Error.class)))
      }
  )
  public FindResponse findMinNth(@Valid @RequestBody FindRequest request) throws IOException {
    return nthMinimumFinderService.findNumber(request);
  }
}
