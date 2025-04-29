import requests
from bs4 import BeautifulSoup
import datetime
from urllib.parse import urljoin
import json
import sys

def crawl_herald_news():
    # 경고 메시지 무시 설정
    import warnings
    warnings.filterwarnings('ignore')

    # 헤럴드경제 경제 뉴스 URL
    url = "https://biz.heraldcorp.com/economy"

    # HTTP 요청 헤더 설정
    headers = {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
    }

    try:
        # HTTP GET 요청 보내기
        response = requests.get(url, headers=headers)
        response.encoding = 'utf-8'  # 한글 인코딩 설정

        # 응답이 성공적이지 않으면 예외 발생
        if response.status_code != 200:
            sys.stderr.write(f"HTTP 요청 실패: {response.status_code}\n")
            sys.exit(1)

        # HTML 파싱
        soup = BeautifulSoup(response.text, "html.parser")

        # 뉴스 아이템들 찾기 (main > section > article > ul > li)
        news_items = soup.select("main.div_layout > section.section_list > article.recent_news > ul.news_list > li")

        # 결과를 저장할 리스트
        news_data = []
        count = 0

        # 각 뉴스 아이템 처리
        for item in news_items:
            link_element = item.select_one("a")
            title_element = item.select_one("p.news_title")
            img_element = item.select_one("div.news_img img")
            date_element = item.select_one("span.date")

            if not link_element or not title_element or not img_element:
                continue

            title = title_element.text.strip()
            link = urljoin("https://biz.heraldcorp.com", link_element['href'])
            img_url = img_element['src']
            date = date_element.text.strip() if date_element else datetime.datetime.now().strftime("%Y-%m-%d %H:%M")

            news_data.append({
                "title": title,
                "link": link,
                "img_url": img_url,
                "date": date
            })

            count += 1
            if count >= 10:  # 최대 10개 뉴스 아이템만 수집
                break

        # JSON 형태로 결과 출력 (표준 출력으로만 전송)
        print(json.dumps(news_data, ensure_ascii=False))
        sys.stdout.flush()

    except Exception as e:
        sys.stderr.write(f"오류 발생: {str(e)}\n")
        sys.exit(1)

if __name__ == "__main__":
    crawl_herald_news()