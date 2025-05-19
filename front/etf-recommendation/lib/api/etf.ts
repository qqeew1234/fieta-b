import { FetchResult, get } from '../http/client';

export interface EtfResponse {
  totalPage: number;
  totalCount: number;
  currentPage: number;
  pageSize: number;
  etfReadResponseList: EtfReturnDto[];
}

interface EtfReturnDto {
  etfId: number;
  etfName: string;
  etfCode: string;
  theme: string;
  returnRate: number;
}

export interface EtfDetailResponse {
  etfId: number;
  etfName: string;
  etfCode: string;
  companyName: string;
  listingDate: string;
}

/**
 * 모든 ETF 데이터를 가져옵니다.
 */
export async function fetchAllEtfs(
  period: string = 'weekly'
): Promise<FetchResult<EtfResponse>> {
  return get('/api/v1/etfs', {
    params: { page: 1, size: 10000, period },
    errorMessage: 'ETF 데이터를 불러오는 데 실패했습니다',
  });
}

/**
 * 페이지네이션된 ETF 데이터를 가져옵니다.
 */
export async function fetchEtfsPage(
  page: number = 1,
  size: number = 20,
  period: string = 'weekly'
): Promise<FetchResult<EtfResponse>> {
  return get('/api/v1/etfs', {
    params: { page, size, period },
    errorMessage: 'ETF 페이지 데이터를 불러오는 데 실패했습니다',
  });
}

/**
 * 단일 ETF 상세 정보를 가져옵니다.
 */
export async function fetchEtfDetail(
  etfId: number
): Promise<FetchResult<EtfDetailResponse>> {
  return get(`/api/v1/etfs/${etfId}`, {
    errorMessage: 'ETF 상세 정보를 불러오는 데 실패했습니다',
  });
}
