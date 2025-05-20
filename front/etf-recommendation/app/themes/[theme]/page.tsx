import { Suspense } from 'react';
import Link from 'next/link';
import { notFound } from 'next/navigation';
import { ArrowLeft, ArrowRight } from 'lucide-react';
import { Button } from '@/components/ui/button';
import EtfFilters from './EtfFilters';
import { EtfReturnDto, fetchEtfs } from '@/lib/api/etf';

// 테마 목록 검증
const validThemes = [
  'AI_DATA',
  'USA',
  'KOREA',
  'REITS',
  'MULTI_ASSET',
  'COMMODITIES',
  'HIGH_RISK',
  'SECTOR',
  'DIVIDEND',
  'ESG',
  'GOLD',
  'GOVERNMENT_BOND',
  'CORPORATE_BOND',
  'DEFENSE',
  'SEMICONDUCTOR',
  'BIO',
  'EMERGING_MARKETS',
];

// 테마 이름 변환 함수
function getThemeDisplayName(themeId: string): string {
  const themeMap: Record<string, string> = {
    AI_DATA: 'AI 데이터',
    USA: '미국',
    KOREA: '한국',
    REITS: '리츠',
    MULTI_ASSET: '멀티에셋',
    COMMODITIES: '원자재',
    HIGH_RISK: '고위험',
    SECTOR: '섹터',
    DIVIDEND: '배당',
    ESG: 'ESG',
    GOLD: '금',
    GOVERNMENT_BOND: '국채',
    CORPORATE_BOND: '회사채',
    DEFENSE: '방위산업',
    SEMICONDUCTOR: '반도체',
    BIO: '바이오',
    EMERGING_MARKETS: '신흥시장',
  };

  return themeMap[themeId] || themeId;
}

// ETF 목록 컴포넌트
function EtfTable({ etfs }: { etfs: EtfReturnDto[] }) {
  return (
    <div className="overflow-x-auto rounded-lg border border-gray-200">
      <table className="w-full min-w-full divide-y divide-gray-200">
        <thead className="bg-gray-50">
          <tr>
            <th
              scope="col"
              className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
            >
              ETF 이름
            </th>
            <th
              scope="col"
              className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
            >
              종목코드
            </th>
            <th
              scope="col"
              className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider"
            >
              수익률 (%)
            </th>
          </tr>
        </thead>
        <tbody className="bg-white divide-y divide-gray-200">
          {etfs.length === 0 ? (
            <tr>
              <td
                colSpan={3}
                className="px-6 py-4 text-center text-sm text-gray-500"
              >
                ETF 정보가 없습니다
              </td>
            </tr>
          ) : (
            etfs.map((etf) => (
              <tr key={etf.etfCode} className="hover:bg-gray-50">
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                  {etf.etfName}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {etf.etfCode}
                </td>
                <td
                  className={`px-6 py-4 whitespace-nowrap text-sm text-right font-medium ${
                    etf.returnRate > 0
                      ? 'text-red-600'
                      : etf.returnRate < 0
                        ? 'text-blue-600'
                        : 'text-gray-500'
                  }`}
                >
                  {etf.returnRate > 0 ? '+' : ''}
                  {etf.returnRate.toFixed(2)}
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}

// 페이지네이션 컴포넌트
function Pagination({
  theme,
  currentPage,
  totalPages,
  keyword,
}: {
  theme: string;
  currentPage: number;
  totalPages: number;
  keyword: string;
}) {
  // 페이지 수가 0이면 페이지네이션 표시하지 않음
  if (totalPages <= 1) {
    return null;
  }

  return (
    <div className="flex items-center justify-center mt-6 gap-1">
      <Link
        href={`/themes/${theme}?page=${Math.max(1, currentPage - 1)}&keyword=${encodeURIComponent(keyword)}`}
        aria-disabled={currentPage <= 1}
        tabIndex={currentPage <= 1 ? -1 : undefined}
        className={currentPage <= 1 ? 'pointer-events-none opacity-50' : ''}
      >
        <Button variant="outline" size="sm" disabled={currentPage <= 1}>
          <ArrowLeft className="h-4 w-4 mr-1" /> 이전
        </Button>
      </Link>

      <div className="flex items-center mx-2">
        <span className="text-sm px-3 py-1 rounded-md bg-gray-100">
          {currentPage} / {totalPages}
        </span>
      </div>

      <Link
        href={`/themes/${theme}?page=${Math.min(totalPages, currentPage + 1)}&keyword=${encodeURIComponent(keyword)}`}
        aria-disabled={currentPage >= totalPages}
        tabIndex={currentPage >= totalPages ? -1 : undefined}
        className={
          currentPage >= totalPages ? 'pointer-events-none opacity-50' : ''
        }
      >
        <Button
          variant="outline"
          size="sm"
          disabled={currentPage >= totalPages}
        >
          다음 <ArrowRight className="h-4 w-4 ml-1" />
        </Button>
      </Link>
    </div>
  );
}

// 메인 페이지 컴포넌트 - 테마별 총 갯수 표시 개선
export default async function ThemePage({
  params,
  searchParams,
}: {
  params: { theme: string };
  searchParams: { page?: string; size?: string; keyword?: string };
}) {
  // URL 파라미터 가져오기
  const { theme } = params;
  const page = parseInt(searchParams.page || '1');
  const size = parseInt(searchParams.size || '20');
  const keyword = searchParams.keyword || '';

  // 유효하지 않은 테마인 경우 404 페이지 노출
  if (!validThemes.includes(theme)) {
    notFound();
  }

  // ETF 데이터 가져오기 (기간 파라미터 제거)
  const { data: etfData } = await fetchEtfs({ theme, page, size, keyword });

  if (!etfData) {
    return <div>ETF 데이터가 없습니다</div>;
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold">
          {getThemeDisplayName(theme)} 테마 ETF
        </h1>
        <p className="text-gray-500 mt-2">
          총{' '}
          <span className="font-medium">
            {etfData.totalCount.toLocaleString()}
          </span>
          개의 ETF가 있습니다
        </p>
      </div>

      {/* 필터 컴포넌트 (클라이언트 컴포넌트) - 기간 필터 제거 */}
      <div className="mb-6">
        <Suspense fallback={<div>필터 로딩중...</div>}>
          <EtfFilters theme={theme} initialKeyword={keyword} />
        </Suspense>
      </div>

      {/* ETF 목록 */}
      <EtfTable etfs={etfData.etfReadResponseList} />

      {/* 페이지네이션 - 기간 파라미터 제거 */}
      {etfData.totalPage > 0 && (
        <Pagination
          theme={theme}
          currentPage={etfData.currentPage}
          totalPages={etfData.totalPage}
          keyword={keyword}
        />
      )}
    </div>
  );
}
