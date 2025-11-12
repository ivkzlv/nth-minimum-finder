package org.nth.minimum.finder.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.nth.minimum.finder.dto.FindRequest;
import org.nth.minimum.finder.dto.FindResponse;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NthMinimumFinderService {

  public FindResponse findNumber(FindRequest request) throws IOException {
    String filePath = request.getFilePath();
    int n = request.getN();

    File file = new File(filePath);
    if (!file.exists() || !file.isFile()) {
      throw new IllegalArgumentException("Файл не существует: " + filePath);
    }

    List<Integer> numbers = readNumbersFromXlsxFile(filePath);
    if (n > numbers.size()) {
      throw new IllegalArgumentException("Запрошенный N (" + n + ") больше количества чисел в файле (" + numbers.size() + ")");
    }

    int[] nums = numbers.stream()
        .mapToInt(Integer::intValue)
        .toArray();

    return new FindResponse(
        request.getN(),
        request.getFilePath(),
        selectNumber(nums, 0, nums.length - 1, n - 1)
    );
  }

  private List<Integer> readNumbersFromXlsxFile(String filePath) throws IOException {
    List<Integer> numbers = new ArrayList<>();

    try (FileInputStream inputStream = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(inputStream)) {

      Sheet sheet = workbook.getSheetAt(0);
      for (Row row : sheet) {
        Cell cell = row.getCell(0);
        if (cell != null) {
          try {
            if (cell.getCellType() == CellType.NUMERIC) {
              numbers.add((int) cell.getNumericCellValue());
            } else if (cell.getCellType() == CellType.STRING) {
              numbers.add(Integer.parseInt(cell.getStringCellValue()));
            }
          } catch (NumberFormatException e) {
            // do nothing
          }
        }
      }
    }

    return numbers;
  }

  private static int selectNumber(int[] numbers, int left, int right, int k) {
    if (left == right) {
      return numbers[left];
    }

    int pivotIndex = 0;
    pivotIndex = partition(numbers, left, right, pivotIndex);

    if (k == pivotIndex) {
      return numbers[k];
    } else if (k < pivotIndex) {
      return selectNumber(numbers, left, pivotIndex - 1, k);
    } else {
      return selectNumber(numbers, pivotIndex + 1, right, k);
    }
  }

  private static int partition(int[] numbers, int left, int right, int pivotIndex) {
    int pivot = numbers[pivotIndex];

    swap(numbers, pivotIndex, right);

    int storeIndex = left;
    for (int i = left; i < right; i++) {
      if (numbers[i] <= pivot) {
        swap(numbers, storeIndex, i);
        storeIndex++;
      }
    }

    swap(numbers, storeIndex, right);
    return storeIndex;
  }

  private static void swap(int[] arr, int i, int j) {
    int temp = arr[i];
    arr[i] = arr[j];
    arr[j] = temp;
  }
}
