package org.nth.minim.finder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.nth.minimum.finder.NthMinimumFinderApp;
import org.nth.minimum.finder.dto.FindRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest(
    classes = NthMinimumFinderApp.class,
    webEnvironment = WebEnvironment.MOCK
)
class NthMinControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  private ObjectMapper objectMapper = new ObjectMapper();

  private File testFile;

  @BeforeEach
  void setUp(@TempDir Path tempDir) throws Exception {
    testFile = createTestXlsxFile(tempDir, Arrays.asList(5, 3, 8, 1, 9, 2));
  }

  @Test
  void shouldFindNthMinimumSuccessfully() throws Exception {
    FindRequest request = new FindRequest();
    request.setFilePath(testFile.getAbsolutePath());
    request.setN(3);

    mockMvc.perform(post("/api/v1/find-min-nth-number")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nthMin").value(3))
        .andExpect(jsonPath("$.filePath").value(testFile.getAbsolutePath()))
        .andExpect(jsonPath("$.n").value(3));
  }

  @Test
  void shouldReturnBadRequestWhenFileDoesNotExist() throws Exception {
    FindRequest request = new FindRequest();
    request.setFilePath("/invalid/path/to/file.xlsx");
    request.setN(1);

    mockMvc.perform(post("/api/v1/find-min-nth-number")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenNIsTooLarge() throws Exception {
    FindRequest request = new FindRequest();
    request.setFilePath(testFile.getAbsolutePath());
    request.setN(10);

    mockMvc.perform(post("/api/v1/find-min-nth-number")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenNIsLessThan1() throws Exception {
    FindRequest request = new FindRequest();
    request.setFilePath(testFile.getAbsolutePath());
    request.setN(0);

    mockMvc.perform(post("/api/v1/find-min-nth-number")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenFilePathIsEmpty() throws Exception {
    FindRequest request = new FindRequest();
    request.setFilePath(testFile.getAbsolutePath());
    request.setN(0);

    mockMvc.perform(post("/api/v1/find-min-nth-number")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  private File createTestXlsxFile(Path tempDir, List<Integer> numbers) throws Exception {
    File tempFile = tempDir.resolve("test_numbers.xlsx").toFile();

    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet("Numbers");

      for (int i = 0; i < numbers.size(); i++) {
        Row row = sheet.createRow(i);
        Cell cell = row.createCell(0);
        cell.setCellValue(numbers.get(i));
      }

      try (FileOutputStream fos = new FileOutputStream(tempFile)) {
        workbook.write(fos);
      }
    }

    return tempFile;
  }
}