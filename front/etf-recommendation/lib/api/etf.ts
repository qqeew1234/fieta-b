import {FetchResult, httpGet} from '../http/client';

export interface EtfResponse {
    totalPage: number;
    totalCount: number;
    currentPage: number;
    pageSize: number;
    etfReadResponseList: EtfReturnDto[];
}

export interface EtfReturnDto {
    etfId: number;
    etfName: string;
    etfCode: string;
    theme: string;
    returnRate: number;
}

export interface EtfAllResponse {
    totalCount: number;
    etfReadResponseList: EtfReturnDto[];
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
export async function fetchEtfs(options?: {
    page?: number;
    size?: number;
    period?: string;
    theme?: string;
    keyword?: string;
}): Promise<FetchResult<EtfResponse>> {
    const {
        page = 1,
        size = 20,
        period = 'weekly',
        theme,
        keyword,
    } = options || {};

    return httpGet('/api/v1/etfs', {
        params: {
            page,
            size,
            period,
            theme,
            keyword: keyword && encodeURIComponent(keyword),
        },
        errorMessage: 'ETF 데이터를 불러오는 데 실패했습니다',
    });
}


//페이징없는 etf 검색
export async function fetchAllEtfs(options?: {
    theme?: string;
    keyword?: string;
}): Promise<FetchResult<EtfAllResponse>> {
    const {
        theme,
        keyword,
    } = options || {};

    return httpGet('/api/v1/etfs/search', {
        params: {
            theme,
            keyword: keyword && encodeURIComponent(keyword),
        },
        errorMessage: 'ETF 검색에 실패했습니다.',
    });
}

/**
 * 단일 ETF 상세 정보를 가져옵니다.
 */
export async function fetchEtfDetail(
    etfId: number
): Promise<FetchResult<EtfDetailResponse>> {
    return httpGet(`/api/v1/etfs/${etfId}`, {
        errorMessage: 'ETF 상세 정보를 불러오는 데 실패했습니다',
    });
}

