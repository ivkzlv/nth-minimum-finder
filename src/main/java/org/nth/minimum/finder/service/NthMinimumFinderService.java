package org.nth.minimum.finder.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.nth.minimum.finder.dto.FindRequest;
import org.nth.minimum.finder.dto.FindResponse;
import org.springframework.stereotype.Service;

@Service
public class NthMinimumFinderService {

  private static final String ALLOWED_FILE_EXTENSION = "xlsx";
  private static final int SHEET_INDEX = 0;
  private static final int CELL_INDEX = 0;

  public FindResponse findNumber(FindRequest request) throws IOException {
    String filePath = request.getFilePath();
    int n = request.getN();

    File file = new File(filePath);
    if (!file.exists() || !file.isFile()) {
      throw new IllegalArgumentException("Файл не существует: " + filePath);
    }

    // Первый плохой момент для последующей обработки
    if (!FilenameUtils.getExtension(filePath).equals(ALLOWED_FILE_EXTENSION)) {
      throw new IllegalArgumentException("Можно обрабатывать только .xlsx файлы: " + filePath);
    }

    // Лучше сразу при получении числа пытаться отсортировать их
    // В таком случае сложность может быть O(n * log(n))
    // При использовании quick select алгоритма в худшем случае сложность будет O(n^2)
    List<Integer> sortedNumbers = readNumbersAndSort(filePath);
    if (n > sortedNumbers.size()) {
      throw new IllegalArgumentException("Запрошенный N (" + n + ") больше количества уникальных чисел в файле (" + sortedNumbers.size() + ")");
    }

    return new FindResponse(request.getN(), request.getFilePath(),
        sortedNumbers.get(n - 1));
  }

  private List<Integer> readNumbersAndSort(String filePath) throws IOException {
    List<Integer> sortedNumbers = new ArrayList<>();

    Set<Integer> uniqueNumbers = new HashSet<>();
    try (FileInputStream inputStream = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(inputStream)) {

      // Второй плохой момент, если файл не содержит столбцов
      if (workbook.getNumberOfSheets() <= 0) {
        throw new IllegalArgumentException("Файл .xlsx не содержит столбцов: " + filePath);
      }

      Sheet sheet = workbook.getSheetAt(SHEET_INDEX);
      for (Row row : sheet) {
        Cell cell = row.getCell(CELL_INDEX);
        if (cell != null) {
          // Можно ограничиться только NUMERIC типом
          if (cell.getCellType() == CellType.NUMERIC) {
            int number = (int) cell.getNumericCellValue();

            if (uniqueNumbers.add(number)) {
              int position = searchPositionToAdd(sortedNumbers, number);
              sortedNumbers.add(position, number);
            }
          }
        }
      }
    }

    return sortedNumbers;
  }

  private int searchPositionToAdd(List<Integer> numbers, int target) {
    int left = 0;
    int right = numbers.size() - 1;

    while (left <= right) {
      int middle = (left + right) >>> 1;

      int middleValue = numbers.get(middle);
      if (middleValue < target) {
        left = middle + 1;
      } else if (middleValue > target) {
        right = middle - 1;
      } else {
        return middle;
      }
    }

    return left;
  }
}
