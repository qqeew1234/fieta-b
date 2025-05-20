"""
증권 기사 스크래퍼

이 스크립트는 헤럴드경제 금융 섹션에서 최신 증권 기사를 수집합니다.
각 기사의 제목, 이미지 URL, 원문 링크, 게시일을 수집하고 JSON 형식으로 출력합니다.
"""

import requests
from bs4 import BeautifulSoup
import json
from datetime import datetime
import logging
from typing import Dict, List, Optional, Any
import time
import random
from urllib.parse import urljoin

# 로깅 설정
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s"
)
logger = logging.getLogger("finance_news_scraper")

class FinanceNewsScraper:
    """헤럴드경제 금융 섹션에서 최신 증권 기사를 스크래핑하는 클래스"""

    BASE_URL = "https://biz.heraldcorp.com"
    TARGET_URL = f"{BASE_URL}/finance"

    def __init__(self):
        """스크래퍼 초기화"""
        self.session = requests.Session()
        self.session.headers.update({
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Accept": "text/html,application/xhtml+xml,application/xml",
            "Accept-Language": "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7",
        })

    def fetch_page(self) -> str:
        """웹 페이지 내용 가져오기"""
        try:
            logger.info(f"페이지 요청 중: {self.TARGET_URL}")
            response = self.session.get(self.TARGET_URL, timeout=10)
            response.raise_for_status()
            return response.text
        except requests.RequestException as e:
            logger.error(f"페이지 요청 중 오류 발생: {e}")
            raise

    def parse_articles(self, html_content: str) -> List[Dict[str, Any]]:
        """HTML에서 기사 정보 추출"""
        soup = BeautifulSoup(html_content, "html.parser")
        articles = []

        try:
            news_list = soup.select("ul.news_list > li")
            logger.info(f"발견된 기사 수: {len(news_list)}")

            for article in news_list:
                try:
                    article_data = self._extract_article_data(article)
                    if article_data:
                        articles.append(article_data)
                except Exception as e:
                    logger.warning(f"기사 추출 중 오류: {e}")
                    continue

        except Exception as e:
            logger.error(f"기사 목록 파싱 중 오류: {e}")

        return articles

    def _extract_article_data(self, article_tag) -> Optional[Dict[str, Any]]:
        """개별 기사에서 필요한 데이터 추출"""
        try:
            # 기사 링크 추출
            article_link_tag = article_tag.find("a")
            if not article_link_tag:
                return None

            article_url = article_link_tag.get("href", "")
            if article_url:
                # 상대 경로인 경우 완전한 URL로 변환
                article_url = urljoin(self.BASE_URL, article_url)

            # 제목 추출
            title_tag = article_tag.select_one("p.news_title")
            title = title_tag.get_text(strip=True) if title_tag else "제목 없음"

            # 이미지 URL 추출
            img_tag = article_tag.select_one("div.news_img img")
            image_url = img_tag.get("src", "") if img_tag else None

            # 게시일 추출
            date_tag = article_tag.select_one("span.date")
            published_date = date_tag.get_text(strip=True) if date_tag else "날짜 없음"

            return {
                "title": title,
                "thumbnail_url": image_url,
                "source_url": article_url,
                "published_at": published_date,
                "scraped_at": datetime.now().isoformat()
            }
        except Exception as e:
            logger.warning(f"기사 데이터 추출 중 오류: {e}")
            return None

    def scrape(self) -> List[Dict[str, Any]]:
        """스크래핑 실행 및 결과 반환"""
        try:
            html_content = self.fetch_page()

            # 짧은 지연 시간 추가 (서버 부하 방지)
            time.sleep(random.uniform(1, 3))

            articles = self.parse_articles(html_content)
            logger.info(f"성공적으로 스크래핑된 기사: {len(articles)}개")
            return articles
        except Exception as e:
            logger.error(f"스크래핑 중 오류 발생: {e}")
            return []

    def to_json(self, articles: List[Dict[str, Any]]) -> str:
        """스크래핑 결과를 JSON 문자열로 변환"""
        return json.dumps(articles, ensure_ascii=False, indent=2)

def convert_date_format(date_str: str, add_seconds: str = "00") -> str:
    parsed_date = datetime.strptime(date_str, "%Y.%m.%d %H:%M")
    iso_format = parsed_date.strftime("%Y-%m-%dT%H:%M:") + add_seconds
    return iso_format

def filter_articles_with_dates(articles):
    return [article for article in articles if article.get('published_at') != '날짜 없음']

def main():
    """메인 실행 함수"""
    try:
        scraper = FinanceNewsScraper()
        articles = scraper.scrape()

        if articles:
            articles_with_dates = filter_articles_with_dates(articles)
            logger.info(f"날짜가 있는 기사 수: {len(articles_with_dates)}")
            for article in articles_with_dates:
                try:
                    article["published_at"] = convert_date_format(article["published_at"])
                except ValueError as e:
                    logger.warning(f"날짜 형식 변환 실패: {article['published_at']} - {e}")
            json_data = scraper.to_json(articles_with_dates)

            # 결과 저장 (옵션)
            with open("finance_news.json", "w", encoding="utf-8") as f:
                f.write(json_data)

            print(f"총 {len(articles)}개의 기사가 스크래핑되었습니다.")
        else:
            print("스크래핑된 기사가 없습니다.")

    except Exception as e:
        logger.critical(f"프로그램 실행 중 치명적 오류: {e}")
        print(f"오류 발생: {e}")

if __name__ == "__main__":
    main()