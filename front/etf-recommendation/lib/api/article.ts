import { FetchResult, get } from '../http/client';

export interface ArticleResponse {
  id: number;
  title: string;
  sourceUrl: string;
  thumbnailUrl: string;
  publishedAt: string;
}

export async function fetchEconomicArticles(): Promise<
  FetchResult<ArticleResponse[]>
> {
  return get('/api/v1/articles', {
    errorMessage: '경제 기사를 불러오는 데 실패했습니다',
  });
}
