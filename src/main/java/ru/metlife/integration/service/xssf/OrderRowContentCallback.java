package ru.metlife.integration.service.xssf;

import static java.lang.String.valueOf;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static ru.metlife.integration.util.CommonUtils.getOrderIndependentHash;
import static ru.metlife.integration.util.CommonUtils.getStringCellValue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import ru.metlife.integration.dto.RecipientDto;
import ru.metlife.integration.service.DeliveryDataService;
import ru.metlife.integration.service.DictionaryService;
import ru.metlife.integration.service.xssf.XlsService.SheetData;

public class OrderRowContentCallback implements ExcelRowContentCollback {

  private DictionaryService dictionaryService;
  private DeliveryDataService deliveryDataService;
  private SheetData dictionarySheetData;

  public OrderRowContentCallback(DictionaryService dictionaryService,
      DeliveryDataService deliveryDataService) {
    this.dictionaryService = dictionaryService;
    this.deliveryDataService = deliveryDataService;
    dictionarySheetData = dictionaryService.processSheet();
  }

  @Override
  public void processRow(int rowNum, Map<String, String> mapData, List<Map<String, String>> data) {
    String polNum = getStringCellValue(mapData, "Номер сертификата");
    String dealership = getStringCellValue(mapData, "Дилерский центр");
    String partner = getStringCellValue(mapData, "Партнер");
    String region = getStringCellValue(mapData, "Region");
    String ppNum = getStringCellValue(mapData, "№ п/п");
    List<RecipientDto> recipients = dictionaryService
        .getRecipientsFromDictionary(dictionarySheetData,
            getOrderIndependentHash(dealership, partner, region));
    if (!recipients.isEmpty()
        && isNotBlank(polNum)
        && !Objects.equals("Совкомбанк", polNum)
        && !deliveryDataService.existsDeliveryDataByPpNum(ppNum)
    ) {
      mapData.put("rowNum", valueOf(rowNum));
      data.add(new LinkedHashMap<>(mapData));
    }
  }
}
