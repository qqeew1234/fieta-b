'use client';

import { useEffect } from 'react';

export default function MarketTickerWidget() {
    useEffect(() => {
        const script = document.createElement('script');
        script.src = 'https://s3.tradingview.com/external-embedding/embed-widget-tickers.js';
        script.async = true;
        script.innerHTML = JSON.stringify({
            symbols: [
                {
                    proName: 'FOREXCOM:SPXUSD',
                    title: 'S&P 500 Index',
                },
                {
                    proName: 'NASDAQ:NDX',
                    title: 'NASDAQ 100',
                },
                {
                    proName: 'KRX:KOSDAQ',
                    title: 'KOSDAQ',
                },
                {
                    proName: 'KRX:KOSPI',
                    title: 'KOSPI',
                },

            ],
            isTransparent: false,
            showSymbolLogo: true,
            colorTheme: 'dark',
            locale: 'kr',
        });

        const container = document.getElementById('tradingview-ticker-widget');
        if (container) container.appendChild(script);
    }, []);

    return (
        <div className="tradingview-widget-container w-full max-w-4xl mx-auto rounded-md shadow-md p-4">
            <div className="tradingview-widget-container__widget" id="tradingview-ticker-widget" />
        </div>
    );
}
