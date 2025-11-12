package org.nth.minimum.finder.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FindResponse {

  private int nthMin;
  private String filePath;
  private int n;
}
