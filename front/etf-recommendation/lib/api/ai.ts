import { FetchResult, httpPost } from '@/lib/http/client';

interface userAnswer {
  question: string;
  answer: string;
}

interface EtfDetail {
  etfId: number;
  etfName: string | null;
  weeklyReturn: number | null;
}

interface Recommendation {
  mainRecommendation: string;
  subRecommendations: string[];
  reason: string;
}

export interface ApiResponse {
  status: string;
  recommendation: Recommendation;
  etfs: EtfDetail[];
}

export async function aiRecommend(
  userAnswerList: userAnswer[]
): Promise<FetchResult<ApiResponse>> {
  return httpPost('/api/v1/recommendation', userAnswerList, {
    errorMessage: '추천 전송에 실패했습니다.',
  });
}
