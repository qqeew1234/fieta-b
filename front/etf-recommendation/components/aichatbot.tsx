'use client';

import { useState, useRef } from 'react';
import { MessageCircle } from 'lucide-react'; // 원하는 아이콘 사용
import clsx from 'clsx';
import { aiChat } from '@/lib/api/ai';

interface Message {
  role: 'user' | 'ai.ts';
  content: string;
}

export default function FloatingChatbot() {
  const [open, setOpen] = useState(false);
  const [messages, setMessages] = useState<Message[]>([]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const sendMessage = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!input.trim()) return;

    setMessages((prev) => [...prev, { role: 'user', content: input }]);
    setInput('');
    setLoading(true);

    // 실제 AI API 엔드포인트로 요청
    const { data, error } = await aiChat(input);

    setMessages((prev) => [
      ...prev,
      { role: 'ai.ts', content: data || '답변을 받아오지 못했습니다.' },
    ]);
    setLoading(false);

    setTimeout(() => {
      messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, 100);
  };

  return (
    <>
      {/* 플로팅 버튼 */}
      <button
        className={clsx(
          'fixed z-50 bottom-6 right-6 w-16 h-16 rounded-full shadow-lg flex items-center justify-center bg-slate-900 text-white transition-all',
          open && 'scale-0 pointer-events-none'
        )}
        aria-label="챗봇 열기"
        onClick={() => setOpen(true)}
      >
        <MessageCircle className="w-8 h-8" />
      </button>

      {/* 챗봇 채팅창 */}
      <div
        className={clsx(
          'fixed z-50 bottom-6 right-6 w-80 max-w-[90vw] h-[500px] bg-white rounded-xl shadow-2xl border flex flex-col transition-all',
          open
            ? 'opacity-100 scale-100'
            : 'opacity-0 scale-95 pointer-events-none'
        )}
      >
        {/* 헤더 */}
        <div className="flex items-center justify-between px-4 py-3 border-b bg-slate-900">
          <span className="text-white font-bold">ETF 도우미</span>
          <button
            className="text-white hover:text-gray-200"
            onClick={() => setOpen(false)}
            aria-label="챗봇 닫기"
          >
            ×
          </button>
        </div>
        {/* 메시지 영역 */}
        <div className="flex-1 overflow-y-auto p-4 bg-gray-50">
          {messages.map((msg, idx) => (
            <div
              key={idx}
              className={`mb-2 flex ${
                msg.role === 'user' ? 'justify-end' : 'justify-start'
              }`}
            >
              <div
                className={`px-3 py-2 rounded-lg text-sm ${
                  msg.role === 'user'
                    ? 'bg-blue-500 text-white'
                    : 'bg-gray-200 text-gray-900'
                }`}
              >
                {msg.content}
              </div>
            </div>
          ))}
          <div ref={messagesEndRef} />
        </div>
        {/* 입력창 */}
        <form
          onSubmit={sendMessage}
          className="flex p-3 border-t gap-2 bg-white"
        >
          <input
            type="text"
            value={input}
            className="flex-1 border rounded px-2 py-1 text-sm"
            placeholder="메시지를 입력하세요..."
            onChange={(e) => setInput(e.target.value)}
            disabled={loading}
          />
          <button
            type="submit"
            className="bg-slate-900 text-white px-4 py-1 rounded"
            disabled={loading || !input.trim()}
          >
            전송
          </button>
        </form>
      </div>
    </>
  );
}
