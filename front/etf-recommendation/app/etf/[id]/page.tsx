'use client'

import {useParams, useRouter} from 'next/navigation'
import {useEffect, useState, useTransition} from 'react'
import { notFound } from 'next/navigation'
import { ArrowLeft, Star, StarHalf } from 'lucide-react'
import Link from 'next/link'
import TradingViewWidget from "@/components/tradingViewWidget";

import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import {
  Tabs,
  TabsContent,
  TabsList,
  TabsTrigger,
} from '@/components/ui/tabs'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import {getSubscribedEtfIds, subscribeToEtf, unsubscribeFromEtf} from "@/app/etf/[id]/action";


type Holding = {
  name: string
  weight: number
}

type ETF = {
  id: number
  name: string
  ticker: string
  issuer: string
  theme: string
  description: string
  price: number
  nav: number
  change: number
  returnRate: number
  volume: number
  launchDate: string
  aum: number
  expense: number
  holdings: Holding[]
  chartData: {
    daily: any[]
    weekly: any[]
    monthly: any[]
    yearly: any[]
  }
}

export default function ETFDetailPage() {
  const params = useParams()
  const etfId = params.id// URL에서 id 값을 가져옴
  const [etf, setEtf] = useState<ETF | null>(null)
  const router = useRouter()
  const [subscribed, setSubscribed] = useState(false) // 구독 상태
  const [loading, setLoading] = useState(false)
  const [isPending, startTransition] = useTransition()

  useEffect(() => {
    if (!etfId) return

    fetch(`https://localhost:8443/api/v1/etfs/${etfId}`)
        .then(res => {
          if (!res.ok) throw new Error('ETF not found')
          return res.json()
        })
        .then(data => {
          setEtf({
            id: data.etfId,
            name: data.etfName,
            ticker: data.etfCode,
            issuer: data.companyName,
            theme: '',  // theme 데이터가 없으므로 비워둡니다.
            description: '', // description 데이터가 없으므로 비워둡니다.
            price: 0, // 가격 데이터가 없으므로 0으로 설정
            nav: 0, // NAV 데이터가 없으므로 0으로 설정
            change: 0, // 변화율 데이터가 없으므로 0으로 설정
            returnRate: 0, // 수익률 데이터가 없으므로 0으로 설정
            volume: 0, // 거래량 데이터가 없으므로 0으로 설정
            launchDate: data.listingDate,
            aum: 0, // 자산 규모가 없으므로 0으로 설정
            expense: 0, // 보수율이 없으므로 0으로 설정
            holdings: [], // 구성 종목 데이터가 없으므로 빈 배열로 설정
            chartData: {
              daily: [],
              weekly: [],
              monthly: [],
              yearly: [],
            },
          })
        })
        .catch(() => notFound()) // 오류 발생 시 404 페이지로 이동
  },[etfId]);

  useEffect(() => {
    if (!etfId) return;

    const checkSubscription = async () => {
      const ids = await getSubscribedEtfIds();
      setSubscribed(ids.includes(Number(etfId)));
    };

    checkSubscription();
  }, [etfId]);

  const handleToggleSubscribe = async () => {
    setLoading(true)
    try {
      if (subscribed) {
        await unsubscribeFromEtf(etfId) // 구독 취소
        setSubscribed(false)
      } else {
        await subscribeToEtf(etfId) // 구독
        setSubscribed(true)
      }
    } catch (err: any) {
      alert(err.message)
    } finally {
      setLoading(false)
    }
  }

  if (!etf) return <div className="p-6">ETF 데이터를 불러오는 중입니다...</div>

  const tradingViewSymbol = `KRX:${etf.ticker.toUpperCase()}`
  const relatedEtfs: ETF[] = [] // 필요시 관련 ETF 데이터 로직 추가

  return (
      <div className="container mx-auto py-6 px-4">
        <div className="mb-6">
          <Link href="/" className="flex items-center gap-1 text-slate-500 hover:text-slate-700 mb-4">
            <ArrowLeft className="h-4 w-4" />
            <span>홈으로 돌아가기</span>
          </Link>

          <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
            <div>
              <div className="flex items-center gap-2">
                <h1 className="text-3xl font-bold">{etf.name}</h1>
                <Badge variant="outline">{etf.theme}</Badge>
              </div>
              <p className="text-slate-500">
                {etf.ticker} | {etf.issuer}
              </p>
            </div>

            <div className="flex items-center gap-2">
              <Button
                  variant={subscribed ? 'secondary' : 'outline'}
                  size="icon"
                  onClick={handleToggleSubscribe}
                  disabled={loading}
              >
                {subscribed ? (
                    <Star className="h-4 w-4 text-yellow-500" />
                ) : (
                    <Star className="h-4 w-4" />
                )}
              </Button>
            </div>
          </div>
        </div>

        <div className="grid md:grid-cols-3 gap-6 mb-8">
          <Card>
            <CardHeader className="pb-2">
              <CardTitle>현재가</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex items-end gap-2">
                <div className="text-3xl font-bold">{etf.price ? etf.price.toLocaleString() : 'Loading...'}원</div>
                <div className={`text-lg ${etf.change >= 0 ? "text-green-600" : "text-red-600"}`}>
                  {etf.change >= 0 ? "+" : ""}
                  {etf.change}%
                </div>
              </div>
              {/*<p className="text-sm text-slate-500 mt-1">NAV: {etf.nav ? etf.nav.toLocaleString() : 'Loading...'}원</p>*/}
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="pb-2">
              <CardTitle>등락률</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold text-green-600">+{etf.returnRate}%</div>
              {/*<p className="text-sm text-slate-500 mt-1">오늘 기준</p>*/}
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="pb-2">
              <CardTitle>거래량</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold"> {etf.volume ? etf.volume.toLocaleString() : 'Loading...'}</div>
              {/*<p className="text-sm text-slate-500 mt-1">오늘 기준</p>*/}
            </CardContent>
          </Card>
        </div>

        <div className="mb-8">
          <Tabs defaultValue="daily">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-2xl font-bold">가격 차트</h2>
              <TabsList>
                <TabsTrigger value="daily">일간</TabsTrigger>
                <TabsTrigger value="weekly">주간</TabsTrigger>
                <TabsTrigger value="monthly">월간</TabsTrigger>
                <TabsTrigger value="yearly">연간</TabsTrigger>
              </TabsList>
            </div>


            <div className="w-full h-[500px]">
              <TradingViewWidget />
            </div>
            차트 부분

            <TabsContent value="daily">
              {/*<ETFChart data={etf.chartData.daily} title="일간 차트" />*/}
            </TabsContent>
            <TabsContent value="weekly">
              {/*<ETFChart data={etf.chartData.weekly} title="주간 차트" />*/}
            </TabsContent>
            <TabsContent value="monthly">
              {/*<ETFChart data={etf.chartData.monthly} title="월간 차트" />*/}
            </TabsContent>
            <TabsContent value="yearly">
              {/*<ETFChart data={etf.chartData.yearly} title="연간 차트" color={etf.change >= 0 ? "#22c55e" : "#ef4444"} />*/}
            </TabsContent>
          </Tabs>
        </div>

        <div className="grid md:grid-cols-2 gap-6 mb-8">
          <Card>
            <CardHeader><CardTitle>ETF 정보</CardTitle></CardHeader>
            <CardContent>
              <div className="space-y-4">
                <p>{etf.description || '상세 정보가 없습니다.'}</p>
                <div className="grid grid-cols-2 gap-4">
                  <div><p className="text-sm text-slate-500">운용사</p><p className="font-medium">{etf.issuer}</p></div>
                  <div><p className="text-sm text-slate-500">상장일</p><p className="font-medium">{etf.launchDate}</p></div>

                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader><CardTitle>구성 종목</CardTitle></CardHeader>
            <CardContent>
              {/* holdings가 존재하고 배열인지 체크 */}
              {etf.holdings && Array.isArray(etf.holdings) && etf.holdings.length > 0 ? (
                  <Table>
                    <TableHeader>
                      <TableRow>
                        <TableHead>종목명</TableHead>
                        <TableHead className="text-right">비중</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {etf.holdings.map((holding, index) => (
                          <TableRow key={index}>
                            <TableCell>{holding.name}</TableCell>
                            <TableCell className="text-right">{holding.weight}%</TableCell>
                          </TableRow>
                      ))}
                    </TableBody>
                  </Table>
              ) : (
                  <div className="text-gray-500">구성 종목 데이터가 없습니다.</div>
              )}
            </CardContent>
          </Card>
        </div>
      </div>
  )
}
