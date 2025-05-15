"use client";
import { Card, CardContent } from "@/components/ui/card";
import Image from "next/image";
import { useEffect, useState } from "react";

// 백엔드에서 받아오는 뉴스 데이터 타입 정의
interface NewsResponse {
    id:number;
    title: string;
    link: string;
    imageUrl: string;
}

// 뉴스 API 호출 함수
async function fetchEconomicNews(): Promise<NewsResponse[]> {
    const response = await fetch("http://localhost:8080/api/v1/news");
    if (!response.ok) {
        throw new Error("뉴스를 불러오는 데 실패했습니다.");
    }
    const rawData = await response.json();

    // 🔧 서버 응답 필드 이름을 클라이언트가 기대하는 형태로 변환
    const mappedData: NewsResponse[] = rawData.map((item: any) => ({
        id:item.id,
        title: item.newsTitle,
        link: item.newsLink,
        imageUrl: item.imageUrl,
    }));

    return mappedData;
}

export default function NewsPage() {
    const [news, setNews] = useState<NewsResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const getNews = async () => {
            try {
                const data = await fetchEconomicNews();
                setNews(data);
            } catch (err) {
                setError("뉴스를 불러오는 데 실패했습니다.");
            } finally {
                setLoading(false);
            }
        };

        getNews();
    }, []);

    if (loading) return <div>로딩 중...</div>;
    if (error) return <div>{error}</div>;

    return (
        <div className="container mx-auto py-8 px-4">
            <h1 className="text-3xl font-bold mb-6">경제 뉴스</h1>
            <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
                {news.map((item) => (
                    <Card
                        key={item.id}
                        className="h-full hover:shadow-md transition-shadow cursor-pointer overflow-hidden">
                        <a
                            href={item.link}
                            target="_blank"
                            rel="noopener noreferrer"
                            className="block h-full"
                        >
                            <div className="relative w-full h-40">
                                <Image
                                    src={item.imageUrl || "/placeholder.svg"}
                                    alt={item.title || "뉴스 이미지"}
                                    fill
                                    className="object-cover"
                                />
                            </div>
                            <CardContent className="p-3">
                                <h3 className="font-medium line-clamp-2 text-sm">{item.title}</h3>
                            </CardContent>
                        </a>
                    </Card>
                ))}
            </div>
        </div>
    );
}
