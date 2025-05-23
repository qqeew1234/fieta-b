package EtfRecommendService.webSocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Builder
public record StockPriceData(
        String stockCode, //종목코드 0
        double currentPrice, //현재가 2
        String dayOverDaySign, //전일 대비 부호 3
        int dayOverDayChange, // 전일 대비 가격 4
        double dayOverDayRate, //전일대비율(등락률) 5
        long accumulatedVolume) //누적 거래량 13)
{

    //터미널에서 확인용
    @Override
    public String toString() {
        return String.format("종목코드=%s 체결가=%.2f 전일대비부호=%s 전일대비가격=%d, 전일대비율(등락률)=%.2f%% 누적거래량=%d",
                stockCode, currentPrice, dayOverDaySign, dayOverDayChange, dayOverDayRate, accumulatedVolume);
    }
}
